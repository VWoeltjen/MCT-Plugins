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
package gov.nasa.arc.mct.data.action;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.gui.ActionContext;
import gov.nasa.arc.mct.gui.ContextAwareAction;
import gov.nasa.arc.mct.gui.FileChooser;
import gov.nasa.arc.mct.gui.View;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.JFileChooser;

/**
 * Represents the "Data > Import" action, used to import 
 * CSV (comma-separated value) format files into MCT. 
 * 
 * As the implementation for this action varies depending 
 * on whether it is accessed via the This menu or the 
 * Objects menu, this is simply an abstract superclass; 
 * implementations for those menus are provided as static 
 * inner classes.
 * 
 * @author jdong
 *
 */
public class DataImportAction extends ContextAwareAction {
	private static final long serialVersionUID = 1364579701311592635L;

	public DataImportAction() {
		super(BundleAccess.BUNDLE.getString("data_import_action"));
	}	 
	
	@Override
	public boolean canHandle(ActionContext context) {
		return true;
	}

	@Override
	public boolean isEnabled() {
		// return targets != null && !targets.isEmpty();
		return true;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		/** if (targets != null) {
			Object src = e.getSource();
			Component c = (src instanceof Component) ? (Component) src : null;
			File file = selectFile(c);
			if (file != null) {
				new CSVExporter(c, targets, file).export();
			}
		} */
	}
	
	/** private File selectFile(Component source) {
		// create a save as dialog
		JFileChooser fileChooser = new CSVFileChooser();
		return fileChooser.showSaveDialog(source) == FileChooser.APPROVE_OPTION ?
				fileChooser.getSelectedFile() : null;
	} */

	/**
	 * Import from the This menu.
	 */
	/** public static class ThisExportCSVAction extends DataImportAction {
		private static final long serialVersionUID = -4715218932910019818L;

		@Override
		protected Collection<AbstractComponent> 
				getTargets(ActionContext context) {
			
			if (context.getWindowManifestation() != null) {
				return Collections.singleton(
						context.getWindowManifestation()
						       .getManifestedComponent());
			}
			return null;
			
		}
		
	}

	/**
	 * Export as CSV, from the Objects menu.
	 */
	/** public static class ObjectsExportCSVAction extends DataImportAction {
		private static final long serialVersionUID = -4715218932910019818L;

		@Override
		protected Collection<AbstractComponent> 
				getTargets(ActionContext context) {
			
			if (context.getSelectedManifestations() != null) {
				List<AbstractComponent> selected = 
						new ArrayList<AbstractComponent>();
				for (View v : context.getSelectedManifestations()) {
					selected.add(v.getManifestedComponent());
				}
				return selected;
			}
			return null;
			
		}
		
	}*/

}
