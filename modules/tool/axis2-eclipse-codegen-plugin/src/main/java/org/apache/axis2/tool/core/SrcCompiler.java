package org.apache.axis2.tool.core;
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
import org.apache.tools.ant.taskdefs.Javac;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.Project;

import java.io.File;
import java.io.IOException;
import java.io.FilenameFilter;
import java.io.FileFilter;
import java.net.URL;
import java.util.Properties;

/**
 * This is the custom class for compiling the source 
 */
public class SrcCompiler extends Javac {

    Project project;
    public SrcCompiler() {
        project = new Project();
        this.setProject(project);
        project.init();
    }

    public void compileSource(File destDir, String compilableSrcLocation){

        Path path;
        Path srcPath = new Path(project,compilableSrcLocation + File.separator + "src");
        this.setSrcdir(srcPath);
        this.setDestdir(destDir);
        this.setIncludes("**/*.java, *.java");
        File lib = new File(compilableSrcLocation+  File.separator + "lib");
        File files [] = new File[lib.listFiles().length];
        files = lib.listFiles();

        Path classpath = new Path(project);
        for (int count =0;count<files.length;count++){
            path = new Path(project,files[count].getAbsolutePath());
            classpath.add(path);
        }
        
        this.setClasspath(classpath);
        this.setFork(true);
        this.perform();

    }
}
