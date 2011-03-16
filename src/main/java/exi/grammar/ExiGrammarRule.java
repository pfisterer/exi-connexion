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
package exi.grammar;

import exi.events.ExiEvent;
import exi.events.ExiEventCode;

/**
 * This class represents a single production rule from an EXI grammar.
 *
 * @author Marco Wegner
 */
public class ExiGrammarRule {

    // --------------------------------------------------------------------
    // Member variables
    // --------------------------------------------------------------------

	/**
     * <p>
     * This rule's right-hand side. The right-hand size can be viewed as
     * the next grammar block to be entered.
     * </p>
     */
	private final String rightHandSide;

    // --------------------------------------------------------------------

	/**
     * <p>
     * This rule's event type.
     * </p>
     */
	private final String eventType;

    // --------------------------------------------------------------------

	/**
     * <p>
     * This rule's event code.
     * </p>
     */
	private final ExiEventCode code;

    // --------------------------------------------------------------------
    // Constructor
    // --------------------------------------------------------------------

	/**
     * <p>
     * Creates a new production rule.
     * </p>
     *
     * @param eventType This rule's event type.
     * @param rightHandSide The right-hand side of the rule.
     * @param code The rule's event code.
     */
	public ExiGrammarRule(String eventType, String rightHandSide, ExiEventCode code) {
		super( );
        this.rightHandSide = rightHandSide;
        this.eventType = eventType;
        this.code = code;
	}

    // --------------------------------------------------------------------
    // Methods
    // --------------------------------------------------------------------

	/**
     * <p>
     * Returns the right-hand side of this rule.
     * </p>
     *
     * @return The right-hand side of this rule.
     */
    public String getRightHandSide( ) {
        return this.rightHandSide;
    }

    // --------------------------------------------------------------------

	/**
     * Returns this rule's event type.
     *
     * @return This rule's event type.
     */
    public String getEventType( ) {
        return this.eventType;
    }

    // --------------------------------------------------------------------

	/**
     * Returns this rule's event code.
     *
     * @return This rule's event code.
     */
    public ExiEventCode getEventCode( ) {
        return this.code;
    }

    // ------------------------------------------------------------------------

    /**
     * <p>
     * Specifies whether this rule matches the specified event.
     * </p>
     *
     * @param e The event.
     * @return <code>true</code> if this rule matches the event, else
     *         <code>false</code>.
     */
    public boolean matches(ExiEvent e) {
        String s = this.eventType;
        s = s.replaceAll("\\(", "\\\\(");
        s = s.replaceAll("\\)", "\\\\)");
        s = s.replaceAll("\\*", ".*");
        return e.getEventTypeString( ).matches(s.replace("(*)", "\\(.*\\)"));
    }

    // ------------------------------------------------------------------------

    /**
     * <p>
     * Specified whether this grammar rule is a general one or not
     * </p>
     * <p>
     * General rule match a certain set of events and not just one. For
     * example, SE(*) matches any SE event while, on the hand, SE(note) only
     * matches the SE event for note.
     * </p>
     *
     * @return <code>true</code> if this rule is general, else
     *         <code>false</code>.
     */
    public boolean isGeneral( ) {
        return (this.eventType.endsWith("(*)"));
    }
}
