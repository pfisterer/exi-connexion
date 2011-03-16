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
package exi.events;

import java.net.URI;

import javax.xml.namespace.QName;
import javax.xml.stream.events.Namespace;

/**
 * This class represents an EXI Namespace Declaration (NS) event.
 *
 * @author Marco Wegner
 */
public class ExiNamespaceDeclaration extends ExiAttribute {

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /**
     * Creates a new EXI Namespace Declaration (NS) event.
     *
     * @param prefix The namespace prefix.
     * @param uri The namespace URI.
     */
    public ExiNamespaceDeclaration(String prefix, URI uri) {
        super(new QName(prefix), uri.toString( ));
    }

    // ------------------------------------------------------------------------

    /**
     * Creates a new EXI Namespace Declaration (NS) event.
     *
     * @param namespace The XML stream event this EXI namespace declaration
     *        event will be created from.
     */
    public ExiNamespaceDeclaration(Namespace namespace) {
        super(namespace);
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    /**
     * Returns the namespace's prefix.
     *
     * @return The namespace's prefix.
     */
    public String getNamespacePrefix( ) {
        return getQualifiedName( ).getLocalPart( );
    }

    // ------------------------------------------------------------------------

    /**
     * Returns the namespace's URI.
     *
     * @return The namespace's URI.
     */
    public URI getNamespaceURI( ) {
        return URI.create(getValue( ));
    }

    // ------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see exi.events.ExiAttribute#getEventType()
     */
    @Override
    public ExiEventType getEventType( ) {
        return ExiEventType.NamespaceDeclaration;
    }

    // ------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see exi.events.ExiAttribute#getEventTypeString()
     */
    @Override
    public String getEventTypeString( ) {
        return getEventType( ).code( );
    }
}
