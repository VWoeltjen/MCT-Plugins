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

import gov.nasa.arc.mct.components.FeedFilterProvider;
import gov.nasa.arc.mct.components.FeedProvider;

import java.text.ParseException;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class GeneratorFeedFilterProvider implements FeedFilterProvider {
	private static final FeedFilterProvider INSTANCE = new GeneratorFeedFilterProvider(); 
	
	private GeneratorFeedFilterProvider() {
		
	}
	
	public static FeedFilterProvider getInstance() {
		return INSTANCE;
	}

	@Override
	public FeedFilterEditor createEditor() {
		return new GeneratorFilterEditor();
	}

	@Override
	public FeedFilter createFilter(String definition)
			throws ParseException {					
		try {
			return new GeneratorFilter(Double.parseDouble(definition));
		} catch (NumberFormatException e) {
			throw new ParseException("", 0);	
		}
	}
	
	private static class GeneratorFilter implements FeedFilter {
		private double plateau;

		public GeneratorFilter(double plateau) {
			super();
			this.plateau = plateau;
		}
		
		@Override
		public boolean accept(Map<String, String> datum) {
			String value = datum.get(FeedProvider.NORMALIZED_VALUE_KEY);
			if (value == null) {
				return false;
			}
			try {
				return Double.parseDouble(value) > plateau;
			} catch (NumberFormatException nfe) {
				return false;
			}
		}
	}
	
	private static class GeneratorFilterEditor extends JPanel implements FeedFilterEditor {
		private static final long serialVersionUID = -4169349927106091689L;
		private Runnable listener = null;
		private JTextField textField = new JTextField("0");
		
		public GeneratorFilterEditor() {
			textField.setColumns(4);
			add(textField);
			textField.getDocument().addDocumentListener(new DocumentListener() {
				@Override
				public void changedUpdate(DocumentEvent arg0) {
					fireListener();
				}

				@Override
				public void insertUpdate(DocumentEvent arg0) {
					fireListener();
				}

				@Override
				public void removeUpdate(DocumentEvent arg0) {
					fireListener();
				}								
			});
		}
		
		@Override
		public String setFilterDefinition(String definition)
				throws ParseException {
			if (definition != null && definition.length() > 0) {
				textField.setText(definition);
			}
			return textField.getText();
		}

		@Override
		public String getFilterDefinition() {
			return textField.getText();
		}

		@Override
		public <T> T getUI(Class<T> uiComponentClass,
				Runnable listener) {
			this.listener = listener;
			return uiComponentClass.isAssignableFrom(JComponent.class) ? 
					uiComponentClass.cast(this) : null;
		}
		
		private void fireListener() {
			if (listener != null) {
				listener.run();
			}
		}
	}


}
