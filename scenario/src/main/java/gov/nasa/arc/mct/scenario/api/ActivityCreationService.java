package gov.nasa.arc.mct.scenario.api;

import gov.nasa.arc.mct.components.AbstractComponent;

public interface ActivityCreationService {

	AbstractComponent createActivity( 
            AbstractComponent parent);
}
