/*
* Copyright 2006 The Apache Software Foundation.
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

import org.apache.axis2.addressing.AddressingConstants;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.util.TargetResolver;

public class TestTargetResolver implements TargetResolver{

    public void resolveTarget(MessageContext messageContext) {
        System.out.println("resolveTarget:" +messageContext.getTo().getAddress());
        if(messageContext.getTo().getAddress().equals("http://ws.apache.org/new/anonymous/address")){
            messageContext.getTo().setAddress(AddressingConstants.Final.WSA_ANONYMOUS_URL);
        }else if(messageContext.getTo().getAddress().startsWith("trtest://")){
            messageContext.getTo().setAddress("http://127.0.0.1:"+messageContext.getTo().getAddress().substring(9));
        }
        System.out.println("resolveTarget:" +messageContext.getTo().getAddress());
    }

}
