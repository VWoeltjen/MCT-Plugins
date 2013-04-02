package gov.nasa.arc.mct.scenario.component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The <code>ExampleModelRole</code> provides a model.  This model can contain an arbitrary complex data structure. 
 * The data contents of the model are persisted by marshalling the model into XML text. 
 * See JavaTM Architecture for XML Binding Reference Implementation (RI) Version: 1.0.5. 
 * 
 * The JAXB annotations specify which parts of the model are persisted.  The XmlRootElement is required, as
 * it specifies the top level java binding to the root XML element.
 * 
 * In this example XmlAccessorType specifies that all fields (public or private) should be marshalled (ie serialized).
 * 
 * @author chris.webster@nasa.gov
 *
 */
@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
public class ActivityModelRole {
	
	// This is the model data. 
	// You can choose whether or not your model data will be persisted using setPersistable().
	//
	// In this example, we will persist data by setting persistable to true, and annotating our model role.
	// The View Model Role associated with this component allows a user to modify and save the data. When the user commits the
	// change, MCT persists model data using JAXB.  The XML text written to the MCT database is similar to 
	// <exampleModelRole><data><doubleData>46.91</doubleData><dataDescription>100 free</dataDescription></data></exampleModelRole>
	
	private ActivityData data = new ActivityData();

	public ActivityData getData() {
		return data;
	}
}
