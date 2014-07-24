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
import gov.nasa.arc.mct.platform.spi.PlatformAccess;
import gov.nasa.arc.mct.platform.spi.RoleAccess;
import gov.nasa.arc.mct.services.internal.component.User;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.JFileChooser;

/**
 * Represents the "Import > Dictionary" action, used to import 
 * CSV (comma-separated value) format dictionary files into MCT. 
 * A dictionary file contains a list of references of TLE data.
 * 
 * @author jdong
 *
 */
public class DictionaryImportAction extends ContextAwareAction {

	private static final long serialVersionUID = -6358531321024665626L;
	private ActionContext currentContext;

	public DictionaryImportAction() {
		super(BundleAccess.BUNDLE.getString("dictionary_import_action"));
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
     * Presents a FileChooser to user, then imports each reference listed in the
     * dictionary file as DataComponent into MCT. 
     * 
     * @param e the {@link ActionEvent}
     */
	@Override
	public void actionPerformed(ActionEvent e) {		
		/**
		 * There are two ways to access the View associated with 
		 * 1. through ActionContext: context.getWindowManifestation() -- suggested
		 * 2. through ActionEvent: e.getSource()
		 *    Object src = e.getSource();
		 *	  Component c = (src instanceof Component) ? (Component) src : null;
		 */
		View window = currentContext.getWindowManifestation();
		File file = selectFile(window);
		
		View manifestation = currentContext.getSelectedManifestations()
	             .iterator().next();
		assert manifestation != null;
		AbstractComponent selectedComponent = 
			               manifestation.getManifestedComponent();
		
		if (file != null) {
			new DictionaryImporter(window, selectedComponent, file).importDictionaries();
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
		
		JFileChooser dictionaryFileChooser = new ImportFileChooser();
		dictionaryFileChooser.setDialogTitle(BundleAccess.BUNDLE.getString("dictionary_import_chooser_title"));
		dictionaryFileChooser.setApproveButtonText(BundleAccess.BUNDLE.getString("dictionary_import_button"));
		return dictionaryFileChooser.showSaveDialog(parent) == FileChooser.APPROVE_OPTION ?
				dictionaryFileChooser.getSelectedFile() : null;
	}
	
	/** 
     * Opens FileChooser with the passed in component as parent and returns a collection 
     * of File objects containing XML files specified by the user through the FileChooser.
     * User can select a single XML file or a directory. Returns any XML files contained
     * in the selected directory.
     *  
     * @param source parent component into which the FileChooser is to be opened
     * @return List of File objects returned by the FileChooser, which may be empty if 
     * directory contained no XML files,
     * or null if user canceled out of the FileChooser
     */
    /** public List<File> selectFiles(Component source){
    	AbstractComponent targetComponent = currentContext.getSelectedManifestations()
                .iterator().next().getManifestedComponent();
    	//Initialize the file chooser with the current user
    	User user = PlatformAccess.getPlatform().getCurrentUser();
        final CustomFileChooser fileChooser = new CustomFileChooser();
        
        //add the list of owners and set selection to current owner
        fileChooser.addOwners(Arrays.asList(RoleAccess.getAllUsers()));
        fileChooser.setOwner(user.getUserId());
       
        fileChooser.setDialogTitle(bundle.getString("import_message")
				+ targetComponent.getDisplayName());
        fileChooser.setApproveButtonText(bundle.getString("import_button"));
        
        fileChooser.setFileSelectionMode(FileChooser.FILES_AND_DIRECTORIES);

        fileChooser.setFileFilter(new XMLFileFilter());
        fileChooser.setMultiSelectionEnabled(true);
        
        if (source == null) return null;
        int returnVal = fileChooser.showOpenDialog(source);

        if (returnVal == FileChooser.APPROVE_OPTION) {
            File[] rootFileOrDir = fileChooser.getSelectedFiles();
            List<File> files = Utilities.filterSelectedFiles(Arrays.asList(rootFileOrDir));     
            //set the owner
            owner = fileChooser.getOwner();
            return files;
        } 
        return null;
    }*/

}
