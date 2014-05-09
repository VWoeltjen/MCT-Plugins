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
import gov.nasa.arc.mct.components.JAXBModelStatePersistence;
import gov.nasa.arc.mct.components.ModelStatePersistence;
import gov.nasa.arc.mct.components.PropertyDescriptor;
import gov.nasa.arc.mct.components.PropertyDescriptor.VisualControlDescriptor;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class GeneratorComponent extends AbstractComponent {
	private AtomicReference<GeneratorModel> model = new AtomicReference<GeneratorModel>(new GeneratorModel());
	
	@Override
	public <T> T handleGetCapability(Class<T> capability) {
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
		return null;
	}
	
	@Override
	public boolean isLeaf() {
		return true;
	}

	@Override
	public List<PropertyDescriptor> getFieldDescriptors() {
		PropertyDescriptor formulaDescriptor = new PropertyDescriptor(
				"Formula", 
				new GeneratorPropertyEditor(model.get()), 
				VisualControlDescriptor.TextField);
		formulaDescriptor.setFieldMutable(true);

		
		
		return Arrays.asList(formulaDescriptor);
	}
	
	
}
