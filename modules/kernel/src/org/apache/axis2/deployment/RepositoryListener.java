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
*/


package org.apache.axis2.deployment;

import org.apache.axis2.deployment.repository.util.ArchiveFileData;
import org.apache.axis2.deployment.repository.util.WSInfoList;
import org.apache.axis2.util.Loader;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;

public class RepositoryListener implements DeploymentConstants {
    private DeploymentEngine deploymentEngine;

    /**
     * The parent directory of the modules and services directories
     */

    /**
     * Reference to a WSInfoList
     */
    private WSInfoList wsInfoList;

    /**
     * This constructor takes two arguments, a folder name and a reference to Deployment Engine
     * First, it initializes the system, by loading all the modules in the /modules directory
     * and then creates a WSInfoList to store information about available modules and services.
     *
     * @param deploymentEngine reference to engine registry for updates
     */

    //The constructor , which loads modules from class path
    public RepositoryListener(DeploymentEngine deploymentEngine, boolean isClasspath) {
        this.deploymentEngine = deploymentEngine;
        wsInfoList = new WSInfoList(deploymentEngine);
        if (!isClasspath) {
            init();
        }
        loadClassPathModules();
    }

    /**
     * Finds a list of modules in the folder and adds to wsInfoList.
     */
    public void checkModules() {
        File root = deploymentEngine.getModulesDir();
        File[] files = root.listFiles();

        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (isSourceControlDir(file)) {
                    continue;
                }
                if (!file.isDirectory()) {
                    if (ArchiveFileData.isModuleArchiveFile(file.getName())) {
                        wsInfoList.addWSInfoItem(file, TYPE_MODULE);
                    }
                } else {
                    if (!"lib".equalsIgnoreCase(file.getName())) {
                        wsInfoList.addWSInfoItem(file, TYPE_MODULE);
                    }
                }
            }
        }
    }


    private boolean isSourceControlDir(File file) {
        if (file.isDirectory()) {
            String name = file.getName();
            if (name.equalsIgnoreCase("CVS") || name.equalsIgnoreCase(".svn")) {
                return true;
            }
        }
        return false;
    }

    private void loadClassPathModules() {
        String classPath = getLocation();
        int lstindex = classPath.lastIndexOf(File.separatorChar);
        if (lstindex > 0) {
            classPath = classPath.substring(0, lstindex);
        } else {
            classPath = ".";
        }
        File root = new File(classPath);
        File[] files = root.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (!file.isDirectory()) {
                    if (ArchiveFileData.isModuleArchiveFile(file.getName())) {
                        //adding modules in the class path
                        wsInfoList.addWSInfoItem(file, TYPE_MODULE);
                    }
                }
            }
        }

        ClassLoader cl = deploymentEngine.getAxisConfig().getModuleClassLoader();
        while (cl != null) {
            if (cl instanceof URLClassLoader) {
                URL[] urls = ((URLClassLoader) cl).getURLs();
                for (int i = 0; (urls != null) && i < urls.length; i++) {
                    String path = urls[i].getPath();
                    //If it is a drive letter, adjust accordingly.
                    if (path.length() >= 3 && path.charAt(0) == '/' && path.charAt(2) == ':')
                        path = path.substring(1);
                    java.io.File file = new java.io.File(URLDecoder.decode(urls[i].getPath()).replace('/',
                            File.separatorChar).replace('|', ':'));
                    if (file.isFile()) {
                        if (ArchiveFileData.isModuleArchiveFile(file.getName())) {
                            //adding modules in the class path
                            wsInfoList.addWSInfoItem(file, TYPE_MODULE);
                        }
                    }
                }
            }
            cl = cl.getParent();
        }

        deploymentEngine.doDeploy();
    }

    /**
     * To get the location of the Axis2.jar from that I can drive the location of class path
     *
     * @return String (location of the axis2 jar)
     */
    private String getLocation() {
        try {
            Class clazz = Loader.loadClass("org.apache.axis2.engine.AxisEngine");
            java.net.URL url = clazz.getProtectionDomain().getCodeSource().getLocation();
            String location = url.toString();
            if (location.startsWith("jar")) {
                url = ((java.net.JarURLConnection) url.openConnection()).getJarFileURL();
                location = url.toString();
            }
            if (location.startsWith("file")) {
                java.io.File file = new java.io.File(URLDecoder.decode(url.getPath()).replace('/',
                        File.separatorChar).replace('|', ':'));
                return file.getAbsolutePath();
            } else {
                return url.toString();
            }
        } catch (Throwable t) {
            return "an unknown location";
        }
    }

    /**
     * Finds a list of services in the folder and adds to wsInfoList.
     */
    public void checkServices() {
        findServicesInDirectory();
        update();
    }

    /**
     * First initializes the WSInfoList, then calls checkModule to load all the modules
     * and calls update() to update the Deployment engine and engine registry.
     */
    public void init() {
        wsInfoList.init();
        checkModules();
        deploymentEngine.doDeploy();
    }

    /**
     * Searches a given folder for jar files and adds them to a list in the
     * WSInfolist class.
     */
    private void findServicesInDirectory() {
        File root = deploymentEngine.getServicesDir();
        File[] files = root.listFiles();

        if (files != null && files.length > 0) {
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (isSourceControlDir(file)) {
                    continue;
                }
                if (!file.isDirectory()) {
                    if (ArchiveFileData.isServiceArchiveFile(file.getName())) {
                        wsInfoList.addWSInfoItem(file, TYPE_SERVICE);
                    }
                } else {
                    if (!"lib".equalsIgnoreCase(file.getName())) {
                        wsInfoList.addWSInfoItem(file, TYPE_SERVICE);
                    }
                }
            }
        }
        wsInfoList.addWSInfoItem(null, TYPE_DEFAULT);
    }

    /**
     * Method invoked from the scheduler to start the listener.
     */
    public void startListener() {
        checkServices();
        update();
    }

    /**
     * Updates WSInfoList object.
     */
    public void update() {
        wsInfoList.update();
    }
}
