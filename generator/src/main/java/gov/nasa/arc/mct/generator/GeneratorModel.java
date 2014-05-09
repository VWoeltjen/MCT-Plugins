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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Data model for Generator user objects. Contains the expression 
 * used to generate data values. Annotated to support 
 * conversion to/from XML using JAXB, in order to persist 
 * this data to the database. 
 *  
 * @author vwoeltje
 *
 */
@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
public class GeneratorModel {
	private String formula = "";

	/**
	 * Get the mathematical expression used to generate data points  
	 * for this object.
	 * @return the expression used to generate data
	 */
	public String getFormula() {
		// Never return null
		return formula != null ? formula : "";
	}

	/**
	 * Set the mathematical expression used to generate data points  
	 * for this object.
	 * @param formula the expression used to generate data
	 */
	public void setFormula(String formula) {
		// Never set to null
		this.formula = formula != null ? formula : "";
	}
}
