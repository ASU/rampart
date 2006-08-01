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

package org.apache.ws.secpolicy.model;

import java.util.ArrayList;

public class SignedEncryptedParts {

    private boolean body;
    
    private ArrayList headers = new ArrayList();
    
    private boolean signedParts;
    
    public SignedEncryptedParts(boolean signedParts) {
        this.signedParts = signedParts;
    }

    /**
     * @return Returns the body.
     */
    public boolean isBody() {
        return body;
    }

    /**
     * @param body The body to set.
     */
    public void setBody(boolean body) {
        this.body = body;
    }

    /**
     * @return Returns the headers.
     */
    public ArrayList getHeaders() {
        return this.headers;
    }

    /**
     * @param headers The headers to set.
     */
    public void addHeader(Header header) {
        this.headers.add(header);
    }

    /**
     * @return Returns the signedParts.
     */
    public boolean isSignedParts() {
        return signedParts;
    }
    
    
    
}