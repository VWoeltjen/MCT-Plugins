package gov.nasa.arc.mct.chronology.timeline.view;

import gov.nasa.arc.mct.chronology.Chronology;
import gov.nasa.arc.mct.chronology.event.ChronologicalEvent;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import gov.nasa.arc.mct.chronology.event.ChronologicalInstant;

public class TimelineTransferable<T extends ChronologicalInstant> implements Transferable {
	public static final DataFlavor EVENT_FLAVOR;
	
	static {
		DataFlavor flavor = null;
		try {
			flavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType) {
				@Override
				public Class<?> getRepresentationClass() {
					return TimelineTransferable.class;
				}
			};
		} catch (ClassNotFoundException cnfe) {
			//TODO log
		}
		EVENT_FLAVOR = flavor;
	};
	
	private static final DataFlavor[] EVENT_FLAVOR_ARRAY = {EVENT_FLAVOR};

	private ChronologicalEvent<T> event;
	private Chronology<T>         source;
	
	public TimelineTransferable(Chronology<T> source,
			ChronologicalEvent<T> event) {
		this.event = event;
		this.source = source;
	}
	
	public ChronologicalEvent<T> getEvent() {
		return event;
	}
	
	public Chronology<T> getChronology() {
		return source;
	}
	
	@Override
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		if (flavor.equals(EVENT_FLAVOR)) {
			return this;
		}
		return null;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return EVENT_FLAVOR_ARRAY;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return flavor.equals(EVENT_FLAVOR);
	}

}
