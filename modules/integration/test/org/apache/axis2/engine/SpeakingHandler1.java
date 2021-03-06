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

package org.apache.axis2.engine;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.description.HandlerDescription;
import org.apache.axis2.description.PhaseRule;
import org.apache.axis2.handlers.AbstractHandler;
import org.apache.axis2.phaseresolver.PhaseMetadata;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.namespace.QName;

public class SpeakingHandler1 extends AbstractHandler implements Handler {
	private static final Log log = LogFactory.getLog(SpeakingHandler1.class);
    private String message;
    private String name;

    public SpeakingHandler1(String message, QName name) {
        this.message = message;
        HandlerDescription handlerDesc = new HandlerDescription();
        handlerDesc.setName("SpeakingHandler1");
        PhaseRule rule = new PhaseRule();
        rule.setPhaseName(PhaseMetadata.PHASE_DISPATCH);
        handlerDesc.setRules(rule);
        handlerDesc.setHandler(this);
        handlerDesc.setName(name.getLocalPart());
        init(handlerDesc);
    }

    public String getName() {
        return name;
    }

    public InvocationResponse invoke(MessageContext msgContext) throws AxisFault {
        log.info("I am " + message + " Handler Running :)");
        return InvocationResponse.CONTINUE;        
    }

    public void revoke(MessageContext msgContext) {
        log.info("I am " + message + " Handler Running :)");
    }

    public void setName(String name) {
        this.name = name;
    }

    public HandlerDescription getHandlerDescription() {
        return handlerDesc;
    }

}
