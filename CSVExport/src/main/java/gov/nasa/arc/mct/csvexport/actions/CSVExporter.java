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
package gov.nasa.arc.mct.csvexport.actions;

import gov.nasa.arc.mct.components.AbstractComponent;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;

/**
 * Responsible for managing the export of a CSV. 
 * Serves as an intermediary between the worker which 
 * handles the background export and the progress 
 * monitor which updates the user.
 * 
 * @author vwoeltje
 *
 */
public class CSVExporter {
	private Component component;
	private Collection<AbstractComponent> components;
	private File file;
	
	/**
	 * Create a new CSV exporter.
	 * @param component an AWT component (for progress monitor)
	 * @param components MCT components to export
	 * @param file the file to which components should be written
	 */
	public CSVExporter(Component component,
			Collection<AbstractComponent> components, File file) {
		super();
		this.component = component;
		this.components = components;
		this.file = file;
	}
	
	/**
	 * Perform the export of items defined in the constructor. 
	 * The preparation of the CSV data and writing to disk 
	 * occur on a background thread, and a progress monitor 
	 * is provided if necessary.
	 */
	public void export() {
		final CSVExportWorker worker = new CSVExportWorker(components, file);
		final ProgressMonitor monitor = new ProgressMonitor(component,
				BundleAccess.BUNDLE.getString("csv_progress_message"), 
				"", 0, 100);
		
		monitor.setMillisToDecideToPopup(200);
		monitor.setMillisToPopup(750);
		
		worker.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (worker.isDone() && evt.getPropertyName().equals("state")) {
					notifyComplete(worker);
				} else if (monitor.isCanceled()) {
					worker.cancel(true);
				} else {
					monitor.setProgress(worker.getProgress());
				} 
			}			
		});
		
		worker.execute();
	}
	
	private void notifyComplete(CSVExportWorker worker) {
		try {
			// Should return true on success; we only want to notify on false.
			// Also, if there is no exception, assume export was cancelled.
			if (!worker.get() && worker.getException() != null) {
				Exception e = worker.getException();
				String message = BundleAccess.BUNDLE.getString("csv_error_message") +
						(e.getMessage() != null ? "\n" + e.getMessage() : "");
				String title = BundleAccess.BUNDLE.getString("csv_error_title");						
				JOptionPane.showMessageDialog(component, message, title, JOptionPane.ERROR_MESSAGE);
			}
		} catch (InterruptedException e) {
			// Should not occur - already checked isDone
		} catch (ExecutionException e) {
			// Should not occur - already checked isDone
		}
	}
}
