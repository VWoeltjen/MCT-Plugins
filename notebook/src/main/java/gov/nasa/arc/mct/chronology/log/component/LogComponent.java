package gov.nasa.arc.mct.chronology.log.component;

import gov.nasa.arc.mct.chronology.Chronology;
import gov.nasa.arc.mct.chronology.ChronologyDomain;
import gov.nasa.arc.mct.chronology.event.ChronologicalEvent;
import gov.nasa.arc.mct.chronology.event.ChronologicalInterval;
import gov.nasa.arc.mct.chronology.event.UNIXTimeInstant;
import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.platform.spi.PlatformAccess;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

public abstract class LogComponent<T> extends AbstractComponent {
	private static final Comparator<AbstractComponent> COMPARATOR =
		new LogEntryComponentComparator();
	
	
	protected <C> C handleGetCapability(Class<C> capability) {
		
		if (Chronology.class.isAssignableFrom(capability)) {
			final List<ChronologicalEvent<UNIXTimeInstant>> events = 
				new ArrayList<ChronologicalEvent<UNIXTimeInstant>>();

			Class<? extends LogEntryComponent<T>> entryClass =
				getEntryComponentClass();
			for (AbstractComponent ac : getComponents()) {
				if (entryClass.isAssignableFrom(ac.getClass())) {
					LogEntryComponent<T> child = entryClass.cast(ac);
					LogEntry<T> entry = child.getEntry();
					events.add(new ClickableEvent(entry.toEvent(), child));
				}
			}
			Collections.sort(events, new Comparator<ChronologicalEvent<UNIXTimeInstant>>() {
				@Override
				public int compare(ChronologicalEvent<UNIXTimeInstant> a,
						ChronologicalEvent<UNIXTimeInstant> b) {
					return UNIXTimeInstant.DOMAIN.getComparator().compare(a.getStart(), b.getStart());
				}
			});
			return capability.cast(new Chronology<UNIXTimeInstant>() {
				@Override
				public List<ChronologicalEvent<UNIXTimeInstant>> getEvents(
						ChronologicalInterval<UNIXTimeInstant> interval) {
					return events;
				}

				@Override
				public List<ChronologicalEvent<UNIXTimeInstant>> getEvents() {
					return events;
				}

				@Override
				public ChronologyDomain<UNIXTimeInstant> getDomain() {
					return UNIXTimeInstant.DOMAIN;
				}

				@Override
				public boolean isMutable() {
					return true; //TODO: Policy?
				}

				@Override
				public boolean add(ChronologicalEvent<UNIXTimeInstant> event,
						UNIXTimeInstant start) {
					Object eventInfo = event.getEventInfo();
					if (!(eventInfo instanceof LogEntry)) {
						return false;
					}
					
					LogEntry<?> entry = (LogEntry<?>) eventInfo;
					
					@SuppressWarnings("unchecked")
					LogEntryComponent<T> child =
						addEntry((T) entry.getEntry(), entry.getReferences());
					child.getEntry().setEntryTime(start.getTimeMillis());
					child.setDisplayName(start.toString());
					
					PlatformAccess.getPlatform().getPersistenceProvider().startRelatedOperations();
					child.save();
					PlatformAccess.getPlatform().getPersistenceProvider().completeRelatedOperations(true);
					
					return true; //TODO: Add.
				}

				@Override
				public boolean remove(ChronologicalEvent<UNIXTimeInstant> event) {
					AbstractComponent eventComponent = null;
					Object eventInfo = event.getEventInfo();
					for (AbstractComponent child : getComponents()) {
						if (child instanceof LogEntryComponent) {
							LogEntry<?> e = ((LogEntryComponent<?>) child).getEntry();
							if (e == eventInfo) {
								eventComponent = child;
							}
						}
					}
					if (eventComponent != null) {
						PlatformAccess.getPlatform().getPersistenceProvider().startRelatedOperations();
						removeDelegateComponent(eventComponent);
						save();
						PlatformAccess.getPlatform().getPersistenceProvider().completeRelatedOperations(true);
					}
					return false; 
				}

			});
		}
		
		return null;
	}
	
	public boolean isLeaf() {
		return false;
	}
	
	public LogEntryComponent<T> addEntry(T entry, List<AbstractComponent> components) {
		PlatformAccess.getPlatform().getPersistenceProvider().startRelatedOperations();
		LogEntryComponent<T> child;
		child = PlatformAccess.getPlatform().getComponentRegistry().newInstance(getEntryComponentClass(), this); //getEntryComponentClass().newInstance();
		child.initialize(entry, components);
		child.setDisplayName(new UNIXTimeInstant(child.getEntry().getEntryTime()).toString());
		child.save();
		//PlatformAccess.getPlatform().getPersistenceProvider().persist(Collections.<AbstractComponent>singleton(child));
		PlatformAccess.getPlatform().getPersistenceProvider().completeRelatedOperations(true);
		// TODO: should not actually persist until commit!
		return child;
	}
	
	public abstract Class<? extends LogEntryComponent<T>> getEntryComponentClass() ;
	
	private class ClickableEvent extends ChronologicalEvent<UNIXTimeInstant>{
		private ChronologicalEvent<UNIXTimeInstant> clickableEvent;
		private AbstractComponent component;
		
		public ClickableEvent(ChronologicalEvent<UNIXTimeInstant> event, AbstractComponent comp) {
			super(event.getStart(), event.getEnd());
			clickableEvent = event;
			component      = comp;
		}
		
		public Object getEventInfo() {
			return clickableEvent.getEventInfo();
		}

		public JComponent getRepresentation(Dimension preferredSize) {
			JComponent jc = clickableEvent.getRepresentation(preferredSize);
			jc.addMouseListener(new MouseListener() {

				@Override
				public void mouseClicked(MouseEvent evt) {
					if (SwingUtilities.isLeftMouseButton(evt) && evt.getClickCount() == 2) {
						component.open();
					} // TODO: Popup menu?
					
				}

				@Override
				public void mouseEntered(MouseEvent e) {

				}

				@Override
				public void mouseExited(MouseEvent e) {

				}

				@Override
				public void mousePressed(MouseEvent e) {
					
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					
				}
				
			});
			return jc;
		}

		public UNIXTimeInstant getStart() {
			return clickableEvent.getStart();
		}

		public UNIXTimeInstant getEnd() {
			return clickableEvent.getEnd();
		}
		
	}

	@Override
	protected void addDelegateComponentsCallback(
			Collection<AbstractComponent> childComponents) {
		List<AbstractComponent> components = new ArrayList<AbstractComponent>();
		components.addAll(getComponents());
		Collections.sort(components, COMPARATOR);
		boolean sorted = true;
		for (int i = 0; i < components.size(); i++) {
			if (components.get(i) != getComponents().get(i)) {
				sorted = false;
				break;
			}
		}
		if (!sorted) addDelegateComponents(components); 
		save();
	}

	
	
	private static class LogEntryComponentComparator implements Comparator<AbstractComponent> {

		@Override
		public int compare(AbstractComponent o1, AbstractComponent o2) {
			if (o1 instanceof LogEntryComponent) {
				if (o2 instanceof LogEntryComponent) {
					long t1 = ((LogEntryComponent<?>) o1).getEntry().getEntryTime();
					long t2 = ((LogEntryComponent<?>) o2).getEntry().getEntryTime();
					return Long.signum(t1 - t2);
				} else {
					return -1; // Log entry components come first!
				}
			} else {
				if (o2 instanceof LogEntryComponent) {
					return 1;
				} else {
					return 0; //Both not log entries
				}
			}
		}
		
	}
	
}
