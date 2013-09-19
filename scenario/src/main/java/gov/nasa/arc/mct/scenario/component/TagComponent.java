package gov.nasa.arc.mct.scenario.component;

import gov.nasa.arc.mct.components.AbstractComponent;

/**
 * A tag component represents a type or similar, 
 * typically associated with some activity. 
 * @author vwoeltje
 *
 */
public class TagComponent extends AbstractComponent {
	/**
	 * Create a new tag. 
	 */
	public TagComponent() {
		
	}
	
	@Override
	public boolean isLeaf() {
		return true;
	}
}
