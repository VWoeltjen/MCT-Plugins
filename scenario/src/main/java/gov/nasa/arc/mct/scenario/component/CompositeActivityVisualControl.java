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

import gov.nasa.arc.mct.gui.CustomVisualControl;
import gov.nasa.arc.mct.services.component.ComponentTypeInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BoxLayout;

/**
 * Collects multiple custom controls for repository-driven 
 * tagging-style specification of children.
 * 
 * @author vwoeltje
 *
 */
public class CompositeActivityVisualControl extends CustomVisualControl  {
	private static final long serialVersionUID = 1944674986744108472L;
	private Map<Class<?>, CustomVisualControl> controls = 
			new HashMap<Class<?>, CustomVisualControl>();
	
	public CompositeActivityVisualControl(Map<Class<?>, ComponentTypeInfo> capabilities) {
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		for (Entry<Class<?>, ComponentTypeInfo> capability : capabilities.entrySet()) {
			CustomVisualControl control = new ActivityVisualControl(capability.getKey(), capability.getValue());
			add(control);
			controls.put(capability.getKey(), control);
		}
	}
	
	@Override
	public void setValue(Object value) {
		if (value instanceof Map) {
			for (Entry<Class<?>, CustomVisualControl> entry : controls.entrySet()) {
				Object v = ((Map<?,?>) value).get(entry.getKey());
				if (v != null) {
					entry.getValue().setValue(v);
				}
			}
		}
	}

	@Override
	public Object getValue() {
		Map<Class<?>, Object> value = new HashMap<Class<?>, Object>();
		for (Entry<Class<?>, CustomVisualControl> entry : controls.entrySet()) {
			value.put(entry.getKey(), entry.getValue().getValue());
		}
		return value;
	}

	@Override
	public void setMutable(boolean mutable) {
		for (CustomVisualControl control : controls.values()) {
			control.setMutable(mutable);
		}
	}

}
