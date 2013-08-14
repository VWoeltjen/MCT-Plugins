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
package org.acme.example.telemetry;

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
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TelemetryModel {
	
	// This is the model data. 
	// You can choose whether or not your model data will be persisted using setPersistable().
	//
	// In this example, we will persist data by setting persistable to true, and annotating our model role.
	// The View associated with this component allows a user to modify and save the data. When the user commits the
	// change, MCT persists model data using JAXB.  The XML text written to the MCT database is similar to 
	// <exampleModelRole><data><doubleData>46.91</doubleData><dataDescription>100 free</dataDescription></data></exampleModelRole>
	
	private TelemetryData data = new TelemetryData ();

	public TelemetryData getData() {
		return data;
	}
}

