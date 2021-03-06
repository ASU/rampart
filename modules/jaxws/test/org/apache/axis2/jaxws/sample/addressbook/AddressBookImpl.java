/*
 * Copyright 2006 The Apache Software Foundation.
 * Copyright 2006 International Business Machines Corp.
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
package org.apache.axis2.jaxws.sample.addressbook;

import java.util.ArrayList;
import java.util.Iterator;

import javax.jws.WebService;

@WebService(endpointInterface="org.apache.axis2.jaxws.sample.addressbook.AddressBook")
public class AddressBookImpl implements AddressBook {

    private static ArrayList<AddressBookEntry> data;
    
    static {
        data = new ArrayList<AddressBookEntry>();
        
        ObjectFactory factory = new ObjectFactory();
        AddressBookEntry entry = factory.createAddressBookEntry();
        entry.setFirstName("Joe");
        entry.setLastName("Test");
        entry.setStreet("1214 Test Ln.");
        entry.setCity("Austin");
        entry.setState("TX");
        data.add(entry);
        
        entry = factory.createAddressBookEntry();
        entry.setFirstName("Sue");
        entry.setLastName("Testfield");
        entry.setStreet("780 1st St.");
        entry.setCity("New York");
        entry.setState("NY");
        data.add(entry);
    }
    
    public boolean addEntry(AddressBookEntry entry) {
        if (entry != null) {
            System.out.println("New AddressBookEntry received");
            System.out.println("       [name] " + entry.getLastName() + ", " + entry.getFirstName());
            System.out.println("      [phone] " + entry.getPhone());
            System.out.println("     [street] " + entry.getStreet());
            System.out.println("[city, state] " + entry.getCity() + ", " + entry.getState());
            data.add(entry);
            return true;
        }
        else {
            return false;
        }
    }

    public AddressBookEntry findEntryByName(String firstname, String lastname) {
        System.out.println("New request received.");
        System.out.println("Looking for entry: [" + firstname + "] [" + lastname + "]");
        Iterator<AddressBookEntry> i = data.iterator();
        while (i.hasNext()) {
            AddressBookEntry entry = i.next();
            
            //If they have a firstname and it doesn't match, just go on
            //to the next entry.
            if (firstname != null) {
                if (!firstname.equals(entry.getFirstName()))
                    continue;                    
            }
            
            if (lastname != null) {
                if (lastname.equals(entry.getLastName()))
                    return entry;
            }
        }
        
        return null;
    }
    
}
