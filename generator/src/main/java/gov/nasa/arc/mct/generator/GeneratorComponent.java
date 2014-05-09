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
package gov.nasa.arc.mct.generator;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.FeedProvider;
import gov.nasa.arc.mct.components.JAXBModelStatePersistence;
import gov.nasa.arc.mct.components.ModelStatePersistence;
import gov.nasa.arc.mct.components.PropertyDescriptor;
import gov.nasa.arc.mct.components.PropertyDescriptor.VisualControlDescriptor;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Creatable user object which generates data based on some user-defined function.
 *   
 * The expression used to generate data may be defined in the object's Info View
 * as a function of t (time, in seconds, since UNIX epoch.)
 * 
 * @author vwoeltje
 *
 */
public class GeneratorComponent extends AbstractComponent {
	/**
	 * Data model for this component. Stored in an atomic reference, 
	 * as threads which use the object may be separate from threads 
	 * which load the object's data.
	 */
	private AtomicReference<GeneratorModel> model = 
			new AtomicReference<GeneratorModel>(new GeneratorModel());
	
	@Override
	public <T> T handleGetCapability(Class<T> capability) {
		// Support persistence (so that changes made to data model 
		// can be saved to database.)
		// JAXB is used to convert this data model to and from a 
		// string; GeneratorModel has been annotated for this purpose.
		if (capability.isAssignableFrom(ModelStatePersistence.class)) {
			JAXBModelStatePersistence<GeneratorModel> persistence = 
					new JAXBModelStatePersistence<GeneratorModel>() {
				@Override
				protected GeneratorModel getStateToPersist() {
					return model.get();
				}

				@Override
				protected void setPersistentState(GeneratorModel modelState) {
					model.set(modelState);
				}

				@Override
				protected Class<GeneratorModel> getJAXBClass() {
					return GeneratorModel.class;
				}
			};
			return capability.cast(persistence);
		}
		
		// Support reading data feeds associated with this object. 
		// This capability is used by feed-driven views such as 
		// plots and tables to identify which data feed should be 
		// used, and how it should be displayed.
		if (capability.isAssignableFrom(FeedProvider.class)) {
			try {
				// Only provide a feed if a valid expression is defined
				String expression = model.get().getFormula();
				if (expression != null) {
					FeedProvider fp = new GeneratorFeedProvider(expression);
					return capability.cast(fp);
				}	
			} catch (IllegalArgumentException iae) {
				// Don't provide feeds if data is bad...
				return null;
			}				
		}	
		
		return null;
	}
	
	@Override
	public boolean isLeaf() {
		// Don't allow this object to be used as a container for other objects.
		return true;
	}

	@Override
	public List<PropertyDescriptor> getFieldDescriptors() {
		// Expose an editable field for the expression which drives data generation.
		// This will be used to populate the UI in the Info view.
		PropertyDescriptor formulaDescriptor = new PropertyDescriptor(
				"Formula f(t)", 
				new GeneratorPropertyEditor(model.get()), 
				VisualControlDescriptor.TextField);
		formulaDescriptor.setFieldMutable(true);
		
		return Arrays.asList(formulaDescriptor);
	}
	
	
}
