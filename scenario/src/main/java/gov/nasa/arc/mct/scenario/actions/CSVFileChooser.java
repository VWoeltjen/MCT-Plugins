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

public class CSVFileChooser extends JFileChooser {
	private static final long serialVersionUID = 3795457853362472291L;

	public CSVFileChooser() {
		setDialogTitle("Export as CSV");
		setApproveButtonText("Export");
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
			if (!path.endsWith(".csv")) {
				file = new File(path + ".csv");
			}
		}
		
		return file;
	}
	
}
