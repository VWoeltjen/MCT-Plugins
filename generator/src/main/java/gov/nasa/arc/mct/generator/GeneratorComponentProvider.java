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

import java.util.Collection;
import java.util.Collections;

import gov.nasa.arc.mct.services.component.AbstractComponentProvider;
import gov.nasa.arc.mct.services.component.ComponentTypeInfo;

/**
 * Exposes Generator components to MCT. Provides an implementation of 
 * ComponentProvider, which will be recognized by the platform by way 
 * of OSGi's Declarative Services. 
 * 
 * See OSGI-INF/services.xml for related declarations.
 * 
 * @author vwoeltje
 *
 */
public class GeneratorComponentProvider extends AbstractComponentProvider {
	
	/**
	 * Provides a description of the Generator object type; used by 
	 * the platform to instantiate new instances of this type, to populate 
	 * the Create menu, etc.
	 */
	private final ComponentTypeInfo generatorTypeInfo = new ComponentTypeInfo(
			"Generator",
			"Generates data",
			GeneratorComponent.class,
			true
			);

	@Override
	public Collection<ComponentTypeInfo> getComponentTypes() {
		// Expose the Generator component to the platform 
		return Collections.singleton(generatorTypeInfo);
	}
	
}
