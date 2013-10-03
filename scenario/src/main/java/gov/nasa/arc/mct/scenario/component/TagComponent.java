package gov.nasa.arc.mct.scenario.component;

import gov.nasa.arc.mct.components.AbstractComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * A tag component represents a type or similar, 
 * typically associated with some activity. 
 * @author vwoeltje
 *
 */
public class TagComponent extends AbstractComponent implements TagCapability {
	private List<TagCapability> tags = new ArrayList<TagCapability>();
	
	/**
	 * Create a new tag. 
	 */
	public TagComponent() {
		tags.add(this);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected <T> List<T> handleGetCapabilities(Class<T> capability) {
		if (capability.isAssignableFrom(TagCapability.class)) {
			return ((List<T>) tags);
		}
		return super.handleGetCapabilities(capability);
	}

	@Override
	public boolean isLeaf() {
		return true;
	}

	@Override
	public String getTag() {
		return getDisplayName();
	}

	@Override
	public AbstractComponent getComponentRepresentation() {
		return this;
	}
	
}
