/**
 * Copyright (c) 2010, Marco Wegner and Dennis Pfisterer, Institute of Telematics, University of Luebeck
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 	- Redistributions of source code must retain the above copyright notice, this list of conditions and the following
 * 	  disclaimer.
 * 	- Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 * 	  following disclaimer in the documentation and/or other materials provided with the distribution.
 * 	- Neither the name of the University of Luebeck nor the names of its contributors may be used to endorse or promote
 * 	  products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package exi.utils;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Bi-directional map for namespaces.
 * 
 * @author Marco Wegner
 */
public class ExiNamespaceTable {

    // ------------------------------------------------------------------------
    // Member variables
    // ------------------------------------------------------------------------

    /**
     * The map.
     */
    private Map<String, URI> nsMap = new HashMap<String, URI>( );

    // ------------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------------

    /**
     * Constructs a new namespace mapping.
     *
     * @throws Exception If something goes wrong.
     */
    public ExiNamespaceTable( ) throws Exception {
        this.nsMap.put("", URI.create(""));
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    /**
     * Adds an entry to the namespace mapping.
     *
     * @param prefix The prefix.
     * @param uri The namespace URI.
     */
    public void put(String prefix, URI uri) {
        this.nsMap.put(prefix, uri);
    }

    // ------------------------------------------------------------------------

    /**
     * Returns the namespace URI belonging to the specified prefix.
     *
     * @param prefix The prefix.
     * @return The namespace URI belonging to the prefix or <code>null</code>
     *         if there is no such prefix in the map.
     */
    public URI getNamespaceURI(String prefix) {
        URI uri = null;
        if (this.nsMap.containsKey(prefix)) {
            uri = this.nsMap.get(prefix);
        }
        return uri;
    }

    // ------------------------------------------------------------------------

    /**
     * Returns the prefix belonging to the specified namespace URI.
     *
     * @param uri The URI.
     * @return The prefix to the namespace URI or <code>null</code> if there
     *         is no such URI in the map.
     */
    public String getNamespacePrefix(URI uri) {
        String prefix = "";
        for (Map.Entry<String, URI> entry : this.nsMap.entrySet( )) {
            if (entry.getValue( ).equals(uri)) {
                prefix = entry.getKey( );
            }
        }
        return prefix;
    }
}
