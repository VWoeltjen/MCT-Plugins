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
import gov.nasa.arc.mct.gui.SelectionProvider;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.roles.events.AddChildEvent;
import gov.nasa.arc.mct.roles.events.RemoveChildEvent;
import gov.nasa.arc.mct.services.component.ViewInfo;
import gov.nasa.arc.mct.services.component.ViewType;

import java.awt.BorderLayout;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Example of a center pane view that integrates with the selection infrastructure. The 
 * {@link #getSelectionProvider()} method is overridden to notify MCT that this panel emits selections. 
 *
 */
@SuppressWarnings("serial")
public final class CenterPanePanel extends View implements
SelectionProvider {
	private static ResourceBundle bundle = ResourceBundle.getBundle("ExampleResourceBundle"); //NOI18N 
	
	private final JList list; // list representing the selected element
	private final CenterPanePanel.CenterPaneListModel model;
	
	public CenterPanePanel(AbstractComponent ac, ViewInfo vi) {
		super(ac,vi);
		
		setLayout(new BorderLayout());
		list = new JList(model = new CenterPaneListModel(getManifestedComponent()));
		list.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		add(list,BorderLayout.CENTER);
		// the list has changed, so fire a selection event so that interested listeners
		// can respond
		list.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				firePropertyChange(SelectionProvider.SELECTION_CHANGED_PROP, null, getSelectedManifestations());
			}
			
		});
	}
	
	@Override
	public void updateMonitoredGUI(AddChildEvent event) {
		// a new child has been added, notify the list view
		listChanged();
	}
	
	@Override
	public void updateMonitoredGUI(RemoveChildEvent event) {
		// a child has been removed, notify the list view
		listChanged();
	}
	
	@Override
	public SelectionProvider getSelectionProvider() {
		return this;
	}
	
	private void listChanged() {
		for (ListDataListener listener : model.getListDataListeners()) {
			listener.contentsChanged(new ListDataEvent(list, ListDataEvent.CONTENTS_CHANGED,0,model.getSize()));
		}
	}
	
	/**
	 * A simple list model for displaying the children of a component.
	 */
	private static class CenterPaneListModel extends AbstractListModel {
		private final AbstractComponent component;
		
		public CenterPaneListModel(AbstractComponent aComponent) {
			component = aComponent;
		}
		
		@Override
		public Object getElementAt(int index) {
			return component.getComponents().get(index).getDisplayName();
		}

		@Override
		public int getSize() {
			return component.getComponents().size();
		}
		
	}

	@Override
	public void addSelectionChangeListener(PropertyChangeListener listener) {
		addPropertyChangeListener(SelectionProvider.SELECTION_CHANGED_PROP, listener);
	}

	@Override
	public void clearCurrentSelections() {
		list.clearSelection();
	}

	@Override
	public Collection<View> getSelectedManifestations() {
		int[] selected = list.getSelectedIndices();
		// iterate over the list of selected items in the list and create manifestations
		List<View> selections = new ArrayList<View>(selected.length);
		List<AbstractComponent> children = getManifestedComponent().getComponents();
		for (int selectedIndex:selected) {
			AbstractComponent comp = children.get(selectedIndex);
			Set<ViewInfo> views = comp.getViewInfos(ViewType.NODE);
			// there should always be a node view for a component
			ViewInfo nodeView = views.iterator().next();
			selections.add(nodeView.createView(comp));
		}
		
		return selections;
	}

	@Override
	public void removeSelectionChangeListener(
			PropertyChangeListener listener) {
		removePropertyChangeListener(SelectionProvider.SELECTION_CHANGED_PROP, listener);
	}
	
}