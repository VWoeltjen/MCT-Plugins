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
package gov.nasa.arc.mct.scenario.actions;

import gov.nasa.arc.mct.gui.FileChooser;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * A file chooser for CSV files, to support 
 * CSV export.
 * 
 * @author vwoeltje
 *
 */
public class CSVFileChooser extends JFileChooser {
	private static final long serialVersionUID = 3795457853362472291L;
	private static final String EXTENSION =
			BundleAccess.BUNDLE.getString("csv_extension");
	
	public CSVFileChooser() {
		setDialogTitle(BundleAccess.BUNDLE.getString("csv_chooser_title"));
		setApproveButtonText(BundleAccess.BUNDLE.getString("csv_chooser_ok"));
		setFileSelectionMode(FileChooser.FILES_ONLY);
		setMultiSelectionEnabled(false);
		setFileFilter(new CSVFileFilter());
	}
	
	@Override
	public File getSelectedFile() {
		File file = super.getSelectedFile();
		
		if (file != null) {
			// Ensure file ends with CSV extension
			String path = file.getAbsolutePath();
			if (!path.endsWith(EXTENSION)) {
				file = new File(path + EXTENSION);
			}
		}
		
		return file;
	}

	// Overridden to allow pop up if file exists
	// Recommended in http://stackoverflow.com/questions/3651494
	@Override
	public void approveSelection() {
		File file = getSelectedFile();

		// Allow if we're not saving, or 
		// if there is no selection, or
		// if the file doesn't exist, or
		// if the user confirms the overwrite.
		if (getDialogType() != JFileChooser.SAVE_DIALOG ||
			file == null || 			
			!file.exists() ||
		    JOptionPane.showConfirmDialog(
				this, 
				BundleAccess.BUNDLE.getString("csv_exists_warning"), 
				BundleAccess.BUNDLE.getString("csv_exists_title"), 
				JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			super.approveSelection();
		}		
	}
}
