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

import java.math.BigInteger;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;

import exi.ExiOptions.FidelityOption;
import exi.events.ExiAttribute;
import exi.events.ExiCharacters;
import exi.events.ExiComment;
import exi.events.ExiEndDocument;
import exi.events.ExiEndElement;
import exi.events.ExiEvent;
import exi.events.ExiEventCode;
import exi.events.ExiEventHandler;
import exi.events.ExiNamespaceDeclaration;
import exi.events.ExiProcessingInstruction;
import exi.events.ExiStartDocument;
import exi.events.ExiStartElement;
import exi.grammar.ExiExtensibleGrammar;
import exi.grammar.ExiGrammar;
import exi.grammar.ExiGrammarGroup;
import exi.grammar.ExiGrammarRule;
import exi.grammar.ExiGrammarGroup.Size;
import exi.io.ExiWriter;
import exi.utils.ExiStringTable;
import exi.utils.StringTablePartition;
import exi.utils.ValuePartition;

/**
 * This class represents an encoder for EXI events.
 *
 * @author Marco Wegner
 */
public class ExiEncoder extends ExiEventHandler {

    // ------------------------------------------------------------------------
    // Static stuff
    // ------------------------------------------------------------------------

    /**
     * The EXI logger user for logging stuff.
     */
    public static Logger log = ExiLogger.getLogger(ExiEncoder.class);

    // ------------------------------------------------------------------------
    // Member variables
    // ------------------------------------------------------------------------

    /**
     * The EXI stream writer.
     */
    private final ExiWriter writer;

    // ------------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------------

    /**
     * Creates a new EXI event handler.
     * @param w The EXI stream writer.
     * @param options The EXI options to use.
     *
     * @throws Exception If something goes wrong during initialization.
     */
    public ExiEncoder(ExiWriter w, ExiOptions options) throws Exception {
        super(options);
        this.writer = w;

        log.debug("---");
        log.debug("--- EXI Encoder started");
        log.debug("--- Bytes written to stream so far: " + w.getOutputStream( ).size( ));
        log.debug("---");
    }

    // ------------------------------------------------------------------------

    /**
     * Handles an EXI event.
     *
     * @param event The EXI event to be handled.
     * @throws ExiException If something goes wrong during event handling.
     */
    public void handle(ExiEvent event) throws ExiException {
        switch (event.getEventType( )) {
            case Attribute:
                handle((ExiAttribute)event);
                break;

            case EndDocument:
                handle((ExiEndDocument)event);
                break;

            case EndElement:
                handle((ExiEndElement)event);
                break;

            case NamespaceDeclaration:
                handle((ExiNamespaceDeclaration)event);
                break;
                
            case ProcessingInstruction:
                handle((ExiProcessingInstruction)event);
                break;

            case StartDocument:
                handle((ExiStartDocument)event);
                break;

            case StartElement:
                handle((ExiStartElement)event);
                break;
        }
    }

    // ------------------------------------------------------------------------

    /**
     * Handles a Processing Instruction (PI) event.
     *
     * @param event The EXI Processing Instruction (PI) event to be handled.
     * @throws ExiException If something goes wrong during grammar handling.
     */
    public void handle(ExiProcessingInstruction event) throws ExiException {
        if (!getOptions( ).isSet(FidelityOption.PRESERVE_PROCESSING_INSTRUCTIONS)) {
            return;
        }
        String target = event.getTarget( );
        String data = event.getData( );
        log.debug(String.format("PI (target = %s; data = %s)", target, data));

        ExiGrammar g = getCurrentGrammar( );
        ExiGrammarRule rule = g.getMatchingRule(event);

        encodeEventCode(rule.getEventCode( ));

        g.moveToGroup(rule.getRightHandSide( ));
    }

    // ------------------------------------------------------------------------

    /**
     * Handles an EXI Start Document (SD) event.
     *
     * @param event The EXI Start Document (SD) event to be handled.
     * @throws ExiException If something goes wrong during grammar handling.
     */
    public void handle(ExiStartDocument event) throws ExiException {
        log.debug("SD");

        ExiGrammar g = getCurrentGrammar( );
        ExiGrammarRule rule = g.getMatchingRule(event);

        encodeEventCode(rule.getEventCode( ));

        g.moveToGroup(rule.getRightHandSide( ));
    }

    // ------------------------------------------------------------------------

    /**
     * Handles an EXI Start Element (SE) event.
     *
     * @param event The EXI Start Element (SE) event to be handled.
     * @throws ExiException If something goes wrong during grammar handling.
     */
    public void handle(ExiStartElement event) throws ExiException {

        log.debug(event.getEventTypeString( ));

        QName qname = event.getQualifiedName( );
        pushQName(qname);

        ExiGrammar g = getCurrentGrammar( );
        ExiGrammarRule rule = g.getMatchingRule(event);

        encodeEventCode(rule.getEventCode( ));

        if (rule.isGeneral( )) {

            encodeOptimizedForHits(getStringTable( ).getUriPartition( ), qname.getNamespaceURI( ), "uri");
            encodeLocalName(qname);

            if ((g instanceof ExiExtensibleGrammar)) {
                extendGrammar(g, rule, event);
            }
        }

        g.moveToGroup(rule.getRightHandSide( ));
        pushElementGrammar(qname);
    }

    // ------------------------------------------------------------------------

    /**
     * Handles an EXI Attribute (AT) event.
     *
     * @param event The EXI Attribute (AT) event to be handled.
     * @throws ExiException If something goes wrong during grammar handling.
     */
    public void handle(ExiAttribute event) throws ExiException {

        QName qname = event.getQualifiedName( );
        String value = event.getValue( );

        log.debug(String.format(
                "AT (name = %s, value = %s)",
                qname.getLocalPart( ),
                value
        ));

        ExiGrammar g = getCurrentGrammar( );
        ExiGrammarRule rule = g.getMatchingRule(event);

        encodeEventCode(rule.getEventCode( ));

        boolean general = rule.isGeneral( );
        if (general) {
            encodeOptimizedForHits(getStringTable( ).getUriPartition( ), qname.getNamespaceURI( ), "uri");
            encodeLocalName(qname);
        }

        encodeValue(qname, value);

        if ((g instanceof ExiExtensibleGrammar) && general) {
            extendGrammar(g, rule, event);
        }

        g.moveToGroup(rule.getRightHandSide( ));
    }

    // ------------------------------------------------------------------------

    /**
     * Handles an EXI Namespace Declaration (NS) event.
     *
     * @param event The EXI Namespace Declaration (NS) event to be handled.
     * @throws ExiException If something goes wrong during grammar handling.
     */
    public void handle(ExiNamespaceDeclaration event) throws ExiException {
        if (!getOptions( ).isSet(FidelityOption.PRESERVE_PREFIXES)) {
            return;
        }

        String prefix = event.getNamespacePrefix( );

        log.debug(String.format(
                "NS (prefix = %s, uri = %s)",
                prefix, event.getNamespaceURI( )
        ));

        ExiGrammar g = getCurrentGrammar( );
        ExiGrammarRule rule = g.getMatchingRule(event);

        encodeEventCode(rule.getEventCode( ));

        encodeOptimizedForHits(getStringTable( ).getUriPartition( ), event.getNamespaceURI( ).toString( ), "uri");
        encodeOptimizedForHits(getStringTable( ).getPrefixPartition( ), prefix, "prefix");

        g.moveToGroup(rule.getRightHandSide( ));
    }

    // ------------------------------------------------------------------------

    /**
     * Handles a Character (CH) event.
     *
     * @param event The Character (CH) event to be handled.
     * @throws ExiException If something goes wrong during grammar handling.
     */
    public void handle(ExiCharacters event) throws ExiException {

        String characters = event.getData( );

        /*
         * Not so sure whether this is actually such a good thing to do. But we
         * need to get rid of character data which just consists of a line
         * break character and a few indentation whitespace characters.
         */
        if (characters.matches("\n[\\s]*")) {
            return;
        }

        log.debug(String.format("CH: \"%s\"", characters));

        ExiGrammar g = getCurrentGrammar( );
        ExiGrammarRule rule = g.getMatchingRule(event);

        encodeEventCode(rule.getEventCode( ));

        encodeValue(getCurrentQName( ), characters);

        if ((g instanceof ExiExtensibleGrammar) && rule.getEventCode( ).getLength( ) > 1) {
            extendGrammar(g, rule, event);
        }

        g.moveToGroup(rule.getRightHandSide( ));
    }

    // ------------------------------------------------------------------------

    /**
     * Handles an EXI End Element (EE) event.
     *
     * @param event The EXI End Element (EE) event to be handled.
     * @throws ExiException If something goes wrong during grammar handling.
     */
    public void handle(ExiEndElement event) throws ExiException {
        log.debug("EE");

        ExiGrammar g = getCurrentGrammar( );
        ExiGrammarRule rule = g.getMatchingRule(event);

        encodeEventCode(rule.getEventCode( ));

        popQName( );
        popGrammar( );
    }

    // ------------------------------------------------------------------------

    /**
     * Handles an EXI End Document (ED) event.
     *
     * @param event The EXI End Document (ED) event to be handled.
     * @throws ExiException If something goes wrong during grammar handling.
     */
    public void handle(ExiEndDocument event) throws ExiException {
        log.debug("ED");

        ExiGrammar g = getCurrentGrammar( );
        ExiGrammarRule rule = g.getMatchingRule(event);

        encodeEventCode(rule.getEventCode( ));

        popGrammar( );  // document grammar
    }

    // ------------------------------------------------------------------------

    /**
     * Handles a EXI Comment (CM) event.
     *
     * @param event The EXI Comment (CM) event.
     * @throws ExiException If something goes wrong during grammar handling.
     */
    public void handle(ExiComment event) throws ExiException {
        if (!getOptions( ).isSet(FidelityOption.PRESERVE_COMMENTS)) {
            return;
        }
        String comment = event.getText( );
        log.debug(String.format("CM: \"%s\"", comment));

        ExiGrammar g = getCurrentGrammar( );
        ExiGrammarRule rule = g.getMatchingRule(event);

        encodeEventCode(rule.getEventCode( ));

        this.writer.writeComment(comment);

        g.moveToGroup(rule.getRightHandSide( ));
    }

    // ------------------------------------------------------------------------

    /**
     * Handles a DocType (DT) event.
     */
    public void handleDocType( ) {
        //
    }

    // ------------------------------------------------------------------------

    /**
     * Handles an Entity Reference (ER) event.
     */
    public void handleEntityReference( ) {
        //
    }

    /**
     * Encodes a single event code to the EXI stream.
     * @param eventCode The event code.
     */
    private void encodeEventCode(ExiEventCode eventCode) {
        ExiGrammarGroup activeGroup = getCurrentGrammar( ).getActiveGroup( );
        Size groupSize = activeGroup.getGroupSize( );
        this.writer.writeEventCode(eventCode, groupSize);
    }

    // ------------------------------------------------------------------------

    /**
     * <p>
     * Handles a string in a partition which is of the type Optimized For
     * Compact identifier (or Optimized For Hits).
     * </p>
     * <p>
     * This includes URI and prefix partitions. The actions taken are the same
     * for both partition types.
     * <p>
     *
     * @param part The string table partition
     * @param s The string.
     * @param msg The partition identifier. This string is merely used for log
     *        messages.
     */
    private void encodeOptimizedForHits(StringTablePartition part, String s, String msg) {
        int size = part.getSize( );
        if (part.lookup(s)) {
            int id = part.getID(s);
            this.writer.writeCompactStringHit(id, size);
            log.debug(String.format("%s hit -- writing %d(%d)", msg, BigInteger.valueOf(id + 1), (int)Math.ceil(Math.log(size+1)/Math.log(2))));
        } else {
            this.writer.writeCompactStringMiss(s, size);
            log.debug(String.format("\"%s\" (%s miss)", s, msg));
            part.add(s);
        }
    }

    // ------------------------------------------------------------------------

    /**
     * Handles a local name string.
     *
     * @param qname The QName.
     */
    private void encodeLocalName(QName qname) {
        String s = qname.getLocalPart( );
        StringTablePartition part = getStringTable( ).getLocalNamesPartition(qname.getNamespaceURI( ));
        if (part.lookup(s)) {
            log.debug("local-name hit");
            int id = part.getID(s);
            int size = part.getSize( );
            this.writer.writeLocalNameHit(id, size);
        } else {
            log.debug(String.format("\"%s\" (local-name miss)", s));
            this.writer.writeLocalNameMiss(s);
            part.add(s);
        }
    }

    // ------------------------------------------------------------------------

    /**
     * <p>
     * Handles a value string.
     * </p>
     * <p>
     * The string is looked up in both the local and the global value partition
     * if necessary. If these look-ups have not been successful, then actual
     * string is written to the stream.
     * </p>
     *
     * @param qname The QName associated to this value. This can either be the
     *        attribute's QName or, in case of character data, the enclosing
     *        element's QName.
     * @param s The string.
     */
    private void encodeValue(QName qname, String s) {
        ExiStringTable table = getStringTable( );
        ValuePartition local = table.getValuePartition(qname);
        if (local.lookup(s)) {
            log.debug("value hit");
            this.writer.writeValueHitLocal(qname, local.getID(s), local.getSize( ));
        } else {
            ValuePartition global = table.getValuePartition( );
            if (global.lookup(s)) {
                log.debug("global-value hit");
                this.writer.writeValueHitGlobal(qname, global.getID(s), global.getSize( ));
            } else {
                log.debug(String.format("\"%s\" (value miss)", s));
                this.writer.writeValueMiss(qname, s);
                local.add(s);
                global.add(s);
            }
        }
    }
}
