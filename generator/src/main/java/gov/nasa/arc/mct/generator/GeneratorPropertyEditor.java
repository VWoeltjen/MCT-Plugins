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

import java.util.List;

import gov.nasa.arc.mct.components.PropertyEditor;

public class GeneratorPropertyEditor implements PropertyEditor<String> {
	private final GeneratorModel model;
	
	public GeneratorPropertyEditor(GeneratorModel model) {
		this.model = model;
	}

	@Override
	public String getAsText() {
		return model.getFormula();
	}

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		model.setFormula(text);
	}

	@Override
	public Object getValue() {
		return model.getFormula();
	}

	@Override
	public void setValue(Object value) throws IllegalArgumentException {
		setAsText(value.toString());
	}

	@Override
	public List<String> getTags() {
		return null;
	}

}
