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
import org.apache.axis2.description.Parameter;

/**
 * Interface Handler
 */
public interface Handler {
    /**
     * Since this might change the whole behavior of Axis2 handlers, and since this is still under discussion 
     * (http://marc.theaimsgroup.com/?l=axis-dev&m=114504084929285&w=2) implementation of this method is deferred.
     * Note : This method will not be automatically called, from Axis2 engine, until this is fully implemented.
     */
    public void cleanup();

    /**
     * Method init.
     *
     * @param handlerdesc
     */
    public void init(HandlerDescription handlerdesc);

    /**
     * Invoke is called to do the actual work of the Handler object.
     * If there is a fault during the processing of this method it is
     * invoke's job to catch the exception and undo any partial work
     * that has been completed.
     * 
     * N.B. This method may be called concurrently from multiple threads.
     *
     * @param msgContext the <code>MessageContext</code> to process with this
     *                   <code>Handler</code>.
     * @return An InvocationResponse that indicates what
     *         the next step in the message processing should be.
     * @throws AxisFault if the handler encounters an error
     */
    public InvocationResponse invoke(MessageContext msgContext) throws AxisFault;

    /**
     * Gets the HandlerDescription of a handler. This is used as an input to get phaseRule of a handler.
     *
     * @return Returns HandlerDescription.
     */
    public HandlerDescription getHandlerDesc();

    /**
     * Method getName.
     *
     * @return Returns String
     */
    public String getName();

    /**
     * Method getParameter.
     *
     * @param name
     * @return Returns Parameter.
     */
    public Parameter getParameter(String name);
    
    /**
     * This type encapsulates an enumeration of possible message processing
     * instruction values that may be returned by a handler/phase within the
     * runtime.  The returned instruction will determine the next step in
     * the processing.
     */
    public class InvocationResponse
    {
      public static InvocationResponse CONTINUE = new InvocationResponse(0);
      public static InvocationResponse SUSPEND = new InvocationResponse(1);
      public static InvocationResponse ABORT = new InvocationResponse(2);

      private int instructionID;
        
      private InvocationResponse(int instructionID)
      {
        this.instructionID = instructionID;
      }
        
      public boolean equals(InvocationResponse instruction)
      {
        return this.instructionID == instruction.instructionID;
      }
        
      public int hashCode()
      {
        return instructionID;
      }
    }
    
}
