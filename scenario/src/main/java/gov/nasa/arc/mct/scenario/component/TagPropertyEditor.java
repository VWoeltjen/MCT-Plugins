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
import gov.nasa.arc.mct.components.PropertyEditor;
import gov.nasa.arc.mct.services.component.ComponentTypeInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Property editor for an activity's tags. Expresses 
 * the list of tags currently applied to a component 
 * by probing its children; makes modifications to 
 * this list by adding and removing children.
 */
public class TagPropertyEditor implements PropertyEditor<Map<ComponentTypeInfo, List<AbstractComponent>>> {
	private AbstractComponent component;
	private List<ComponentTypeInfo> capabilities = new ArrayList<ComponentTypeInfo>();
	
	public TagPropertyEditor(AbstractComponent component, Collection<ComponentTypeInfo> capabilities) {
		super();
		this.component = component;
		this.capabilities.addAll(capabilities);
	}

	@Override
	public String getAsText() {
		// Unused
		return "";
	}

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		// Unused
	}

	@Override
	public Map<ComponentTypeInfo, List<AbstractComponent>> getValue() {
		Map<ComponentTypeInfo, List<AbstractComponent>> result = 
				new HashMap<ComponentTypeInfo, List<AbstractComponent>>();
		for (ComponentTypeInfo c : capabilities) {
			List<AbstractComponent> current = 
					new ArrayList<AbstractComponent>();
			for (AbstractComponent child : component.getComponents()) {
				if (c.getTypeClass().isAssignableFrom(child.getClass())) {
					// TODO: Need to find a better way of distinguishing 
					// objects which originate capabilities from objects 
					// which aggregate capabilities.
					// (This is to avoid exposing "Activity" components
					// in tagging UI)
					if (child.isLeaf()) {
						current.add(child);
					}
				}
			}
			result.put(c, current);
		}
		return result;
	}

	@Override
	public void setValue(Object value) throws IllegalArgumentException {
		if (!(value instanceof Map)) {
			throw new IllegalArgumentException("Map expected");
		}
		
		// Aggregate new children
		Map<String, AbstractComponent> newChildren = 
				new LinkedHashMap<String, AbstractComponent>();
		for (Entry<?,?> entry : ((Map<?,?>) value).entrySet()) {
			if (entry.getValue() instanceof List) {
				for (Object element : ((List<?>)entry.getValue())) {			
					if (!(element instanceof AbstractComponent)) {
						throw new IllegalArgumentException("Must provide map of list of AbstractComponents");
					} else {
						AbstractComponent comp = (AbstractComponent) element;
						newChildren.put(comp.getComponentId(), comp);
					}
				}
			} else {
				throw new IllegalArgumentException("Must provide map of list of AbstractComponents");
			}
		}
		
		// Don't bother adding any current children
		// And identify children to remove
		Map<ComponentTypeInfo, List<AbstractComponent>> current = getValue();
		List<AbstractComponent> toRemove = new ArrayList<AbstractComponent>();
		for (List<AbstractComponent> list : current.values()) {
			toRemove.addAll(list);
		}
		Iterator<AbstractComponent> iter = toRemove.iterator();
		while (iter.hasNext()) {
			AbstractComponent next = iter.next();
			if (newChildren.containsKey(next.getComponentId())) {
				iter.remove();
				newChildren.remove(next.getComponentId());
			}
		}
		
		component.removeDelegateComponents(toRemove);
		component.addDelegateComponents(newChildren.values());
	}

	@Override
	public List<Map<ComponentTypeInfo, List<AbstractComponent>>> getTags() {
		// Unused
		return Collections.emptyList();
	}
	
}
