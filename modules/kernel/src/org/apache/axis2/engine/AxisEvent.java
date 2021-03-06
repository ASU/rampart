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

public class AxisEvent {

    /**
     * An Axis event is sent to registered listeners whenever anything significant
     * happens to <code>AxisConfiguration</code>.
     */
    public static final int SERVICE_DEPLOY    = 1;
    public static final int SERVICE_REMOVE    = 0;
    public static final int SERVICE_STOP      = 2;
    public static final int SERVICE_START     = 3;
    public static final int MODULE_DEPLOY     = 4;
    public static final int MODULE_REMOVE     = 5;
    private int EVENT_TYPE;

    public AxisEvent(int EVENT_TYPE) {
        this.EVENT_TYPE = EVENT_TYPE;
    }

    public int getEventType() {
        return EVENT_TYPE;
    }

}
