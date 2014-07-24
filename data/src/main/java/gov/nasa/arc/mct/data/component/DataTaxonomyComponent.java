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
package gov.nasa.arc.mct.data.component;

import java.util.concurrent.atomic.AtomicReference;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.Bootstrap;
import gov.nasa.arc.mct.components.JAXBModelStatePersistence;
import gov.nasa.arc.mct.components.ModelStatePersistence;

/**
 * Represent the top level User Component (BootStrap Component) which acts as parent 
 * of DataComponents to form a tree structure
 * @author jdong2
 *
 */
public class DataTaxonomyComponent extends AbstractComponent implements Bootstrap {
	
	private final AtomicReference<DataTaxonomyModel> model = new AtomicReference<DataTaxonomyModel> (new DataTaxonomyModel());
	
	public DataTaxonomyModel getModel() {
		return model.get();
	}

	public void setTimeStamp(String id, String endTime) {		
		model.set(getModel().setTime(id, endTime));		
	}
	
	public Boolean hasTimeStamp(String id) {	
		return getModel().contains(id);
	}
	
	public long getTimeStamp(String id) {
		return getModel().getEndTime(id);
	}


	@Override
	protected <T> T handleGetCapability(Class<T> capability) {
		if (capability.isAssignableFrom(Bootstrap.class)) {
			return capability.cast(this);
		} else if (ModelStatePersistence.class.isAssignableFrom(capability)) { 
			// save additional attributes to database, refers to MCT wiki for details
			JAXBModelStatePersistence<DataTaxonomyModel> persistence = new JAXBModelStatePersistence<DataTaxonomyModel> () {

				@Override
				protected DataTaxonomyModel getStateToPersist() {
					return model.get();
				}

				@Override
				protected void setPersistentState(DataTaxonomyModel modelState) {
					model.set(modelState);
				}

				@Override
				protected Class<DataTaxonomyModel> getJAXBClass() {
					return DataTaxonomyModel.class;
				}
				
			};
			
			return capability.cast(persistence);
		}
		return super.handleGetCapability(capability);
	}
	

	@Override
	public boolean isGlobal() {
		return true;
	}

	@Override
	public boolean isSandbox() {
		return false;
	}

	@Override
	public int categoryIndex() {
		return 0;
	}

	@Override
	public int componentIndex() {
		return 0;
	}
}
