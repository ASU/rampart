/*
 * Copyright 2003,2004 The Apache Software Foundation.
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
package org.apache.axis.deployment;

import org.apache.axis.engine.AxisFault;
import org.apache.axis.engine.EngineRegistry;
import org.apache.axis.engine.EngineRegistryFactory;
import org.apache.axis.phaseresolver.PhaseException;

import javax.xml.stream.XMLStreamException;


public class EngineRegistryFactoryImpl implements  EngineRegistryFactory{
    public EngineRegistry createEngineRegistry(String file) throws AxisFault {
        try {
            DeploymentEngine deploymentEngine = new DeploymentEngine(file);
            return deploymentEngine.start();
        } catch (PhaseException e) {
            throw AxisFault.makeFault(e);
        } catch (DeploymentException e) {
            throw AxisFault.makeFault(e);
        } catch (XMLStreamException e) {
            throw AxisFault.makeFault(e);
        }
    }
}
