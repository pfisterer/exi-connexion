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

import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;

/**
 * This class represents an EXI Attribute (AT) event.
 *
 * @author Marco Wegner
 */
public class ExiAttribute extends ExiEvent {

    // ------------------------------------------------------------------------
    // Member variables
    // ------------------------------------------------------------------------

    /**
     * The attribute's qualified name.
     */
    private final QName qname;

    // ------------------------------------------------------------------------

    /**
     * The attribute's value.
     */
    private final String value;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /**
     * Creates a new EXI Attribute (AT) event.
     *
     * @param qname The attribute's qualified name.
     * @param value The attribute's value.
     */
    public ExiAttribute(QName qname, String value) {
        super( );
        this.qname = qname;
        this.value = value;
    }

    // ------------------------------------------------------------------------

    /**
     * Creates a new EXI Attribute (AT) event.
     *
     * @param attribute The XML stream event this EXI Attribute (AT) event will
     *        be created from.
     */
    public ExiAttribute(Attribute attribute) {
        super( );
        this.qname = attribute.getName( );
        this.value = attribute.getValue( );
    }

    // --------------------------------------------------------------------
    // Methods
    // --------------------------------------------------------------------

    /**
     * Returns the attribute's qualified name.
     *
     * @return The attribute's qualified name.
     */
    public QName getQualifiedName( ) {
        return this.qname;
    }

    // --------------------------------------------------------------------

    /**
     * Returns the attribute's value.
     *
     * @return The attribute's value.
     */
    public String getValue( ) {
        return this.value;
    }

    // --------------------------------------------------------------------

    /*
     * (non-Javadoc)
     *
     * @see exi.events.ExiEvent#getEventType()
     */
    @Override
    public ExiEventType getEventType( ) {
        return ExiEventType.Attribute;
    }

    // --------------------------------------------------------------------

    /*
     * (non-Javadoc)
     *
     * @see exi.events.ExiEvent#getEventTypeString()
     */
    @Override
    public String getEventTypeString( ) {
        return String.format(String.format(
                "%s(%s)",
                getEventType( ).code( ),
                this.qname.getLocalPart( )
        ));
    }
}
