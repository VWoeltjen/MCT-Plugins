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

import gov.nasa.arc.mct.api.feed.BufferFullException;
import gov.nasa.arc.mct.api.feed.FeedDataArchive;
import gov.nasa.arc.mct.data.access.DataArchiveAccess;
import gov.nasa.arc.mct.data.component.DataComponent;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingWorker;

import gov.nasa.arc.mct.components.FeedProvider;
import gov.nasa.arc.mct.components.FeedProvider.RenderingInfo;

/**
 * A SwingWorker responsible for managing the background 
 * activities of Data import. These include parsing data,
 * and saving to MCT.  
 * 
 * @author jdong
 *
 */
public class DataImportWorker extends SwingWorker<Boolean, Void> {
	private File file;
	private FileNotFoundException fnfException;
	private BufferFullException bfException;
	
	public DataImportWorker(File file) {
		super();
		this.file = file;		
		if (file == null) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	protected Boolean doInBackground() throws Exception {
		setProgress(0);
		
		System.out.println("start reading data.");
		Boolean success = readFile(file);
		System.out.println("finish reading data.");
		setProgress(100);
		
		return success & !isCancelled();
	}
	
	private Boolean readFile(File file) {
		boolean success = true;
		Scanner fileScanner = null;
		try {
			fileScanner = new Scanner(file);
		} catch (FileNotFoundException e) {
			fnfException = e;
			success = false;
			e.printStackTrace();
		} finally {
			success = (fileScanner != null);
		}
		
		String line = null;
		while (success && fileScanner.hasNextLine()) {
			line = fileScanner.nextLine();
			System.out.println(line);
			saveData(line);						
		}	
		fileScanner.close();
		
		return success;
	}

    private void saveData(String line) {
    	FeedDataArchive dataArchive = DataArchiveAccess.getDataArchive();
    	Scanner lineScanner = new Scanner(line);
		lineScanner.useDelimiter(",");
		String feedID = DataComponent.PREFIX + lineScanner.next();
		String time   = lineScanner.next();
		String value  = lineScanner.next();
		System.out.println(feedID + "/" + time + "/" + value);
		lineScanner.close();
		
    	Map<String, String> datum = new HashMap<String, String>();
    	RenderingInfo ri = new RenderingInfo(value, Color.ORANGE, " ", Color.ORANGE, true);;
    	ri.setPlottable(true);
    	
    	// Fill in the normally expected key/value pairs
	    datum.put(FeedProvider.NORMALIZED_IS_VALID_KEY, Boolean.TRUE.toString());   
	    datum.put(FeedProvider.NORMALIZED_RENDERING_INFO, ri.toString());
	    datum.put(FeedProvider.NORMALIZED_TIME_KEY, String.valueOf(time));
	    datum.put(FeedProvider.NORMALIZED_VALUE_KEY, value);
	   
	    // System.out.println("Before put data.");
	    if (dataArchive != null) {
	    	// System.out.println("DataArchive is bound.");
	    	try {
				dataArchive.putData(feedID, TimeUnit.MILLISECONDS, Long.parseLong(time), datum);
			} catch (BufferFullException e) {
				bfException = e;
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
	    	// System.out.println("After put data.");
	    }				
    }

	public List<Exception> getException() {
		List<Exception> exceptions = new ArrayList<Exception> ();
		if (fnfException != null) exceptions.add(fnfException);
		if (bfException != null ) exceptions.add(bfException);
	    return exceptions;
		
	}
}
