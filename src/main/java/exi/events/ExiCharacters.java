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

import javax.xml.stream.events.Characters;

/**
 * This class represents an EXI Characters (CH) event.
 *
 * @author Marco Wegner
 */
public class ExiCharacters extends ExiEvent {

    // --------------------------------------------------------------------
    // Member variables
    // ------------------------------------------------------------------------

    /**
     * The actual character data.
     */
    private final String data;

    // --------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /**
     * Creates a new EXI characters (CH) event.
     *
     * @param data The character data.
     */
    public ExiCharacters(String data) {
        this.data = data;
    }

    // --------------------------------------------------------------------

    /**
     * Creates a new EXI characters (CH) event.
     *
     * @param chars The XML stream event this EXI Characters (CH) event will be
     *        created from.
     */
    public ExiCharacters(Characters chars) {
        this.data = chars.getData( );
    }

    // --------------------------------------------------------------------
    // Methods
    // --------------------------------------------------------------------

    /**
     * Returns this character event's actual data.
     *
     * @return This character event's actual data.
     */
    public String getData( ) {
        return this.data;
    }

    // --------------------------------------------------------------------

    /* (non-Javadoc)
     * @see exi.events.ExiEvent#getEventType()
     */
    @Override
    public ExiEventType getEventType( ) {
        return ExiEventType.Characters;
    }
}