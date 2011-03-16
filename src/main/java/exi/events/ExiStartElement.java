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
/**
 *
 */
package exi.events;

import javax.xml.namespace.QName;
import javax.xml.stream.events.StartElement;

/**
 * This class represents an EXI Start Element (SE) event.
 *
 * @author Marco Wegner
 */
public class ExiStartElement extends ExiEvent {

    // ------------------------------------------------------------------------
    // Member variables
    // ------------------------------------------------------------------------

    /**
     * The element's qualified name.
     */
    private final QName qname;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /**
     * Creates a new EXI Start Element (SE) event.
     *
     * @param qname The attribute's qualified name.
     */
    public ExiStartElement(QName qname) {
        super( );
        this.qname = qname;
    }

    // --------------------------------------------------------------------

    /**
     * Creates a new EXI Start Element (SE) event.
     *
     * @param event The XML stream event this EXI Start Element (SE) event will
     *        be created from.
     */
    public ExiStartElement(StartElement event) {
        super( );
        this.qname = event.getName( );
    }

    // --------------------------------------------------------------------
    // Methods
    // --------------------------------------------------------------------

    /**
     * Returns the element's qualified name.
     *
     * @return The element's qualified name.
     */
    public QName getQualifiedName( ) {
        return this.qname;
    }

    // --------------------------------------------------------------------

    /*
     * (non-Javadoc)
     *
     * @see exi.events.ExiEvent#getEventType()
     */
    @Override
    public ExiEventType getEventType( ) {
        return ExiEventType.StartElement;
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
