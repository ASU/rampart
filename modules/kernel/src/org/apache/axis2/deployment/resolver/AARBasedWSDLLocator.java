package org.apache.axis2.deployment.resolver;

import org.apache.axis2.deployment.DeploymentConstants;
import org.apache.axis2.deployment.util.Utils;
import org.apache.ws.commons.schema.resolver.DefaultURIResolver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.InputSource;

import javax.wsdl.xml.WSDLLocator;
import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.net.URI;
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

/**
 * Custom WSDL locator to load schemas from zip archives
 * Need to provide the aarFile and the baseInputStream for
 * the base WSDL file
 * <p/>
 * The logic here is that we only care about the import location
 * all imports must be relative to the META-INF folder
 */
public class AARBasedWSDLLocator extends DefaultURIResolver implements WSDLLocator {

    protected static final Log log = LogFactory
            .getLog(AARBasedWSDLLocator.class);

    private File aarFile;
    private InputStream baseInputStream;
    private URI lastImportLocation;
    private String baseURI;

    public AARBasedWSDLLocator(String baseURI, File zipFile, InputStream baseInputStream) {
        this.baseURI = baseURI;
        this.baseInputStream = baseInputStream;
        this.aarFile = zipFile;
    }

    public InputSource getBaseInputSource() {
        return new InputSource(baseInputStream);
    }

    /**
     * @param parentLocation
     * @param importLocation
     */
    public InputSource getImportInputSource(String parentLocation, String importLocation) {
        lastImportLocation = URI.create(parentLocation).resolve(importLocation);

        if (isAbsolute(importLocation)) {
            return super.resolveEntity(
                    null, importLocation, parentLocation);
        } else {
            //we don't care about the parent location
            ZipInputStream zin = null;
            try {

                zin = new ZipInputStream(new FileInputStream(aarFile));
                ZipEntry entry;
                byte[] buf = new byte[1024];
                int read;
                ByteArrayOutputStream out;
                String searchingStr = lastImportLocation.toString();
                while ((entry = zin.getNextEntry()) != null) {
                    String entryName = entry.getName().toLowerCase();
                    if (entryName.equalsIgnoreCase(searchingStr)) {
                        out = new ByteArrayOutputStream();
                        while ((read = zin.read(buf)) > 0) {
                            out.write(buf, 0, read);
                        }
                        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
                        return new InputSource(in);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    if (zin != null) zin.close();
                } catch (IOException e) {
                    log.debug(e);
                }
            }
        }

        log.info("AARBasedWSDLLocator: Unable to resolve" + lastImportLocation);
        return null;
    }

    /**
     * As for the zip there is no point in returning
     * a base URI
     */
    public String getBaseURI() {
        // we don't care
        return baseURI;
    }

    /**
     * returns the latest import
     */
    public String getLatestImportURI() {
        //we don't care about this either
        return lastImportLocation.toString();
    }
    
    public void close() {
        //TODO: FIXME:    
    }
}
