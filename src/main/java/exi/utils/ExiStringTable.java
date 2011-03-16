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

import java.util.HashMap;

import javax.xml.namespace.QName;

/**
 * The EXI string table consisting of several string table partitions.
 *
 * @author Marco Wegner
 */
public class ExiStringTable {

    // ------------------------------------------------------------------------
    // Member variables
    // ------------------------------------------------------------------------

    /**
     * The strig table partition for URIs.
     */
    private UriPartition uris;

    // ------------------------------------------------------------------------

    /**
     * The strig table partition for prefixes.
     */
    private PrefixPartition prefixes;

    // ------------------------------------------------------------------------

    /**
     * The local names string table partitions mapped to their namespace URIs.
     */
    private HashMap<String, LocalNamesPartition> localNames;

    // ------------------------------------------------------------------------

    /**
     * The value string table partitions mapped to their QNames.
     */
    private HashMap<QName, ValuePartition> values;

    // ------------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------------

    /**
     * Creates a new string table. External instantiation is not permitted,
     * though.
     */
    public ExiStringTable( ) {
        this.uris = new UriPartition( );
        this.prefixes = new PrefixPartition( );
        this.localNames = new HashMap<String, LocalNamesPartition>( );
        this.values = new HashMap<QName, ValuePartition>( );
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    /**
     * Returns the URI string table partition.
     *
     * @return The URI string table partition.
     */
    public UriPartition getUriPartition( ) {
        return this.uris;
    }

    // ------------------------------------------------------------------------

    /**
     * Returns the prefix string table partition.
     *
     * @return The prefix string table partition.
     */
    public PrefixPartition getPrefixPartition( ) {
        return this.prefixes;
    }

    // ------------------------------------------------------------------------

    /**
     * Returns the default string table partition for local names.
     *
     * @return The default string table partition for local names.
     */
    public LocalNamesPartition getLocalNamesPartition( ) {
        return getLocalNamesPartition("");
    }

    // ------------------------------------------------------------------------

    /**
     * Returns the local names string table partition for the specified
     * namespace URI.
     *
     * @param namespaceURI The partition's namespace URI.
     * @return The local names string table partition for the specified
     *         namespace URI.
     */
    public LocalNamesPartition getLocalNamesPartition(String namespaceURI) {
        LocalNamesPartition result;

        if (this.localNames.containsKey(namespaceURI)) {
            result = this.localNames.get(namespaceURI);
        } else {
            result = new LocalNamesPartition(namespaceURI);
            this.localNames.put(namespaceURI, result);
        }

        return result;
    }

    // ------------------------------------------------------------------------

    /**
     * Returns the global string table partition for value strings.
     *
     * @return The global string table partition for value strings.
     */
    public ValuePartition getValuePartition( ) {
        return getValuePartition(null);
    }

    // ------------------------------------------------------------------------

    /**
     * Returns the string table partition for value strings of the specified
     * QName.
     *
     * @param qname The associated QName.
     * @return The string table partition for value strings associated to the
     *         specified QName.
     */
    public ValuePartition getValuePartition(QName qname) {
        ValuePartition result;

        if (this.values.containsKey(qname)) {
            result = this.values.get(qname);
        } else {
            result = new ValuePartition(qname);
            this.values.put(qname, result);
        }

        return result;
    }
}
