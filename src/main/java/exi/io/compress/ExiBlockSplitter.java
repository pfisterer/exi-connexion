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
package exi.io.compress;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.net.URI;
import java.util.LinkedList;
import java.util.zip.InflaterInputStream;

import javax.xml.namespace.QName;

import exi.ExiConstants;
import exi.ExiException;
import exi.ExiOptions;
import exi.ExiOptions.FidelityOption;
import exi.events.ExiAttribute;
import exi.events.ExiCharacters;
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
import exi.io.ExiInputStream;
import exi.utils.ExiNamespaceTable;
import exi.utils.ExiStringTable;
import exi.utils.StringTablePartition;
import exi.utils.ValuePartition;

/**
 * EXI handler that takes over the block splitting when reading compressed
 * streams.
 * 
 * @author Marco Wegner
 */
public class ExiBlockSplitter extends ExiEventHandler {
    
    /**
     * @author Marco Wegner
     */
    private class ValueChannelInfo {
        /**
         * The QName.
         */
        QName qname;
        /**
         * The channel size.
         */
        int size = 1;
        /**Creates a new channel info.
         * @param qname The QName.
         */
        public ValueChannelInfo(QName qname) {
            this.qname = qname;
        }
    }
    
    /**
     * The list of EXI blocks.
     */
    private final LinkedList<ExiInputBlock> blocks = new LinkedList<ExiInputBlock>( );
    /**
     * The input stream.
     */
    private final ExiInputStream is;
    
    /**
     * The namespace map.
     */
    private final ExiNamespaceTable table = new ExiNamespaceTable( );
    
    /**
     * The current block size.
     */
    private int currentBlockSize;
    /**
     * The current block.
     */
    private ExiInputBlock currentBlock;
    /**
     * The currently used structure channel.
     */
    private ExiStructureOutputChannel currentStructure;
    /**
     * The list of channel infos. 
     */
    private LinkedList<ValueChannelInfo> currentChannelInfos;

    // ------------------------------------------------------------------------

    /**
     * Creates a new block splitter.
     * 
     * @param is The input stream.
     * @param options The EXI options
     * @throws Exception
     */
    public ExiBlockSplitter(ExiInputStream is, ExiOptions options) throws Exception {
        super(options);
        
        if (options.useCompression( )) {
            InflaterInputStream iis = new InflaterInputStream(is);
            ByteArrayOutputStream temp = new ByteArrayOutputStream( );
            int b;
            while ((b = iis.read( )) != -1) {
                temp.write(b);
            }
            ExiInputStream eis = new ExiInputStream(temp.toByteArray( ));
            eis.reset( );
            eis.setByteAligned( );
            this.is = eis;
        } else {
            this.is = is;
        }
        
        initializeNewBlock( );
        
        decode( );
    }

    // ------------------------------------------------------------------------

    /**
     * Initializes a new block.
     */
    private void initializeNewBlock( ) {
        currentBlockSize = 0;
        currentBlock = new ExiInputBlock( );
        currentStructure = new ExiStructureOutputChannel( );
        currentChannelInfos = new LinkedList<ValueChannelInfo>( );
    }

    // ------------------------------------------------------------------------

    /**
     * Decodes the stream.
     * 
     * @throws ExiException
     */
    private void decode( ) throws ExiException {
        // at least for now...
        setFactory(new ExiBuiltInGrammarFactory(getOptions( )));

        pushDocumentGrammar( );

        ExiGrammarRule rule;
        do {
            rule = decodeEventCode( );
            handleGrammarRule(rule);
            
            if (currentBlockSize == ExiConstants.BLOCK_SIZE_DEFAULT) {
                finishBlock( );
                initializeNewBlock( );
            }
            
        } while (!rule.getEventType( ).equals("ED"));
        
        finishBlock( );
    }

    // ------------------------------------------------------------------------

    /**
     * Handles a single grammar rule.
     * 
     * @param rule The rule.
     * @throws ExiException
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
     * Handles an EXI Attribute event.
     * 
     * @param rule The EXI grammar rule.
     * @throws ExiException If something goes wrong during grammar manipulation.
     */
    private void handleAttribute(ExiGrammarRule rule) throws ExiException {
        String eventType = rule.getEventType( );

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

        // the correct value is still unknown, therefore ""
        ExiAttribute event = new ExiAttribute(qname, "");
        increaseBlockSize(qname);

        ExiGrammar g = getCurrentGrammar( );
        if ((g instanceof ExiExtensibleGrammar) && general) {
            extendGrammar(g, rule, event);
        }

        g.moveToGroup(rule.getRightHandSide( ));
    }

    // ------------------------------------------------------------------------

    /**
     * Handles an EXI Start Element event.
     * 
     * @param rule The EXI grammar rule.
     * @throws ExiException If something goes wrong during grammar manipulation.
     */
    private void handleStartElement(ExiGrammarRule rule) throws ExiException {
        String eventType = rule.getEventType( );

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

        ExiGrammar g = getCurrentGrammar( );
        if ((g instanceof ExiExtensibleGrammar) && general) {
            extendGrammar(g, rule, event);
        }

        g.moveToGroup(rule.getRightHandSide( ));
        pushElementGrammar(qname);
    }

    // ------------------------------------------------------------------------

    /**
     * Handles an EXI End Document event.
     * 
     * @param rule The EXI grammar rule.
     */
    private void handleEndDocument(ExiGrammarRule rule) {
        //
    }

    // ------------------------------------------------------------------------

    /**
     * Handles an EXI End Element event.
     * 
     * @param rule The EXI grammar rule.
     */
    private void handleEndElement(ExiGrammarRule rule) {
        popQName( );
        popGrammar( );
    }

    // ------------------------------------------------------------------------

    /**
     * Handles an EXI Namespace event.
     * 
     * @param rule The EXI grammar rule.
     * @throws ExiException If something goes wrong during grammar manipulation.
     */
    private void handleNamespaceDeclaration(ExiGrammarRule rule) throws ExiException {
        if (!getOptions( ).isSet(FidelityOption.PRESERVE_PREFIXES)) {
            return;
        }

        String uri = decodeURI( );
        String prefix = decodeOptimizedForHits(getStringTable( ).getPrefixPartition( ), "prefix");

        new ExiNamespaceDeclaration(prefix, URI.create(uri));

        getCurrentGrammar( ).moveToGroup(rule.getRightHandSide( ));
    }

    // ------------------------------------------------------------------------

    /**
     * Handles an EXI Start Document event.
     * 
     * @param rule The EXI grammar rule.
     * @throws ExiException If something goes wrong during grammar manipulation.
     */
    private void handleStartDocument(ExiGrammarRule rule) throws ExiException {
        getCurrentGrammar( ).moveToGroup(rule.getRightHandSide( ));
    }

    // ------------------------------------------------------------------------

    /**
     * Handles an EXI Characters event.
     * 
     * @param rule The EXI grammar rule.
     * @throws ExiException If something goes wrong during grammar manipulation.
     */
    private void handleCharacters(ExiGrammarRule rule) throws ExiException {
        
        // the correct value is still unknown, therefore ""
        ExiCharacters event = new ExiCharacters("");
        increaseBlockSize(getCurrentQName( ));

        ExiGrammar g = getCurrentGrammar( );
        if ((g instanceof ExiExtensibleGrammar) && rule.getEventCode( ).getLength( ) > 1) {
            extendGrammar(g, rule, event);
        }

        getCurrentGrammar( ).moveToGroup(rule.getRightHandSide( ));
    }

    // ------------------------------------------------------------------------

    /**
     * Handles an EXI comment.
     * 
     * @param rule The EXI grammar rule.
     * @throws ExiException If something goes wrong during grammar manipulation.
     */
    private void handleComment(ExiGrammarRule rule) throws ExiException {
        if (!getOptions( ).isSet(FidelityOption.PRESERVE_COMMENTS)) {
            return;
        }

        currentStructure.writeString(this.is.readString( ));

        getCurrentGrammar( ).moveToGroup(rule.getRightHandSide( ));
    }

    // ------------------------------------------------------------------------

    /**
     * Decodes an event code and returns the associated grammar rule.
     * 
     * @return The grammar rule.
     * @throws ExiException If errors occur during event code decoding.
     */
    private ExiGrammarRule decodeEventCode( ) throws ExiException {
        ExiGrammarGroup activeGroup = getCurrentGrammar( ).getActiveGroup( );
        Size groupSize = activeGroup.getGroupSize( );

        ExiGrammarRule rule;
        
        int p1 = readPart(groupSize.getPartSize(0));
        rule = activeGroup.getMatchingRule(new ExiEventCode(p1));
        if (rule == null) {
            int p2 = readPart(groupSize.getPartSize(1));
            rule = activeGroup.getMatchingRule(new ExiEventCode(p1, p2));
            if (rule == null) {
                int p3 = readPart(groupSize.getPartSize(2));
                rule = activeGroup.getMatchingRule(new ExiEventCode(p1, p2, p3));
            }
        }

        return rule;
    }

    // ------------------------------------------------------------------------

    /**
     * Reads an EXI event code part.
     * 
     * @param partSize The current part size.
     * @return The EXI event code part.
     */
    private int readPart(int partSize) {
        int bits = (int)Math.ceil(Math.log(partSize)/Math.log(2));
        BigInteger temp = this.is.readNBitUnsignedInteger(bits);
        currentStructure.writeNBitUnsignedInteger(temp, bits);
        return temp.intValue( );
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
        BigInteger temp;
        
        int bits = (int)Math.ceil(Math.log(part.getSize( )+1)/Math.log(2));
        temp = this.is.readNBitUnsignedInteger(bits);
        int code = temp.intValue( );
        currentStructure.writeNBitUnsignedInteger(temp, bits);
        
        if (code == 0) {
            String s = this.is.readString( );
            currentStructure.writeString(s);
            
            part.add(s);
            return s;
        }

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
        BigInteger temp;
        
        StringTablePartition part = getStringTable( ).getLocalNamesPartition(namespaceURI);
        temp = this.is.readUnsignedInteger( );
        int code = temp.intValue( );
        
        if (code == 0) {
            currentStructure.writeUnsignedInteger(temp);
            int bits = (int)Math.ceil(Math.log(part.getSize( ))/Math.log(2));
            temp = this.is.readNBitUnsignedInteger(bits);
            currentStructure.writeNBitUnsignedInteger(temp, bits);
            int index = temp.intValue( );
            return part.getValue(index);
        }

        String s = this.is.readString(code - 1);
        currentStructure.writeString(s, 1);
        
        part.add(s);
        return s;
    }

    // ------------------------------------------------------------------------

    /**
     * Increases the overall the number of values contained in this block and
     * also the specific number of values associated to a certain qualified
     * name.
     * 
     * @param qname The qualified name associated to the last
     */
    private void increaseBlockSize(QName qname) {
        currentBlockSize++;
        
        boolean found = false;
        
        for (ValueChannelInfo vcs : currentChannelInfos) {
            if (vcs.qname.equals(qname)) {
                found = true;
                vcs.size++;
            }
        }
        
        if (!found) {
            currentChannelInfos.add(new ValueChannelInfo(qname));
        }
    }

    // ------------------------------------------------------------------------

    /**
     *
     */
    private void finishBlock( ) {
        currentBlock.setStructureChannel(new ExiStructureInputChannel(currentStructure.toByteArray( )));
        
        if (currentBlockSize <= 100) {
            for (ValueChannelInfo vci : currentChannelInfos) {
                copyValueChannel(vci);
            }
        } else {
            for (ValueChannelInfo vci : currentChannelInfos) {
                if (vci.size <= 100) {
                    copyValueChannel(vci);
                }
            }
            for (ValueChannelInfo vci : currentChannelInfos) {
                if (vci.size > 100) {
                    copyValueChannel(vci);
                }
            }
        }
        
        blocks.add(currentBlock);
    }

    // ------------------------------------------------------------------------

    /**
     * Copies a value channel from the in put stream to the curren block.
     * 
     * @param vci Information containing the qualified name associated to this
     *        value channel and the number of values contained.
     */
    private void copyValueChannel(ValueChannelInfo vci) {
        
        ExiValueOutputChannel evo = new ExiValueOutputChannel(vci.qname);
        
        for (int i = 0; i < vci.size; ++i) {
            
            ExiStringTable table = getStringTable( );
            ValuePartition local = table.getValuePartition(vci.qname);
            ValuePartition global = table.getValuePartition( );
            
            BigInteger temp = this.is.readUnsignedInteger( );
            
            int code = temp.intValue( );
            if (code == 0) {
                evo.writeUnsignedInteger(temp);
                // value is found in the local table
                int bits = (int)Math.ceil(Math.log(local.getSize( ))/Math.log(2));
                temp = this.is.readNBitUnsignedInteger(bits);
                evo.writeNBitUnsignedInteger(temp, bits);
            } else if (code == 1) {
                evo.writeUnsignedInteger(temp);
                // value is found in the global table
                int bits = (int)Math.ceil(Math.log(global.getSize( ))/Math.log(2));
                temp = this.is.readNBitUnsignedInteger(bits);
                evo.writeNBitUnsignedInteger(temp, bits);
            } else {
                // value is neither found in the local nor the global table
                String s = this.is.readString(code - 2);
                evo.writeString(s, 2);
                local.add(s);
                global.add(s);
            }
        }
        
        ExiValueInputChannel vc = new ExiValueInputChannel(vci.qname, evo.toByteArray( ));
        vc.setSize(vci.size);
        currentBlock.addValueChannel(vc);
    }

    // ------------------------------------------------------------------------

    /**
     * Returns the list of blocks extracted from this stream.
     * 
     * @return The list of blocks extracted from this stream.
     */
    public LinkedList<ExiInputBlock> getBlocks( ) {
        return this.blocks;
    }
}
