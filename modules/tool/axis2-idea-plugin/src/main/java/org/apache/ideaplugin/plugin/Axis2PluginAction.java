package org.apache.ideaplugin.plugin;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;

import javax.swing.*;
/*
* Copyright 2004,2005 The Apache Software Foundation.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*
*/

/**
 * Author: Deepal Jayasinghe
 * Date: Sep 24, 2005
 * Time: 10:22:08 AM
 */
public class Axis2PluginAction extends AnAction {

    private ImageIcon myIcon;

    public Axis2PluginAction() {
        super("GC", "Axis2 plugins", null);
    }

    public void actionPerformed(AnActionEvent anActionEvent) {
        Application application =
                ApplicationManager.getApplication();
        Project project = (Project) anActionEvent.getDataContext().getData(DataConstants.PROJECT);

        Axis2IdeaPlugin axis2component =
                (Axis2IdeaPlugin) application.getComponent(Axis2IdeaPlugin.class);
        axis2component.showTool(project);
    }

    public void update(AnActionEvent event) {
        super.update(event);
        Presentation presentation = event.getPresentation();
        if (ActionPlaces.MAIN_TOOLBAR.equals(event.getPlace())) {
            if (myIcon == null) {
                java.net.URL resource = Axis2PluginAction.class.getResource("/icons/icon.png");
                myIcon = new ImageIcon(resource);
            }
            presentation.setIcon(myIcon);
        }
    }

}
