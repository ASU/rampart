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

package org.apache.axis2;

import junit.framework.TestCase;

import java.io.File;

/**
 * Abstract base class for test cases.
 */
public abstract class AbstractTestCase
        extends TestCase {
    protected String testDir = "test" + File.separator;
    protected String sampleDir = "samples" + File.separator;
    protected String outDir = "target" + File.separator + "generated" +
            File.separator +
            "samples" +
            File.separator;
    protected String tempDir = "target" + File.separator + "generated" +
            File.separator +
            "temp";
    protected String testResourceDir = "test-resources";


    /**
     * Basedir for all file I/O. Important when running tests from
     * the reactor.
     */
    public String basedir = System.getProperty("basedir");

    /**
     * @param testName
     */
    public AbstractTestCase(String testName) {
        super(testName);
        if (basedir == null) {
            basedir = new File(".").getAbsolutePath();
        }
        testDir = new File(basedir, testDir).getAbsolutePath();
        sampleDir = new File(basedir, sampleDir).getAbsolutePath();
        outDir = new File(basedir, outDir).getAbsolutePath();
        tempDir = new File(basedir, tempDir).getAbsolutePath();
    }


    public File getTestResourceFile(String relativePath) {
        return new File(testResourceDir, relativePath);
    }
}

