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

import javax.xml.stream.events.Comment;

/**
 * This class represents an EXI Comment (CM) event.
 *
 * @author Marco Wegner
 */
public class ExiComment extends ExiEvent {

    // --------------------------------------------------------------------
    // Member variables
    // ------------------------------------------------------------------------

    /**
     * The comment text.
     */
    private final String text;

    // --------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /**
     * Constructs an EXI comment (CM) event.
     *
     * @param text The event's actual data.
     */
    public ExiComment(String text) {
        super( );
        this.text = text;
    }

    // --------------------------------------------------------------------

    /**
     * Creates a new EXI Comment (CM) event.
     *
     * @param comment The XML stream event this EXI Comment (CM) event will be
     *        created from.
     */
    public ExiComment(Comment comment) {
        this.text = comment.getText( );
    }

    // --------------------------------------------------------------------
    // Methods
    // --------------------------------------------------------------------

    /**
     * Returns this comment event's actual text.
     *
     * @return This comment event's actual text.
     */
    public String getText( ) {
        return this.text;
    }

    // --------------------------------------------------------------------

    /* (non-Javadoc)
     * @see exi.events.ExiEvent#getEventType()
     */
    @Override
    public ExiEventType getEventType( ) {
        return ExiEventType.Comment;
    }
}
