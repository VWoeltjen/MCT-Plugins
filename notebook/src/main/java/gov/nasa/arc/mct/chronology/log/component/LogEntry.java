package gov.nasa.arc.mct.chronology.log.component;

import gov.nasa.arc.mct.chronology.event.ChronologicalEvent;
import gov.nasa.arc.mct.chronology.event.UNIXTimeInstant;
import gov.nasa.arc.mct.chronology.reference.view.ReferenceView;
import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.platform.spi.PlatformAccess;
import gov.nasa.arc.mct.services.activity.TimeService;
import gov.nasa.arc.mct.services.component.ComponentRegistry;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class LogEntry<T> {
	private long   creationTime;
	private T      entry;
	private List<String> references = new ArrayList<String>(); ;
	
	/**
	 * For JAXB unmarshalling
	 */
	public LogEntry() {

	}
	
	public LogEntry(T logEntry, List<AbstractComponent> components) {
		TimeService timeService = PlatformAccess.getPlatform().getTimeService();
		creationTime = (timeService != null) ? timeService.getCurrentTime() : System.currentTimeMillis();
		entry        = logEntry;
		for (AbstractComponent comp : components) {
			references.add(comp.getComponentId());
		}
	}
	
	public long getEntryTime() {
		return creationTime;
	}
	
	public T getEntry() {
		return entry;
	}
	
	public List<String> getReferencedIDs() {
		return references;
	}
	
	public List<AbstractComponent> getReferences() {
		List<AbstractComponent> comps = new ArrayList<AbstractComponent>();
		for (String id : references) {
			AbstractComponent comp = PlatformAccess.getPlatform().getComponentRegistry().getComponent(id);
			if (comp != null) comps.add(comp);
		}
		return comps;
	}
	
	public void setEntryTime(long time) {
		creationTime = time;
	}
	
	public ChronologicalEvent<UNIXTimeInstant> toEvent() {
		return new LogEvent(creationTime);
	}
	
	public boolean canCast(Class<?> c) {
		if (entry == null || c == null) return false;
		else return c.isAssignableFrom(entry.getClass());
	}
	
	private class LogEvent extends ChronologicalEvent<UNIXTimeInstant> {

		public LogEvent(long time) {
			super(new UNIXTimeInstant(time), new UNIXTimeInstant(time));
		}

		
		public JComponent getRepresentation(Dimension d) {
			ComponentRegistry registry = ComponentRegistryAccess.getComponentRegistry();
			
			JPanel panel = new JPanel() {
				private static final long serialVersionUID = -2731173468823398240L;
				public Font getFont() {
					for (Component c : getComponents()) {
						Font f = c.getFont();
						if (f != null) return f;
					}
					return super.getFont();
				}
				public void setFont(Font f) {
					for (Component c : getComponents()) {
						c.setFont(f);
					}
				}
			};
			
			if (d == null) panel.setLayout(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.weightx = 1.0;
			gbc.anchor = GridBagConstraints.WEST;
			JLabel l = new JLabel("<html>" +
					//"<i>" + DATE_FORMAT.format(new Date(getEntryTime())) + "</i>: " +
					getEntry().toString() + //.replaceAll("\n", "\n<br/>") + 
					"</html>");
			//l.setToolTipText(DATE_FORMAT.format(new Date(getEntryTime())));
			if (d != null) {
				JPanel p = new JPanel();
				p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
				p.add(l, BorderLayout.NORTH);
				p.add(Box.createVerticalGlue());
				for (String reference : getReferencedIDs()) {
					AbstractComponent comp = registry.getComponent(reference);
					if (comp != null) {
						View     view = new ReferenceView(comp, ReferenceView.VIEW_INFO);
						view.setAlignmentX(JComponent.LEFT_ALIGNMENT);
						view.setOpaque(false);
						p.add(view);
					}
				}
				return p;
			}
			panel.add(l, gbc);
			
			if (!getReferencedIDs().isEmpty() && d == null) { // TODO: Pick which dimensions to fill
				JPanel referencePanel = new JPanel();
				referencePanel.setLayout(new BoxLayout(referencePanel, BoxLayout.PAGE_AXIS));
				for (String reference : getReferencedIDs()) {
					AbstractComponent comp = registry.getComponent(reference);
					if (comp != null) {
						View     view = new ReferenceView(comp, ReferenceView.VIEW_INFO);
						view.setAlignmentX(JComponent.LEFT_ALIGNMENT);
						referencePanel.add(view);
					}
				}
				
				gbc.gridx = 1;
				gbc.fill = GridBagConstraints.VERTICAL;
				gbc.weightx = 0;
				panel.add(referencePanel, gbc);
			}
			
			return panel;
		}


		@Override
		public Object getEventInfo() {
			return LogEntry.this;
		}
	}
}
