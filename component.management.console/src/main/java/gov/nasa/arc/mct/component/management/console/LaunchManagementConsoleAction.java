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
package gov.nasa.arc.mct.component.management.console;

import gov.nasa.arc.mct.gui.ActionContext;
import gov.nasa.arc.mct.gui.ContextAwareAction;

import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.JDialog;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

import com.github.danielpacak.osgi.swingconsole.Resource;
import com.github.danielpacak.osgi.swingconsole.ResourceManager;
import com.github.danielpacak.osgi.swingconsole.SwingConsole;

/**
 * An action to launch the {@link SwingConsole management} console.
 * 
 * @author pacak.daniel@gmail.com
 */
public class LaunchManagementConsoleAction extends ContextAwareAction {

    private static final long serialVersionUID = -6266100985979734883L;

    private static Resource resource = ResourceManager.getResource(LaunchManagementConsoleAction.class);

    public static final String MENU_BAR_PATH = "/help/additions";

    public static final String COMMAND_KEY = "LAUNCH_MGMT_CONSOLE_ACTION";

    public LaunchManagementConsoleAction() {
        super(resource.getString("action.name"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Window hostingWindow = getFirstVisibleWindow();
        SwingConsole console = new SwingConsole(getBundleContext());
        JDialog dialog = new JDialog(hostingWindow);
        dialog.setModal(true);
        dialog.setTitle(resource.getString("dialog.title"));
        dialog.setSize(resource.getInteger("dialog.width"), resource.getInteger("dialog.height"));

        dialog.add(console);
        dialog.setLocationRelativeTo(hostingWindow);
        dialog.setVisible(true);
    }

    @Override
    public boolean canHandle(ActionContext context) {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // TODO Hacky, but I don't see a better way to get the BundleContext.
    BundleContext getBundleContext() {
        return FrameworkUtil.getBundle(LaunchManagementConsoleAction.class).getBundleContext();
    }

    private Window getFirstVisibleWindow() {
        Window[] windows = Window.getWindows();
        if (windows == null) {
            return null;
        }
        for (Window win : windows) {
            if (win.isVisible()) {
                return win;
            }
        }
        return null;
    }

}
