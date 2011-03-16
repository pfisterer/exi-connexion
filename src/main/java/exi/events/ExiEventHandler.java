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

import java.util.Stack;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;

import exi.ExiException;
import exi.ExiLogger;
import exi.ExiOptions;
import exi.grammar.ExiBuiltInGrammarFactory;
import exi.grammar.ExiExtensibleGrammar;
import exi.grammar.ExiGrammar;
import exi.grammar.ExiGrammarFactory;
import exi.grammar.ExiGrammarRule;
import exi.utils.ExiStringTable;

/**
 * This is the abstract base class for the EXI encoder and decoder.
 *
 * @author Marco Wegner
 */
public abstract class ExiEventHandler {

    // ------------------------------------------------------------------------
    // Static stuff
    // ------------------------------------------------------------------------

    /**
     * The EXI logger user for logging stuff.
     */
    private static Logger log = ExiLogger.getLogger(ExiEventHandler.class);

    // ------------------------------------------------------------------------
    // Member variables
    // ------------------------------------------------------------------------

    /**
     * The factory for creating new grammars.
     */
    private ExiGrammarFactory factory;

    // ------------------------------------------------------------------------

    /**
     * The EXI grammar stack. Grammars remain on the stack until their
     * execution has terminated.
     */
    private final Stack<ExiGrammar> grammarStack = new Stack<ExiGrammar>( );

    // ------------------------------------------------------------------------

    /**
     * The string table.
     */
    private final ExiStringTable table = new ExiStringTable( );

    // ------------------------------------------------------------------------

    /**
     * The stack of QNames.
     */
    private final Stack<QName> qnameStack = new Stack<QName>( );

    // ------------------------------------------------------------------------

    /**
     * The EXI options used for handling events.
     */
    private final ExiOptions options;

    // ------------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------------

    /**
     * Constructs a new EXI event handler.
     *
     * @param options The EXI options to use.
     * @throws ExiException If something goes wrong during grammar
     *         initialization.
     */
    protected ExiEventHandler(ExiOptions options) throws ExiException {
        super( );
        this.options = options;
        // at least for now...
        setFactory(new ExiBuiltInGrammarFactory(getOptions( )));
        pushDocumentGrammar( );
    }

    // ------------------------------------------------------------------------
    // Setters and getters
    // ------------------------------------------------------------------------

    /**
     * Sets the factory used for grammar creation.
     *
     * @param factory The factory.
     */
    protected void setFactory(ExiGrammarFactory factory) {
        this.factory = factory;
    }

    // ------------------------------------------------------------------------

    /**
     * Returns the grammar factory.
     *
     * @return The grammar factory.
     */
    protected ExiGrammarFactory getFactory( ) {
        return this.factory;
    }

    // ------------------------------------------------------------------------

    /**
     * Returns the stack of grammars.
     *
     * @return The stack of grammars.
     */
    private Stack<ExiGrammar> getGrammarStack( ) {
        return this.grammarStack;
    }

    // ------------------------------------------------------------------------

    /**
     * Returns the string table instance.
     *
     * @return The string table instance.
     */
    protected ExiStringTable getStringTable( ) {
        return this.table;
    }

    // ------------------------------------------------------------------------

    /**
     * Returns the stack of QNames.
     *
     * @return The stack of QNames.
     */
    protected Stack<QName> getQNameStack( ) {
        return this.qnameStack;
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    /**
     * Extends the specified grammar.
     *
     * TODO: this surely works with fewer parameters as well!!!
     *
     * @param g The grammar.
     * @param rule The new rule.
     * @param e The event.
     * @throws ExiException If something goes wrong during grammar manipulation.
     */
    protected void extendGrammar(ExiGrammar g, ExiGrammarRule rule, ExiEvent e)
            throws ExiException {
                ((ExiExtensibleGrammar)g).extend(rule.getRightHandSide( ), e.getEventTypeString( ));
                log.debug(String.format(
                        "Grammar group %s extended by a leading %s:\n%s",
                        g.getActiveGroup( ).getName( ),
                        e.getEventTypeString( ),
                        g.toString( )
                ));
            }

    // ------------------------------------------------------------------------

    /**
     * Pushes the document grammar on the grammar stack.
     *
     * @throws ExiException If something goes wrong during grammar creation.
     */
    protected void pushDocumentGrammar( ) throws ExiException {
        getGrammarStack( ).push(getFactory( ).createDocumentGrammar( ));
    }

    // ------------------------------------------------------------------------

    /**
     * Pushes an element grammar on the grammar stack. The element is identified
     * by the specified QName.
     *
     * @param qname The element's qualified name.
     * @throws ExiException If something goes wrong during grammar creation.
     */
    protected void pushElementGrammar(QName qname) throws ExiException {
        getGrammarStack( ).push(getFactory( ).createElementGrammar(qname));
        log.debug(String.format(
                "Element grammar for \"%s\" pushed on the rule stack.",
                qname.getLocalPart( )
        ));
    }

    // ------------------------------------------------------------------------

    /**
     * Pops the top-most grammar from the grammar stack.
     */
    protected void popGrammar( ) {
        getGrammarStack( ).pop( );
        log.debug("Current rule popped from the rule stack.");
    }

    // ------------------------------------------------------------------------

    /**
     * Retrieves the top-most grammar from the grammar stack.
     *
     * @return The currently active grammar.
     */
    protected ExiGrammar getCurrentGrammar( ) {
        return getGrammarStack( ).peek( );
    }

    // ------------------------------------------------------------------------

    /**
     * Pushes the specified QName on the QName stack.
     *
     * @param qname The qualified name.
     */
    protected void pushQName(QName qname) {
        getQNameStack( ).push(qname);
    }

    // ------------------------------------------------------------------------

    /**
     * Pops the top-most qualified name from the QName stack.
     */
    protected void popQName( ) {
        getQNameStack( ).pop( );
    }

    // ------------------------------------------------------------------------

    /**
     * Retrieves the top-most qualified name from the QName stack.
     *
     * @return The currently active QName.
     */
    protected QName getCurrentQName( ) {
        return getQNameStack( ).peek( );
    }

    // ------------------------------------------------------------------------

    /**
     * Returns the options used here.
     *
     * @return The options instance.
     */
    protected ExiOptions getOptions( ) {
        return this.options;
    }
}
