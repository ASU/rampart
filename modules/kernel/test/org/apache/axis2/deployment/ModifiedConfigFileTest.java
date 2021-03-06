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

import junit.framework.TestCase;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.engine.AxisConfiguration;

public class ModifiedConfigFileTest extends TestCase {

    AxisConfiguration er;

    public void testPhaseOrderchage() {
        try {
            String filename = "./test-resources/deployment/BadConfigOrderChange/axis2.xml";
            er = ConfigurationContextFactory.createConfigurationContextFromFileSystem(null,filename).getAxisConfiguration();
            fail(
                    "this must failed gracefully with DeploymentException \"Invalid System predefined " +
                    "inphases , phase order does not\" +\n support\\n recheck axis2.xml\"");
        } catch (AxisFault e) {

        }
    }

}
