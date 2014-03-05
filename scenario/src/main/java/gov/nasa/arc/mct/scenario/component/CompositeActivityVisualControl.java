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
import gov.nasa.arc.mct.gui.CustomVisualControl;
import gov.nasa.arc.mct.scenario.component.ActivityComponent.ActivityCustomProperty;
import gov.nasa.arc.mct.services.component.ComponentTypeInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Collects multiple custom controls for repository-driven 
 * tagging-style specification of children.
 * 
 * @author vwoeltje
 *
 */
public class CompositeActivityVisualControl extends CustomVisualControl implements ChangeListener {
	private static final long serialVersionUID = 1944674986744108472L;
	private Map<ComponentTypeInfo, CustomVisualControl> controls = 
			new HashMap<ComponentTypeInfo, CustomVisualControl>();
	private CustomVisualControl linkControl;
	
	public CompositeActivityVisualControl(Map<ComponentTypeInfo, List<AbstractComponent>> capabilities) {
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		for (Entry<ComponentTypeInfo, List<AbstractComponent>> capability : capabilities.entrySet()) {
			CustomVisualControl control = addControl(
					new ActivityVisualControl(capability.getKey(), capability.getValue()),
					capability.getKey().getDisplayName() + "s");
			controls.put(capability.getKey(), control);
		}
		
		linkControl = addControl(new CompositeUrlControl(
									new LinkVisualControl("Activity Description:"),
									new LinkVisualControl("Procedure")
								), "External Link");
	}
	
	private CustomVisualControl addControl(CustomVisualControl control, String title) {
		control.addChangeListener(this);
		TitledBorder border = BorderFactory.createTitledBorder(title);
		border.setTitleColor(new JLabel().getForeground());
		control.setBorder(border);
		add(control);		
		return control;
	}
	
	@Override
	public void setValue(Object value) {
		if (value instanceof ActivityCustomProperty) {
			ActivityCustomProperty property = 
					((ActivityCustomProperty) value);
			Map<ComponentTypeInfo, List<AbstractComponent>> map = 
					property.getTagStyleChildren();
			for (Entry<ComponentTypeInfo, CustomVisualControl> entry : controls.entrySet()) {
				Object v = map.get(entry.getKey());
				if (v != null) {
					entry.getValue().setValue(v);
				}
			}
			
			linkControl.setValue(Arrays.asList(property.getUrl(), property.getProcedureUrl()));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getValue() {
		Map<ComponentTypeInfo, List<AbstractComponent>> value = 
				new HashMap<ComponentTypeInfo, List<AbstractComponent>>();
		for (Entry<ComponentTypeInfo, CustomVisualControl> entry : controls.entrySet()) {
			value.put(entry.getKey(), 
					(List<AbstractComponent>) entry.getValue().getValue());
		}
		List<?> links = (List<?>) linkControl.getValue();
		return new ActivityCustomProperty(value, 
				links.get(0).toString(),
				links.get(1).toString());
	}

	@Override
	public void setMutable(boolean mutable) {
		for (CustomVisualControl control : controls.values()) {
			control.setMutable(mutable);
		}
		linkControl.setMutable(mutable);
	}

	@Override
	public void stateChanged(ChangeEvent evt) {
		fireChange();
	}

	private class CompositeUrlControl extends CustomVisualControl {
		private static final long serialVersionUID = -5628643314386458704L;

		private List<CustomVisualControl> urlControls = 
				new ArrayList<CustomVisualControl>();
		
		public CompositeUrlControl(CustomVisualControl... urlControls) {
			Collections.addAll(this.urlControls, urlControls);
			
			setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
			for (CustomVisualControl control : urlControls) {
				add(control);
				control.addChangeListener(CompositeActivityVisualControl.this);
			}
		}

		@Override
		public void setValue(Object value) {
			if (value instanceof List) {
				List<?> list = (List<?>) value;
				for (int i = 0; i < Math.min(list.size(), urlControls.size()); i++) {
					urlControls.get(i).setValue(list.get(i));
				}
			}
		}

		@Override
		public Object getValue() {
			List<Object> value = new ArrayList<Object>();
			for (CustomVisualControl control : urlControls) {
				value.add(control.getValue().toString());
			}
			return value;
		}

		@Override
		public void setMutable(boolean mutable) {
			for (CustomVisualControl control : urlControls) {
				control.setMutable(mutable);
			}
		}		
	}
	
}
