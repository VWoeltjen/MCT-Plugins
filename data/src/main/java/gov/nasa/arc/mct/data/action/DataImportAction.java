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
import gov.nasa.arc.mct.data.component.DataTaxonomyComponent;
import gov.nasa.arc.mct.gui.ActionContext;
import gov.nasa.arc.mct.gui.ContextAwareAction;
import gov.nasa.arc.mct.gui.FileChooser;
import gov.nasa.arc.mct.gui.View;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JFileChooser;


/**
 * Represents the "Import > Data" action, used to import 
 * CSV (comma-separated value) format files into MCT. 
 * 
 * @author jdong
 *
 */
public class DataImportAction extends ContextAwareAction {

	private static final long serialVersionUID = 7714664337177992302L;
	private ActionContext currentContext;

	public DataImportAction() {
		super(BundleAccess.BUNDLE.getString("data_import_action"));
	}	 
	
	@Override
	public boolean canHandle(ActionContext context) {
		currentContext = context;
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	/**
     * Presents a FileChooser to user, then imports the data into MCT. 
     * 
     * @param e the {@link ActionEvent}
     */
	@Override
	public void actionPerformed(ActionEvent e) {
		View window = currentContext.getWindowManifestation();
		File file = selectFile(window);
		
		View manifestation = currentContext.getSelectedManifestations()
	             .iterator().next();
		assert manifestation != null;
		AbstractComponent selectedComponent = manifestation.getManifestedComponent();
		
		if (file != null) {
			new DataImporter(window, selectedComponent, file).importData();
		}
	}
	
	/**
	 * if enable select multiple files or directory, refer to ImportAction.selectFile()
	 * 
	 * Opens FileChooser with the passed in component as the parent and returns the selected
	 * File specified by the user through the FileChooser.
     * 
	 * @param parent the current window which the FileChooser belongs to
	 * @return a File object returned by the FileChooser, or null if user canceled out of the FileChooser
	 */
	private File selectFile(Component parent) {
		if (parent == null) return null;
		
		JFileChooser dataFileChooser = new ImportFileChooser();
		dataFileChooser.setDialogTitle(BundleAccess.BUNDLE.getString("data_import_chooser_title"));
		dataFileChooser.setApproveButtonText(BundleAccess.BUNDLE.getString("data_import_button"));
		return dataFileChooser.showSaveDialog(parent) == FileChooser.APPROVE_OPTION ?
				dataFileChooser.getSelectedFile() : null;
	}

}
