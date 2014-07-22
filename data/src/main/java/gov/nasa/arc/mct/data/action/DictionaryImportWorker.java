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
import gov.nasa.arc.mct.data.component.DataComponent;
import gov.nasa.arc.mct.data.component.DataTaxonomyComponent;
import gov.nasa.arc.mct.platform.spi.PlatformAccess;
import gov.nasa.arc.mct.services.component.ComponentRegistry;
import gov.nasa.arc.mct.services.internal.component.ComponentInitializer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.SwingWorker;

/**
 * A SwingWorker responsible for managing the background 
 * activities of Dictionary import. These include creating 
 * DataComponent and saving to MCT.  
 * 
 * @author jdong
 *
 */
public class DictionaryImportWorker extends SwingWorker<Boolean, Void> {
	private AbstractComponent parent;
	private File file;
	private IOException ioe;
	
	/** The MCT component registry **/
	private ComponentRegistry registry;
	
	public DictionaryImportWorker(AbstractComponent component, File file) {
		super();
		this.parent = component;
		this.file = file;
		this.registry = PlatformAccess.getPlatform().getComponentRegistry();
		
		if (component == null || file == null) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	protected Boolean doInBackground() throws Exception {
		setProgress(0);		
		Boolean success = parseDictionary(file);
		setProgress(100);
		
		return success & !isCancelled();
	}
	
	private Boolean parseDictionary(File file) {
		List<AbstractComponent> components = new ArrayList<AbstractComponent> ();
		BufferedReader r = null;
		boolean success = true;

		try {
			r = new BufferedReader(new FileReader(file));
			String reference = null;
			while ((reference = r.readLine()) != null) {				
				components.add(createDataComponent(reference, parent));
			}			
		} catch(IOException ioe) {
			success = false;
			this.ioe = ioe;
		} finally {
			if (r != null) {
				try {
					r.close();
				} catch(IOException ioe) {
					success = false;
					this.ioe = ioe;
				}
			}
		}
		components.add(parent);
		// save to database
		PlatformAccess.getPlatform().getPersistenceProvider().persist(components);
		
		return success;
	}
	
	private AbstractComponent createDataComponent(String reference, AbstractComponent parent) {
		AbstractComponent dataComponent = registry.newInstance(DataComponent.class, parent);
		dataComponent.setExternalKey(reference);
		dataComponent.setDisplayName(reference);
		if ((dataComponent instanceof DataComponent) && (parent instanceof DataTaxonomyComponent)) {
			((DataComponent)dataComponent).setParent((DataTaxonomyComponent)parent);
		}
				
		ComponentInitializer dataComponentCapability = dataComponent.getCapability(ComponentInitializer.class);
        dataComponentCapability.setId(DataComponent.PREFIX + dataComponent.getExternalKey());
        dataComponentCapability.setOwner(BundleAccess.BUNDLE.getString("data_owner"));
        dataComponentCapability.setCreator(BundleAccess.BUNDLE.getString("data_owner"));        
		return dataComponent;
	}

	public IOException getException() {
		return ioe;
	}
}
