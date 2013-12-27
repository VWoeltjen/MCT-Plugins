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

public abstract class ExportCSVAction extends ContextAwareAction {
	private static final long serialVersionUID = 1364579701311592635L;
	private Collection<AbstractComponent> targets;

	public ExportCSVAction() {
		super("CSV...");
	}	 
	
	protected abstract Collection<AbstractComponent> 
			getTargets(ActionContext context);
	
	@Override
	public boolean canHandle(ActionContext context) {
		targets = getTargets(context);
		return targets != null;
	}

	@Override
	public boolean isEnabled() {
		return targets != null && !targets.isEmpty();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (targets != null) {
			Object src = e.getSource();
			Component c = (src instanceof Component) ? (Component) src : null;
			File file = selectFile(c);
			if (file != null) {
				new CSVExporter(c, targets, file).export();
			}
		}
	}
	
	private File selectFile(Component source) {
		// create a save as dialog
		JFileChooser fileChooser = new CSVFileChooser();
		return fileChooser.showDialog(source, "Export") == FileChooser.APPROVE_OPTION ?
				fileChooser.getSelectedFile() : null;
	}

	public static class ThisExportCSVAction extends ExportCSVAction {
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
	
	public static class ObjectsExportCSVAction extends ExportCSVAction {
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
		
	}

}
