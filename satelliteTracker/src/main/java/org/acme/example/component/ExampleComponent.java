/*******************************************************************************
 * Mission Control Technologies, Copyright (c) 2009-2012, United States Government
 * as represented by the Administrator of the National Aeronautics and Space 
 * Administration. All rights reserved.
 *
 * The MCT platform is licensed under the Apache License, Version 2.0 (the 
 * "License"); you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT 
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the 
 * License for the specific language governing permissions and limitations under 
 * the License.
 *
 * MCT includes source code licensed under additional open source licenses. See 
 * the MCT Open Source Licenses file included with this distribution or the About 
 * MCT Licenses dialog available at runtime from the MCT Help menu for additional 
 * information. 
 *******************************************************************************/
package org.acme.example.component;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.JAXBModelStatePersistence;
import gov.nasa.arc.mct.components.ModelStatePersistence;
import gov.nasa.arc.mct.components.PropertyDescriptor;
import gov.nasa.arc.mct.components.TextInitializer;
import gov.nasa.arc.mct.components.PropertyDescriptor.VisualControlDescriptor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The <code>ExampleComponent</code> class defines a simple component. In this case, <code>AbstractComponent</code> 
 * provides all the functionality required (the default implementation assumes this component can contain
 * an arbitrary component). The no argument constructor is required by the MCT platform. This component will
 * participate in persistence like other MCT components. 
 * @author chris.webster@nasa.gov
 *
 */
public class ExampleComponent extends AbstractComponent {
	private final AtomicReference<ExampleModelRole> model = new AtomicReference<ExampleModelRole>(new ExampleModelRole());

	@Override
	protected <T> T handleGetCapability(Class<T> capability) {
		if (ModelStatePersistence.class.isAssignableFrom(capability)) {
		    JAXBModelStatePersistence<ExampleModelRole> persistence = new JAXBModelStatePersistence<ExampleModelRole>() {

				@Override
				protected ExampleModelRole getStateToPersist() {
					return model.get();
				}

				@Override
				protected void setPersistentState(ExampleModelRole modelState) {
					model.set(modelState);
				}

				@Override
				protected Class<ExampleModelRole> getJAXBClass() {
					return ExampleModelRole.class;
				}
		        
			};
			
			return capability.cast(persistence);
		}
		
		return null;
	}
	
	public ExampleModelRole getModel() {
		return model.get();
	}

	@Override
	public List<PropertyDescriptor> getFieldDescriptors()  {

		// Provide an ordered list of fields to be included in the MCT Platform's InfoView.
		List<PropertyDescriptor> fields = new ArrayList<PropertyDescriptor>();

		// Describe the field "dataDescription" in the business class MyData. 
		// Here we specify an immutable field, whereby its initial value is specified using a convenience class TextInitializer.
		String labelText = "World Swimming Event";
		PropertyDescriptor swimmingEvent = new PropertyDescriptor(labelText, 
				new TextInitializer(getModel().getData().getDataDescription()), VisualControlDescriptor.Label);

		// Describe MyData's field "doubleData". 
		// We specify a mutable text field.  The control display's values are maintained in the business model
		// via the PropertyEditor object.  When a new value is to be set, the editor also validates the prospective value.
		PropertyDescriptor swimmingWorldRecord = new PropertyDescriptor("Men's World Record (Rome 2009 Phelps)", 
				new TextPropertyEditor(this),  VisualControlDescriptor.TextField);
		swimmingWorldRecord.setFieldMutable(true);

		// Describe MyData's field "genderSelection". Here is a mutabl combo box visual control.  The control's initial value, 
		// and its selection states are taken from the business model via the PropertyEditor.
		PropertyDescriptor gender = new PropertyDescriptor("Gender", new EnumerationPropertyEditor(this),  VisualControlDescriptor.ComboBox);
		gender.setFieldMutable(true);

		// Describe MyData's field "verified".  This is a mutable check box visual control. 
		PropertyDescriptor verified = new PropertyDescriptor("Verified", new BooleanPropertyEditor(this),  VisualControlDescriptor.CheckBox);
		verified.setFieldMutable(true);
		
		fields.add(swimmingEvent);
		fields.add(swimmingWorldRecord);
		fields.add(gender);
		fields.add(verified);

		return fields;
	}
}
