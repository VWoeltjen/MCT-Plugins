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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;

import javax.swing.SwingWorker;

/**
 * A SwingWorker responsible for managing the background 
 * activities of CSV export. These include "rendering" the CSV 
 * and writing it to disk. 
 * 
 * @author vwoeltje
 *
 */
public class CSVExportWorker extends SwingWorker<Boolean, Void> {
	private Collection<AbstractComponent> components;
	private File file;
	private IOException ioe;
	
	public CSVExportWorker(Collection<AbstractComponent> components, File file) {
		super();
		this.components = components;
		this.file = file;
		
		if (components == null || file == null || components.isEmpty()) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	protected Boolean doInBackground() throws Exception {
		setProgress(0);
		
		CSVRenderer r = new CSVRenderer(components);
		
		int rowCount = r.getRowCount();
		
		Writer w = null;
		boolean success = true;
		try {
			w = new FileWriter(file);
			w.write(r.renderHeaders());
			for (int i = 0; i < rowCount && !isCancelled(); i++) {
				setProgress((i * 100) / rowCount);
				w.write(r.renderRow(i));
			}
		} catch (IOException ioe) {
			success = false;
			this.ioe = ioe;		
		} finally {
			if (w != null) {
				try {
					w.close();
				} catch (IOException ioe) {
					success = false;
					this.ioe = ioe;
				}
			}
		}
		
		setProgress(100);
		
		return success & !isCancelled();
	}

	public IOException getException() {
		return ioe;
	}
}
