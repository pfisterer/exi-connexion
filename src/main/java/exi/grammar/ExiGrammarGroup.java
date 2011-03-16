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
package exi.grammar;

import java.util.LinkedList;

import exi.ExiException;
import exi.events.ExiEvent;
import exi.events.ExiEventCode;

/**
 * This class represents a single group of rules in an EXI grammar.
 *
 * @author Marco Wegner
 */
public class ExiGrammarGroup {

    // ------------------------------------------------------------------------
    // Nested types
    // ------------------------------------------------------------------------

    /**
     * This class represents the size of this grammar group. This size is
     * evaluated as the number of distinct values for each part.
     *
     * @author Marco Wegner
     */
    public class Size {
        /** The part sizes. */
        private int parts[] = new int[3];

        /** Constructs a new size instance. */
        Size( ) {
            ExiEventCode eventCode = ExiGrammarGroup.this.rules.getLast( ).getEventCode( );
            for (int i = 0; i < 3; i++) {
                this.parts[i] = eventCode.getPart(i) + 1;
            }
        }

        /**
         * Returns the size for the specified part number.
         *
         * @param n The part number (0 <= n <= 2).
         * @return The size of the specified part.
         */
        public int getPartSize(int n) {
            if (n < 0 || n > 2) {
                throw new IllegalArgumentException("Part number must be either 0, 1, or 2!");
            }
            return this.parts[n];
        }
    }

    // ------------------------------------------------------------------------
    // Member variables
    // ------------------------------------------------------------------------

    /**
     * This group's name.
     */
    private final String name;

    // ------------------------------------------------------------------------

    /**
     * The list of rules in this group.
     */
    private final LinkedList<ExiGrammarRule> rules = new LinkedList<ExiGrammarRule>( );

    // ------------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------------

    /**
     * Creates a new EXI grammar group.
     *
     * @param name This group's new name.
     */
    public ExiGrammarGroup(String name) {
        this.name = name;
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    /**
     * Returns this group's name.
     *
     * @return This group's name.
     */
    public String getName( ) {
        return this.name;
    }

    // ------------------------------------------------------------------------

    /**
     * Appends a rule to this grammar group.
     *
     * @param rule The new rule.
     */
    public void append(ExiGrammarRule rule) {
        this.rules.add(rule);
    }

    // ------------------------------------------------------------------------

    /**
     * Prepends a rule in this grammar group.
     *
     * @param rule The new rule.
     */
    public void prepend(ExiGrammarRule rule) {
        for (ExiGrammarRule r : this.rules) {
            r.getEventCode( ).increment(0);
        }
        this.rules.add(0, rule);
    }

    // ------------------------------------------------------------------------

    /**
     * </p>
     * Returns the matching grammar rule for the specified event.
     * </p>
     * <p>
     * If no such rule can be found, this usually signifies a faulty grammar or
     * a faulty implementation. In that case an exception is thrown.
     * </p>
     *
     * @param e The current EXI event.
     * @return The matching rule for the event.
     * @throws ExiException If no matching rule can be found in the grammar.
     */
    public ExiGrammarRule getMatchingRule(ExiEvent e) throws ExiException {
        ExiGrammarRule result = null;
        for (ExiGrammarRule rule : this.rules) {
            if (rule.matches(e)) {
                result = rule;
                break;
            }
        }

        if (result == null) {
            throw new ExiNoMatchingRuleException(e.toString( ));
        }

        return result;
    }

    // ------------------------------------------------------------------------

    /**
     * Returns the matching rule for the specified EXI event code.
     *
     * @param code The EXI event code.
     * @return The matching grammar rule or <code>null</code> if no such rule
     *         exists.
     */
    public ExiGrammarRule getMatchingRule(ExiEventCode code) {
        ExiGrammarRule result = null;
        for (ExiGrammarRule rule : this.rules) {
            if (rule.getEventCode( ).equals(code)) {
                result = rule;
                break;
            }
        }

        return result;
    }

    // ------------------------------------------------------------------------

    /**
     * Returns the group size instance for this EXI grammar group.
     *
     * @return The group size instance.
     */
    public Size getGroupSize( ) {
        return new Size();
    }

    // ------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString( ) {
        StringBuffer sb = new StringBuffer( );
        sb .append(String.format("%s:\n", getName( )));

        for (ExiGrammarRule rule : this.rules) {
            sb.append(String.format("\t%-16s %-40s %s\n",
                    rule.getEventType( ),
                    rule.getRightHandSide( ),
                    rule.getEventCode( ).toString( )
            ));
        }
        sb.append("\n");
        return sb.toString( );
    }
}
