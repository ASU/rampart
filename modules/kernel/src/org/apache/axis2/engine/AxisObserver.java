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

import org.apache.axis2.description.AxisModule;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.AxisServiceGroup;
import org.apache.axis2.description.ParameterInclude;

public interface AxisObserver extends ParameterInclude {

    // The initilization code will go here
    void init(AxisConfiguration axisConfig);

    void serviceUpdate(AxisEvent event, AxisService service);

    void serviceGroupUpdate(AxisEvent event, AxisServiceGroup serviceGroup);

    void moduleUpdate(AxisEvent event, AxisModule module);
}
