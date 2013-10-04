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
package gov.nasa.arc.mct.scenario.component;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.JAXBModelStatePersistence;
import gov.nasa.arc.mct.components.ModelStatePersistence;

import java.util.concurrent.atomic.AtomicReference;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

public class TagRepositoryComponent extends AbstractComponent implements RepositoryCapability {
	private AtomicReference<TagRepositoryModel> model =
			new AtomicReference<TagRepositoryModel>(new TagRepositoryModel());

	@Override
	public <T> T handleGetCapability(Class<T> capabilityClass) {
		return (capabilityClass.isAssignableFrom(ModelStatePersistence.class)) ?
			capabilityClass.cast(persistence) :
			super.handleGetCapability(capabilityClass);
	}
	
	@Override
	public Class<?> getCapabilityClass() {
		return TagComponent.class;
	}

	@Override
	public String getUserScope() {
		return model.get().scope;
	}
	
	public void setUserScope(String scope) {
		model.get().scope = scope;
	}
	
	@XmlRootElement
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class TagRepositoryModel {
		private String scope = "";
	}
	
	private final ModelStatePersistence persistence = 
			new JAXBModelStatePersistence<TagRepositoryModel>() {
		@Override
		protected TagRepositoryModel getStateToPersist() {
			return model.get();
		}

		@Override
		protected void setPersistentState(TagRepositoryModel modelState) {
			model.set(modelState);
		}

		@Override
		protected Class<TagRepositoryModel> getJAXBClass() {
			return TagRepositoryModel.class;
		}		
	};
}