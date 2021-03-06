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

import org.apache.axis2.description.Flow;

import javax.xml.namespace.QName;

public class MockFlow extends Flow {
    public MockFlow(String message, int length) {
        super();
        for (int i = 0; i < length; i++) {
            SpeakingHandler1 h1 = new SpeakingHandler1(
                    "Executing " + i + " inside " + message, new QName("SpeakingHandler" + i));
            h1.setName("SpeakingHandler" + i);
            this.addHandler(h1.getHandlerDescription());
        }
    }

}
