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
package exi;

import java.net.URI;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;

import exi.ExiOptions.FidelityOption;
import exi.events.ExiAttribute;
import exi.events.ExiCharacters;
import exi.events.ExiComment;
import exi.events.ExiEndElement;
import exi.events.ExiEventCode;
import exi.events.ExiEventHandler;
import exi.events.ExiNamespaceDeclaration;
import exi.events.ExiStartElement;
import exi.grammar.ExiBuiltInGrammarFactory;
import exi.grammar.ExiExtensibleGrammar;
import exi.grammar.ExiGrammar;
import exi.grammar.ExiGrammarGroup;
import exi.grammar.ExiGrammarRule;
import exi.grammar.ExiGrammarGroup.Size;
import exi.io.ExiReader;
import exi.utils.ExiNamespaceTable;
import exi.utils.ExiStringTable;
import exi.utils.StringTablePartition;
import exi.utils.ValuePartition;

/**
 * This class represents a decoder for EXI streams.
 *
 * @author Marco Wegner
 */
public class ExiDecoder extends ExiEventHandler {

    // ------------------------------------------------------------------------
    // Static stuff
    // ------------------------------------------------------------------------

    /**
     * The EXI logger user for logging stuff.
     */
    private static Logger log = ExiLogger.getLogger(ExiDecoder.class);

    // ------------------------------------------------------------------------
    // Member variables
    // ------------------------------------------------------------------------

    /**
     * The EXI document builder which generates a DOM tree from a series of EXI
     * events.
     */
    private final ExiDocumentBuilder builder;

    // ------------------------------------------------------------------------

    /**
     * The stream reader for EXI streams.
     */
    private final ExiReader reader;

    // ------------------------------------------------------------------------

    /**
     * The namespace map.
     */
    private final ExiNamespaceTable table = new ExiNamespaceTable( );

    // ------------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------------

    /**
     * Constructs a new decoder for EXI streams.
     *
     * @param builder The EXI document builder which generates a DOM tree from a
     *        series of EXI events.
     * @param r The stream reader for EXI streams.
     * @param options The EXI options to use.
     * @throws Exception If something goes wrong during initialization.
     */
    public ExiDecoder(ExiDocumentBuilder builder, ExiReader r, ExiOptions options) throws Exception {
        super(options);
        this.builder = builder;
        this.reader = r;

        log.debug("--- EXI Decoder started");
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    /**
     * Starts the decoding process. This mainly decodes an EXI event code and
     * afterwards finds the matching rule from the grammar. From this rule an
     * EXI event is derived which is then used for handling the event itself and
     * the event content id appropriate.
     *
     * @throws ExiException If an error occurs while manipulating an EXI
     *         grammar.
     */
    public void decode( ) throws ExiException {
        // at least for now...
        setFactory(new ExiBuiltInGrammarFactory(getOptions( )));

        pushDocumentGrammar( );

        ExiGrammarRule rule;
        do {
            rule = decodeEventCode( );
            handleGrammarRule(rule);
        } while (!rule.getEventType( ).equals("ED"));
    }

    // ------------------------------------------------------------------------

    /**
     * Initiates the generation of an EXI event from a grammar rule. This
     * grammar rule has been selected beforehand as the matching one to the
     * previously read event code.
     *
     * @param rule The matching rule for the previously read event code.
     * @throws ExiException If an error occurs while manipulating an EXI
     *         grammar.
     */
    private void handleGrammarRule(ExiGrammarRule rule) throws ExiException {
        String eventType = rule.getEventType( );
        if (eventType.startsWith("AT")) {
            handleAttribute(rule);
        } else if (eventType.startsWith("SE")) {
            handleStartElement(rule);
        } else if (eventType.equals("ED")) {
            handleEndDocument(rule);
        } else if (eventType.equals("EE")) {
            handleEndElement(rule);
        } else if (eventType.equals("NS")) {
            handleNamespaceDeclaration(rule);
        } else if (eventType.equals("SD")) {
            handleStartDocument(rule);
        } else if (eventType.equals("CH")) {
            handleCharacters(rule);
        } else if (eventType.equals("CM")) {
            handleComment(rule);
        }
    }

    // ------------------------------------------------------------------------

    /**
     * Handles an EXI event decoded as the Comment event (CM).
     *
     * @param rule The EXI grammar rule which matched the decoded event code.
     * @throws ExiException If an error occurs while manipulating an EXI
     *         grammar.
     */
    private void handleComment(ExiGrammarRule rule) throws ExiException {
        if (!getOptions( ).isSet(FidelityOption.PRESERVE_COMMENTS)) {
            return;
        }

        log.debug(rule.getEventType( ));

        ExiComment event = new ExiComment(this.reader.readComment( ));
        this.builder.handle(event);

        getCurrentGrammar( ).moveToGroup(rule.getRightHandSide( ));
    }

    // ------------------------------------------------------------------------

    /**
     * Handles an EXI event decoded as the StartDocument event (SD).
     *
     * @param rule The EXI grammar rule which matched the decoded event code.
     * @throws ExiException If an error occurs while manipulating an EXI
     *         grammar.
     */
    private void handleStartDocument(ExiGrammarRule rule) throws ExiException {
        log.debug(rule.getEventType( ));

        getCurrentGrammar( ).moveToGroup(rule.getRightHandSide( ));
    }

    // ------------------------------------------------------------------------

    /**
     * Handles an EXI event decoded as the StartElement event (SE).
     *
     * @param rule The EXI grammar rule which matched the decoded event code.
     * @throws ExiException If an error occurs while manipulating an EXI
     *         grammar.
     */
    private void handleStartElement(ExiGrammarRule rule) throws ExiException {
        String eventType = rule.getEventType( );
        log.debug(eventType);

        boolean general = rule.isGeneral( );
        String uri, localPart, prefix;
        if (general) {
            uri = decodeURI( );
            localPart = decodeLocalName(uri);
        } else {
            uri = getCurrentQName( ).getNamespaceURI( );
            localPart = eventType.substring(3, eventType.length( ) - 1);
        }
        prefix = this.table.getNamespacePrefix(URI.create(uri));
        QName qname = new QName(uri, localPart, prefix);

        pushQName(qname);
        ExiStartElement event = new ExiStartElement(qname);
        this.builder.handle(event);

        ExiGrammar g = getCurrentGrammar( );
        if ((g instanceof ExiExtensibleGrammar) && general) {
            extendGrammar(g, rule, event);
        }

        g.moveToGroup(rule.getRightHandSide( ));
        pushElementGrammar(qname);
    }

    /**
     * Handles an EXI event decoded as the Attribute event (AT).
     *
     * @param rule The EXI grammar rule which matched the decoded event code.
     * @throws ExiException If an error occurs while manipulating an EXI
     *         grammar.
     */
    private void handleAttribute(ExiGrammarRule rule) throws ExiException {
        String eventType = rule.getEventType( );
        log.debug(eventType);

        boolean general = rule.isGeneral( );

        String uri, localPart;
        if (general) {
            uri = decodeURI( );
            localPart = decodeLocalName(uri);
        } else {
            uri = "";
            localPart = eventType.substring(3, eventType.length( ) - 1);
        }
        QName qname = new QName(uri, localPart);

        String value = decodeValue(qname);

        ExiAttribute event = new ExiAttribute(qname, value);

        this.builder.handle(event);

        ExiGrammar g = getCurrentGrammar( );
        if ((g instanceof ExiExtensibleGrammar) && general) {
            extendGrammar(g, rule, event);
        }

        g.moveToGroup(rule.getRightHandSide( ));
    }

    // ------------------------------------------------------------------------

    /**
     * Handles an EXI event decoded as the Namespace event (NS).
     *
     * @param rule The EXI grammar rule which matched the decoded event code.
     * @throws ExiException If an error occurs while manipulating an EXI
     *         grammar.
     */
    private void handleNamespaceDeclaration(ExiGrammarRule rule) throws ExiException {
        if (!getOptions( ).isSet(FidelityOption.PRESERVE_PREFIXES)) {
            return;
        }

        log.debug(rule.getEventType( ));

        String uri = decodeURI( );
        String prefix = decodeOptimizedForHits(getStringTable( ).getPrefixPartition( ), "prefix");

        ExiNamespaceDeclaration event = new ExiNamespaceDeclaration(prefix, URI.create(uri));
        this.builder.handle(event);

        getCurrentGrammar( ).moveToGroup(rule.getRightHandSide( ));
    }

    // ------------------------------------------------------------------------

    /**
     * Handles an EXI event decoded as the Characters event (CH).
     *
     * @param rule The EXI grammar rule which matched the decoded event code.
     * @throws ExiException If an error occurs while manipulating an EXI
     *         grammar.
     */
    private void handleCharacters(ExiGrammarRule rule) throws ExiException {
        String eventType = rule.getEventType( );
        log.debug(eventType);

        String value = decodeValue(getCurrentQName( ));
        ExiCharacters event = new ExiCharacters(value);
        this.builder.handle(event);

        ExiGrammar g = getCurrentGrammar( );
        if ((g instanceof ExiExtensibleGrammar) && rule.getEventCode( ).getLength( ) > 1) {
            extendGrammar(g, rule, event);
        }

        getCurrentGrammar( ).moveToGroup(rule.getRightHandSide( ));
    }

    // ------------------------------------------------------------------------

    /**
     * Handles an EXI event decoded as the EndElement event (EE).
     *
     * @param rule The EXI grammar rule which matched the decoded event code.
     * @throws ExiException If an error occurs while manipulating an EXI
     *         grammar.
     */
    private void handleEndElement(ExiGrammarRule rule) throws ExiException {
        log.debug(rule.getEventType( ));

        this.builder.handle(new ExiEndElement( ));

        popQName( );
        popGrammar( );
    }

    // ------------------------------------------------------------------------

    /**
     * Handles an EXI event decoded as the StartDocument event (SD).
     *
     * @param rule The EXI grammar rule which matched the decoded event code.
     * @throws ExiException If an error occurs while manipulating an EXI
     *         grammar.
     */
    private void handleEndDocument(ExiGrammarRule rule) throws ExiException {
        log.debug(rule.getEventType( ));
    }

    // ------------------------------------------------------------------------

    /**
     * Decodes an EXI event code and retrieves the matching rule from the
     * grammar. The actual length of the event code is determined by gradually
     * constructing event codes of lengths 1, 2 and 3 and test whether they have
     * distinct matches in the active grammar.
     *
     * @return The matching grammar rule.
     * @throws ExiException If something goes wrong during grammar manipulation
     *         or event code generation.
     */
    private ExiGrammarRule decodeEventCode( ) throws ExiException {
        ExiGrammarGroup activeGroup = getCurrentGrammar( ).getActiveGroup( );
        Size groupSize = activeGroup.getGroupSize( );

        ExiGrammarRule rule;

        int p1 = this.reader.readEventCodePart(groupSize.getPartSize(0));
        rule = activeGroup.getMatchingRule(new ExiEventCode(p1));
        if (rule == null) {
            int p2 = this.reader.readEventCodePart(groupSize.getPartSize(1));
            rule = activeGroup.getMatchingRule(new ExiEventCode(p1, p2));
            if (rule == null) {
                int p3 = this.reader.readEventCodePart(groupSize.getPartSize(2));
                rule = activeGroup.getMatchingRule(new ExiEventCode(p1, p2, p3));
            }
        }

        return rule;
    }

    // ------------------------------------------------------------------------

    /**
     * Decodes an URI from the EXI stream. The URI string table partition is
     * queried upon recurring URIs
     *
     * @return The decoded URI.
     */
    private String decodeURI( ) {
        return decodeOptimizedForHits(getStringTable( ).getUriPartition( ), "uri");
    }

    // ------------------------------------------------------------------------

    /**
     * Decodes a string from a string table partition optimized for hits (or
     * optimized for frequent use of compact identifiers).
     *
     * @param part The string table partition to be queried.
     * @param msg An additional log message.
     * @return The decoded string.
     */
    private String decodeOptimizedForHits(StringTablePartition part, String msg) {
        int code = this.reader.readCompactStringCode(part.getSize( ));
        if (code == 0) {
            log.debug(String.format("%s miss", msg));
            String s = this.reader.readCompactString( );
            part.add(s);
            return s;
        }

        log.debug(String.format("%s hit", msg));
        return part.getValue(code - 1);
    }

    // ------------------------------------------------------------------------

    /**
     * Decodes a local name string from the EXI string. The string table
     * partition for local names is queried if this string has already been
     * encountered.
     *
     * @param namespaceURI The currently active namespace URI.
     * @return The decoded local name.
     */
    private String decodeLocalName(String namespaceURI) {
        StringTablePartition part = getStringTable( ).getLocalNamesPartition(namespaceURI);
        int code = this.reader.readLocalNameCode( );
        if (code == 0) {
            log.debug("local name hit");
            return part.getValue(this.reader.readLocalNameIndex(part.getSize( )));
        }

        log.debug("local name miss");
        String s = this.reader.readLocalName(code);
        part.add(s);
        return s;
    }

    // ------------------------------------------------------------------------

    /**
     * Decodes a value string from the EXI stream. The local and global string
     * table partition is queried if the string has already been encountered.
     *
     * @param qname The enclosing element's QName.
     * @return The decoded value string.
     */
    private String decodeValue(QName qname) {
        int code = this.reader.readValueStringCode(qname);
        ExiStringTable table = getStringTable( );
        ValuePartition local = table.getValuePartition(qname);
        ValuePartition global = table.getValuePartition( );

        if (code == 0) {
            // value is found in the local table
            log.debug("value hit");
            return local.getValue(this.reader.readValueStringIndex(qname, local.getSize( )));
        }

        if (code == 1) {
            // value is found in the global table
            log.debug("global-value hit");
            return global.getValue(this.reader.readValueStringIndex(qname, global.getSize( )));
        }

        log.debug("(value miss)");
        String s = this.reader.readValueString(qname, code);
        local.add(s);
        global.add(s);
        return s;
    }
}
