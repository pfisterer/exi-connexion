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

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;

import exi.ExiException;
import exi.ExiLogger;
import exi.ExiOptions;
import exi.ExiOptions.FidelityOption;
import exi.events.ExiEventCodeGenerator;

/**
 * <p>
 * Factory for built-in EXI grammars.
 * </p>
 *
 * @author Marco Wegner
 */
public class ExiBuiltInGrammarFactory implements ExiGrammarFactory {

    // ------------------------------------------------------------------------
    // Static stuff
    // ------------------------------------------------------------------------

    /**
     * The EXI logger user for logging stuff.
     */
    private static Logger log = ExiLogger.getLogger(ExiBuiltInGrammarFactory.class);

    // ------------------------------------------------------------------------
    // Member variables
    // ------------------------------------------------------------------------

    /**
     * A store for keeping already generated grammars so that they can be
     * reused.
     */
    private Map<String, ExiGrammar> store = new HashMap<String, ExiGrammar>( );

    // ------------------------------------------------------------------------

    /**
     * The EXI options.
     */
    private final ExiOptions options;

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    /**
     * Creates a new factory for creating EXI built-in grammars.
     *
     * @param options The EXI options used for grammar creation.
     */
    public ExiBuiltInGrammarFactory(ExiOptions options) {
        this.options = options;
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    /**
     * <p>
     * Creates an EXI Buit-in Document Grammar as described in
     * {@link <a href="http://www.w3.org/TR/exi/#builtinDocGrammars">Section 9.4.1</a>}
     * of the EXI documentation.
     * </p>
     */
    public ExiGrammar createDocumentGrammar( ) throws ExiException {

        ExiGrammar eg = new ExiDocumentGrammar( );
        ExiEventCodeGenerator g = new ExiEventCodeGenerator( );

        String start = "Document";
        String content = "DocContent";
        String end = "DocEnd";

        eg.append(start, content, "SD", g.getNextCode(1));

        g.reset( );

        eg.append(content, end, "SE(*)", g.getNextCode(1));
        if (this.options.isSet(FidelityOption.PRESERVE_DTDS)) {
            eg.append(content, content, "DT", g.getNextCode(2));
        }
        if (this.options.isSet(FidelityOption.PRESERVE_COMMENTS)) {
            eg.append(content, content, "CM", g.getNextCode(3));
        }
        if (this.options.isSet(FidelityOption.PRESERVE_PROCESSING_INSTRUCTIONS)) {
            eg.append(content, content, "PI", g.getNextCode(3));
        }
        g.reset( );

        eg.append(end, "", "ED", g.getNextCode(1));
        if (this.options.isSet(FidelityOption.PRESERVE_COMMENTS)) {
            eg.append(end, end, "CM", g.getNextCode(2));
        }
        if (this.options.isSet(FidelityOption.PRESERVE_PROCESSING_INSTRUCTIONS)) {
            eg.append(end, end, "PI", g.getNextCode(2));
        }
        eg.setInitialGroup(start);

        return eg;
    }

    // ------------------------------------------------------------------------

    /**
     * <p>
     * Creates an EXI Buit-in Fragment Grammar as described in
     * {@link <a href="http://www.w3.org/TR/exi/#builtinFragGrammars">Section 9.4.2</a>}
     * of the EXI documentation.
     * </p>
     */
    public ExiGrammar createFragmentGrammar( ) throws ExiException {

        ExiGrammar eg = new ExiFragmentGrammar( );
        ExiEventCodeGenerator g = new ExiEventCodeGenerator( );

        String start = "Fragment";
        String content = "FragmentContent";

        eg.append(start, content, "SD", g.getNextCode(1));

        g.reset( );

        eg.append(content, content, "SE(*)", g.getNextCode(1));
        eg.append(content, "", "ED", g.getNextCode(1));
        if (this.options.isSet(FidelityOption.PRESERVE_COMMENTS)) {
            eg.append(content, content, "CM", g.getNextCode(2));
        }
        if (this.options.isSet(FidelityOption.PRESERVE_PROCESSING_INSTRUCTIONS)) {
            eg.append(content, content, "PI", g.getNextCode(2));
        }
        eg.setInitialGroup(start);

        return eg;
    }

    // ------------------------------------------------------------------------

    /**
     * <p>
     * Creates an EXI Built-in Element Grammar.
     * </p>
     *
     * <p>
     * The grammar is described in
     * {@link <a href="http://www.w3.org/TR/exi/#builtinElemGrammars">Section 9.4.3</a>}
     * of the EXI documentation.
     * </p>
     */
    public ExiGrammar createElementGrammar(QName qname) throws ExiException {

        String key = generateMapKey(qname);
        ExiGrammar eg;

        String qns = qname.toString( );
        if (this.store.containsKey(key)) {
            eg = this.store.get(key);
            log.debug(String.format(
                    "Element grammar for %s retrieved from the store.", qns));
        } else {
            eg = buildElementGrammar(qname);
            this.store.put(key, eg);
            log.debug(String.format(
                    "Element grammar for %s added to the store.", qns));
        }

        eg.setInitialGroup("StartTag" + capitalize(qname.getLocalPart( )));
        return eg;
    }

    // ------------------------------------------------------------------------

    /**
     * <p>
     * Builds a new built-in element grammar.
     * </p>
     *
     * @param qname The element's QName.
     * @return The newly generated element grammar.
     * @throws ExiException If something goes wrong during event code
     *         generation.
     */
    private ExiGrammar buildElementGrammar(QName qname) throws ExiException {

        ExiGrammar eg = new ExiElementGrammar( );
        ExiEventCodeGenerator g = new ExiEventCodeGenerator( );

        String start = "StartTag" + capitalize(qname.getLocalPart( ));
        String content = "Element" + capitalize(qname.getLocalPart( ));

        eg.append(start, "", "EE", g.getNextCode(2));
        eg.append(start, start, "AT(*)", g.getNextCode(2));
        if (this.options.isSet(FidelityOption.PRESERVE_PREFIXES)) {
            eg.append(start, start, "NS", g.getNextCode(2));
        }
        eg.append(start, content, "SE(*)", g.getNextCode(2));
        eg.append(start, content, "CH", g.getNextCode(2));
        if (this.options.isSet(FidelityOption.PRESERVE_DTDS)) {
            eg.append(start, content, "ER", g.getNextCode(2));
        }
        if (this.options.isSet(FidelityOption.PRESERVE_COMMENTS)) {
            eg.append(start, content, "CM", g.getNextCode(3));
        }
        if (this.options.isSet(FidelityOption.PRESERVE_PROCESSING_INSTRUCTIONS)) {
            eg.append(start, content, "PI", g.getNextCode(3));
        }

        g.reset( );

        eg.append(content, "", "EE", g.getNextCode(1));
        eg.append(content, content, "SE(*)", g.getNextCode(2));
        eg.append(content, content, "CH", g.getNextCode(2));
        if (this.options.isSet(FidelityOption.PRESERVE_DTDS)) {
            eg.append(content, content, "ER", g.getNextCode(2));
        }
        if (this.options.isSet(FidelityOption.PRESERVE_COMMENTS)) {
            eg.append(content, content, "CM", g.getNextCode(3));
        }
        if (this.options.isSet(FidelityOption.PRESERVE_PROCESSING_INSTRUCTIONS)) {
            eg.append(content, content, "PI", g.getNextCode(3));
        }

        return eg;
    }

    // ------------------------------------------------------------------------
    // Helper methods
    // ------------------------------------------------------------------------

    /**
     * <p>
     * Generates a map key from an element QName. this key is then used to
     * identify grammars which are stored in a map.
     * </p>
     *
     * @param qname The element's QName.
     * @return The generated key.
     */
    private String generateMapKey(QName qname) {
        StringBuffer sb = new StringBuffer( );
        if (!qname.getPrefix( ).isEmpty( )) {
            sb.append(qname.getPrefix( ));
            sb.append(":");
        }
        sb.append(qname.getLocalPart( ));
        return sb.toString( );
    }

    // ------------------------------------------------------------------------

    /**
     * <p>
     * Capitalizes the specified string.
     * </p>
     *
     * @param s The string to be capitalized.
     * @return The capitalized string.
     */
    private String capitalize(String s) {
        return String.valueOf(s.charAt(0)).toUpperCase( ) + s.substring(1);
    }
}
