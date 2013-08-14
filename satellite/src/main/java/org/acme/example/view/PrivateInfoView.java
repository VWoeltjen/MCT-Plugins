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
package org.acme.example.view;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.services.component.ViewInfo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
/**
 * The <code>PrivateInfoViewRole</code> class provides a view exposed in the inspector area, which is
 * the right hand side of a window. This view provides information only when the example component is 
 * <em>private</em>. When an <code>AbstractComponent</code> is created, its default visibility is 
 * <em>private</em>. An <code>AbstractComponent</code> becomes <em>public</em> when it is dropped to
 * a dropbox.
 * 
 * @author nija.shi@nasa.gov
 */
public final class PrivateInfoView extends View {
	
	public PrivateInfoView(AbstractComponent comp, ViewInfo vi) {
		super(comp,vi);
		
		JPanel view = new JPanel();
		view.setLayout(new BoxLayout(view, BoxLayout.Y_AXIS));
		
		// Add the header for this view manifestation.
		view.add(createHeaderRow("Information available only for the owner", Color.red, 15)); //NOI18N
		view.add(new JLabel());
		
		// Add the content for this view manifestation.
		AbstractComponent component = getManifestedComponent();
		view.add(createMultiLabelRow("Display name of this component: ", component.getDisplayName())); //NOI18N
		view.add(createMultiLabelRow("Owner of this component: ", component.getOwner())); //NOI18N
		
		setLayout(new BorderLayout());
		add(view, BorderLayout.NORTH);
	}
	
	// The following are the utility methods for formatting this 
	// info view manifestation.
	
	// Creates a formatted JPanel that contains the header in a JLabel
	private JPanel createHeaderRow(String title, Color color, float size) {
		JPanel panel = new JPanel(new BorderLayout());
		JLabel label = new JLabel(title);
		label.setFont(label.getFont().deriveFont(Font.BOLD, size));
		label.setForeground(color);
		panel.add(label, BorderLayout.WEST);
		return panel;
	}
	
	// Creates a formatted JPanel that contains two JLabels: 
	// the field name and the field value.
	private JPanel createMultiLabelRow(String fieldName, String text) {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		JLabel field = new JLabel(fieldName);
		field.setFont(field.getFont().deriveFont(Font.BOLD));
		
		JLabel value = new JLabel(text);
		value.setFont(field.getFont().deriveFont(Font.BOLD));
		value.setForeground(Color.gray);
		
		panel.add(field, BorderLayout.WEST);
		panel.add(value, BorderLayout.CENTER);
		panel.setBounds(new Rectangle(field.getWidth() + value.getWidth(), 10));
		return panel;
	}
}