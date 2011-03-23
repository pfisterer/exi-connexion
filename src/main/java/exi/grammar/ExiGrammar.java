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

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import exi.ExiException;
import exi.ExiLogger;
import exi.events.ExiEvent;
import exi.events.ExiEventCode;
import exi.events.ExiMalformedEventCodeException;

/**
 * This class represents an abstract EXI grammar.
 *
 * @author Marco Wegner
 */
public abstract class ExiGrammar {

    // ------------------------------------------------------------------------
    // Static stuff
    // ------------------------------------------------------------------------

    /**
     * The EXI logger user for logging stuff.
     */
    private static Logger log = ExiLogger.getLogger(ExiGrammar.class);

    // ------------------------------------------------------------------------
    // Member variables
    // ------------------------------------------------------------------------

    /**
     * This map groups the production rules together. Production rules are
     * grouped together if they share the same left-hand side.
     */
	private final Map<String,ExiGrammarGroup> groups;

    // ------------------------------------------------------------------------

	/**
     * The rule group currently in use.
     */
	private ExiGrammarGroup activeGroup = null;

    // ------------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------------

	/**
     * <p>
     * Creates a new EXI grammar.
     * </p>
     */
	public ExiGrammar( ) {
	    this.groups = new HashMap<String,ExiGrammarGroup>( );
	}

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

	/**
     * <p>
     * Adds a new production rule to this grammar.
     * </p>
     *
     * @param leftHandSide The left-hand side of the rule.
     * @param rightHandSide The right-hand side of the rule.
     * @param eventType The rule's event type.
     * @param code The rule's event code.
     */
	public void append(String leftHandSide, String rightHandSide, String eventType, ExiEventCode code) {
        ExiGrammarGroup group = this.groups.get(leftHandSide);
        if (group == null) {
            group = new ExiGrammarGroup(leftHandSide);
            this.groups.put(leftHandSide, group);
        }
        group.append(new ExiGrammarRule(eventType, rightHandSide, code));
	}

    // ------------------------------------------------------------------------

	/**
     * <p>
     * Adds a new production rule to this grammar. This is a convenience
     * method.
     * </p>
     *
     * @param leftHandSide The rule's left-hand side.
     * @param rightHandSide The rule's right-hand side.
     * @param eventType The rule's event type.
     * @param code The rule's event code as a string.
     * @throws ExiMalformedEventCodeException If something goes wrong while
     *         parsing the specified event code string.
     */
	public void append(String leftHandSide, String rightHandSide, String eventType, String code) throws ExiMalformedEventCodeException {
	    append(leftHandSide, rightHandSide, eventType, new ExiEventCode(code));
	}

    // ------------------------------------------------------------------------

	/**
     * <p>
     * Prepends a rule to the active grammar group.
     * </p>
     * <p>
     * The new rule is constructed from the specified right-hand side and event
     * type. It will get the event code 0. The other rules in this group will
     * have the respective first part of their event codes incremented by one.
     * </p>
     *
     * @param rightHandSide The new rule's right-hand side.
     * @param eventType The new rule's event type.
     * @throws ExiException If the event code for new rule cannot be
     *         constructed (shouldn't usually happen).
     */
	public void prepend(String rightHandSide, String eventType) throws ExiException {
	    ExiGrammarRule rule = new ExiGrammarRule(eventType, rightHandSide, new ExiEventCode(0));
	    this.activeGroup.prepend(rule);
	}

    // ------------------------------------------------------------------------

    /**
     * <p>
     * Move to the default grammar group.
     * </p>
     *
     * @param name The default group's name.
     * @throws ExiUnknownGroupException If no such group exists in this
     *         grammar.
     */
	public void setInitialGroup(String name) throws ExiUnknownGroupException {
	    moveToGroup(name);
	}

    // ------------------------------------------------------------------------

	/**
     * <p>
     * Move to next grammar group.
     * </p>
     *
     * @param name The next group's name.
     * @throws ExiUnknownGroupException If no such group exists in this
     *         grammar.
     */
	public void moveToGroup(String name) throws ExiUnknownGroupException {

	    ExiGrammarGroup group = this.groups.get(name);
	    String agn = null; // the active group's name
	    if (this.activeGroup != null) {
            agn = this.activeGroup.getName( );
        }
        if (group == null) {
            throw new ExiUnknownGroupException(name);
        }

        if (agn != null && !name.equals(agn)) {
            log.debug(String.format("Grammar moves on to group %s", name));
        }

        this.activeGroup = group;
	}

    // ------------------------------------------------------------------------

	/**
     * </p>
     * Returns the matching grammar rule for the specified event.
     * </p>
     *
     * @param e The current EXI event.
     * @return The matching rule for the event.
     * @throws ExiException If no matching rule can be found in the grammar.
     */
	public ExiGrammarRule getMatchingRule(ExiEvent e) throws ExiException {
        return this.activeGroup.getMatchingRule(e);
    }

    // ------------------------------------------------------------------------

    /**
     * Returns the current (active) grammar group.
     *
     * @return The active grammar group.
     */
    public ExiGrammarGroup getActiveGroup( ) {
        return this.activeGroup;
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    /**
     * <p>
     * Returns a string representation of this grammar. The output is similar
     * to that used in the
     * {@link <a href="http://www.w3.org/TR/exi/">EXI documentation</a>}.
     * </p>
     */
	@Override
	public String toString( ) {
	    StringBuffer sb = new StringBuffer( );
	    for (Map.Entry<String,ExiGrammarGroup> e : this.groups.entrySet( )) {
            sb.append(e.getValue( ).toString( ));
        }
	    return sb.toString( );
	}
}
