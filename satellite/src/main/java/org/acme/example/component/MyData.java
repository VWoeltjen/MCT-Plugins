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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * MCT will be able to share any class that has the jaxb {@link javax.xml.bind.annotation.XmlRootElement} or {@link javax.xml.bind.annotation.XmlAccessorType} class annotation. Another
 * option is to use the {@link gov.nasa.arc.mct.services.annotation.Share} class annotation. Using the jaxb annotations will have an
 * additional effect of serializing objects of the class to the model_state table. Using the {@link gov.nasa.arc.mct.services.annotation.Share}
 * annotation will only enable objects of the class to be shareable. The objects will not get serialized to the model_state table.
 * 
 * It is important to note that any field that is marked as transient will not be shared. The class definition should be able to initialize
 * any transient fields properly. Further note that, currently, MCT will not share any field that is of type defined in an external library.
 * Therefore, any such field should be marked as a transient field. 
 * 
 * MCT will add synchronized modifier to any non-private methods for any classes that are annotated with the jaxb {@link javax.xml.bind.annotation.XmlRootElement} or
 * {@link javax.xml.bind.annotation.XmlAccessorType} annotations or any classes that are annotated with the {@link gov.nasa.arc.mct.services.annotation.Share}
 * annotation. This will enable multiple MCT instances to modify and/or access the same share instance correctly. Therefore, one should
 * write the class as if it is only accessed by a single thread.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class MyData {
	
	private double doubleData = 49.82; //seconds
	private String dataDescription = "100m fly"; // World record men 100m fly (Rome 2009) Phelps
    final String[] gender = {"men", "women"};
    private String genderSelection = gender[1];
    private boolean verified = false;
    
	public double getDoubleData() {
		return doubleData;
	}

	public void setDoubleData(double data) {
		this.doubleData = data;
	}
	
	public String getDataDescription() {
		return dataDescription;
	}

	public void setDataDescription(String dataDescription) {
		this.dataDescription = dataDescription;
	}

	public String[] getGender() {
		return gender;
	}

	public String getGenderSelection() {
		return genderSelection;
	}

	public void setGenderSelection(String genderSelection) {
		this.genderSelection = genderSelection;
	}

	public boolean isVerified() {
		return verified;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
	}
}