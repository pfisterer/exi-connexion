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

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

/**
 * This class represents the options (including the fidelity options) used in
 * EXI.
 *
 * @author Marco Wegner
 */
public class ExiOptions implements ExiConstants {

    // ------------------------------------------------------------------------
    // Nested types
    // ------------------------------------------------------------------------

    /**
     * Enumeration for alignment value (EXI option alignment).
     *
     * @author Marco Wegner
     */
    public enum Alignment {
        /**
         * Bit-packed alignment.
         */
        BIT_PACKED,
        /**
         * Alignment along byte borders.
         */
        BYTE_ALIGNED,
        /**
         * Alignment as used for compression, including reordering and
         * multiplexing. Compression itself is not applied.
         */
        PRE_COMPRESSED
    }

    // ------------------------------------------------------------------------

    /**
     * EXI fidelity options.
     *
     * @author Marco Wegner
     */
    public enum FidelityOption {
        /**
         * CM events are preserved.
         */
        PRESERVE_COMMENTS,
        /**
         * PI events are preserved.
         */
        PRESERVE_PROCESSING_INSTRUCTIONS,
        /**
         * DOCTYPE and ER events are preserved.
         */
        PRESERVE_DTDS,
        /**
         * NS events and namespace prefixes are preserved.
         */
        PRESERVE_PREFIXES,
        /**
         * Lexical form of element and attribute values is preserved.
         */
        PRESERVE_LEXICAL_VALUES
    }

    // ------------------------------------------------------------------------
    // Static stuff
    // ------------------------------------------------------------------------

    /**
     * The EXI logger user for logging stuff.
     */
    private static Logger log = ExiLogger.getLogger(ExiOptions.class);

    // ------------------------------------------------------------------------
    // Member variables
    // ------------------------------------------------------------------------

    /**
     * EXI option alignment.
     */
    private Alignment align = ALIGN_DEFAULT;

    // ------------------------------------------------------------------------

    /**
     * EXI option compression.
     */
    private boolean useCompression = USE_COMPRESSION_DEFAULT;

    // ------------------------------------------------------------------------

    /**
     * EXI option fragment.
     */
    private boolean useFragments = USE_FRAGMENTS_DEFAULT;

    // ------------------------------------------------------------------------

    /**
     * EXI option schemaID.
     */
    private String schemaId = SCHEMA_ID_DEFAULT;

    // ------------------------------------------------------------------------

    /**
     * EXI option codecMap.
     */
    private Object codecMap = CODEC_MAP_DEFAULT;

    // ------------------------------------------------------------------------

    /**
     * EXI option blockSize.
     */
    private int blockSize = BLOCK_SIZE_DEFAULT;

    // ------------------------------------------------------------------------

    /**
     * CM events are preserved.
     */
    private boolean preserveComments = PRESERVE_COMMENTS_DEFAULT;

    // ------------------------------------------------------------------------

    /**
     * PI events are preserved.
     */
    private boolean preservePIs = PRESERVE_PI_DEFAULT;

    // ------------------------------------------------------------------------

    /**
     * DOCTYPE and ER events are preserved.
     */
    private boolean preserveDTDs = PRESERVE_DTD_DEFAULT;

    // ------------------------------------------------------------------------

    /**
     * NS events and namespace prefixes are preserved.
     */
    private boolean preservePrefixes = PRESERVE_PREFIX_DEFAULT;

    // ------------------------------------------------------------------------

    /**
     * Lexical form of element and attribute values is preserved.
     */
    private boolean preserveLexicalValues = PRESERVE_LEXICAL_DEFAULT;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /**
     * Creates a set of default options.
     *
     * <p>
     * The options are preliminarily set to their default values. They can be
     * modified using the appropriate setter methods.
     * </p>
     */
    public ExiOptions( ) {
        //
    }

    // ------------------------------------------------------------------------

    /**
     * Constructs an EXI options instance from a DOM document. This document
     * results from reading an EXI stream's header.
     *
     * @param document The DOM document.
     * @throws ExiException If something goes wrong during option construction.
     */
    public ExiOptions(Document document) throws ExiException {
        NodeList childNodes = document.getDocumentElement( ).getChildNodes( );
        for (int i = 0; i < childNodes.getLength( ); ++i) {
            Node n = childNodes.item(i);
            if (n.getNodeType( ) != Node.ELEMENT_NODE) {
                continue;
            }

            Element e = (Element)n;
            String name = e.getTagName( );
            if (name.equals("lesscommon")) {
                parseLessCommon(e);
            } else if (name.equals("common")) {
                parseCommon(e);
            } else if (name.equals("strict")) {
                parseStrict(e);
            }
        }
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    /**
     * Sets a single EXI option to the specified value.
     *
     * @param type The option to be set.
     * @param value The new value.
     */
    public void set(FidelityOption type, boolean value) {
        switch (type) {
            case PRESERVE_COMMENTS:
                this.preserveComments = value;
                showFidelityLogMessage("PRESERVE COMMENTS", value);
                break;

            case PRESERVE_DTDS:
                this.preserveDTDs = value;
                showFidelityLogMessage("PRESERVE DTDs", value);
                break;

            case PRESERVE_LEXICAL_VALUES:
                this.preserveLexicalValues = value;
                showFidelityLogMessage("PRESERVE LEXICAL VALUES", value);
                break;

            case PRESERVE_PREFIXES:
                this.preservePrefixes = value;
                showFidelityLogMessage("PRESERVE PREFIXES", value);
                break;

            case PRESERVE_PROCESSING_INSTRUCTIONS:
                this.preservePIs = value;
                showFidelityLogMessage("PRESERVE PIs", value);
                break;
        }
    }

    // ------------------------------------------------------------------------

    /**
     * Queries the value of a single EXI option from this EXI options instance
     * and returns it.
     *
     * @param type The option to query.
     * @return The value of the EXI option.
     */
    public boolean isSet(FidelityOption type) {
        boolean set = false;
        switch (type) {
            case PRESERVE_COMMENTS:
                set = this.preserveComments;
                break;

            case PRESERVE_DTDS:
                set = this.preserveDTDs;
                break;

            case PRESERVE_LEXICAL_VALUES:
                set = this.preserveLexicalValues;
                break;

            case PRESERVE_PREFIXES:
                set = this.preservePrefixes;
                break;

            case PRESERVE_PROCESSING_INSTRUCTIONS:
                set = this.preservePIs;
                break;
        }

        return set;
    }

    // ------------------------------------------------------------------------

    /**
     * Sets the value for the EXI option alignment.
     *
     * @param align The new value for the EXI option alignment.
     */
    public void setAlign(Alignment align) {
        this.align = align;
    }

    // ------------------------------------------------------------------------

    /**
     * Returns the the value for the EXI option alignment.
     *
     * @return The value for the EXI option alignment.
     */
    public Alignment getAlign( ) {
        return this.align;
    }

    // ------------------------------------------------------------------------

    /**
     * Sets the value for the EXI option compression.
     *
     * @param useCompression The new value for the EXI option compression.
     */
    public void setCompression(boolean useCompression) {
        this.useCompression = useCompression;
    }

    // ------------------------------------------------------------------------

    /**
     * Returns the value for the EXI option compression.
     *
     * @return The value for the EXI option compression.
     */
    public boolean useCompression( ) {
        return this.useCompression;
    }

    // ------------------------------------------------------------------------

    /**
     * Sets the value for the EXI option fragment.
     *
     * @param useFragments The new value for the EXI option fragment.
     */
    public void setFragments(boolean useFragments) {
        this.useFragments = useFragments;
    }

    // ------------------------------------------------------------------------

    /**
     * Returns the value for the EXI option fragment.
     *
     * @return The value for the EXI option fragment.
     */
    public boolean useFragments( ) {
        return this.useFragments;
    }

    // ------------------------------------------------------------------------

    /**
     * Sets the value for the EXI option schemaID.
     *
     * @param schemaId The new value for the EXI option schemaID.
     */
    public void setSchemaId(String schemaId) {
        this.schemaId = schemaId;
    }

    // ------------------------------------------------------------------------

    /**
     * Returns the value for the EXI option schemaID.
     *
     * @return The value for the EXI option schemaID.
     */
    public String getSchemaId( ) {
        return this.schemaId;
    }

    // ------------------------------------------------------------------------

    /**
     * Sets the value for the EXI option codecMap.
     *
     * @param codecMap The new value for the EXI option codecMap.
     */
    public void setCodecMap(Object codecMap) {
        this.codecMap = codecMap;
    }

    // ------------------------------------------------------------------------

    /**
     * Returns the value for the EXI option codecMap.
     *
     * @return The value for the EXI option codecMap.
     */
    public Object getCodecMap( ) {
        return this.codecMap;
    }

    // ------------------------------------------------------------------------

    /**
     * Sets the value for the EXI option blockSize.
     *
     * @param blockSize The new value for the EXI option blockSize.
     */
    public void setBlockSize(int blockSize) {
        this.blockSize = blockSize;
    }

    // ------------------------------------------------------------------------

    /**
     * Returns the value for the EXI option blockSize.
     *
     * @return The value for the EXI option blockSize.
     */
    public int getBlockSize( ) {
        return this.blockSize;
    }

    // ------------------------------------------------------------------------

    /**
     * Creates an XML string from this options object to be used for EXI
     * encoding. The resulting XML conforms to the XML Schema as specified in
     * the
     * {@link <a href="http://www.w3.org/TR/exi/#optionsSchema">EXI documentation</a>}.
     *
     * @return The EXI options XML string.
     * @throws Exception If something goes wrong during document creation.
     */
    public String toXML( ) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance( );
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder( );
        Document document = builder.newDocument( );

        Element header = document.createElement("header");
        header.setAttribute("xmlns", "http://www.w3.org/2007/07/exi");
        document.appendChild(header);

        Element lesscommon = buildLessCommon(document);
        if (lesscommon != null) {
            header.appendChild(lesscommon);
        }
        Element common = buildCommon(document);
        if (common != null) {
            header.appendChild(common);
        }
        Element strict = buildStrict(document);
        if (strict != null) {
            header.appendChild(strict);
        }

        OutputFormat format = new OutputFormat(document);
        StringWriter sw = new StringWriter( );
        XMLSerializer printer = new XMLSerializer(sw, format);
        printer.setNamespaces(true);
        printer.serialize(document);

        return sw.toString( );
    }

    // ------------------------------------------------------------------------

    /**
     * Creates the EXI option's <code>lesscommon</code> XML element.
     *
     * @param document The DOM document.
     * @return The newly created element.
     */
    private Element buildLessCommon(Document document) {
        Element lesscommon = null;
        List<Element> elems = new ArrayList<Element>( );

        Element uncommon = buildUncommon(document);
        if (uncommon != null) {
            elems.add(uncommon);
        }
        Element preserve = buildPreserve(document);
        if (preserve != null) {
            elems.add(preserve);
        }
        if (this.blockSize != BLOCK_SIZE_DEFAULT) {
            Element bsize = document.createElement("blockSize");
            bsize.appendChild(document.createTextNode(String.valueOf(this.blockSize)));
            elems.add(bsize);
        }

        if (elems.size( ) > 0) {
            lesscommon = document.createElement("lesscommon");
            for (Element e : elems) {
                lesscommon.appendChild(e);
            }
        }

        return lesscommon;
    }

    // ------------------------------------------------------------------------

    /**
     * Creates the EXI option's <code>uncommon</code> XML element.
     *
     * @param document The DOM document.
     * @return The newly created element.
     */
    private Element buildUncommon(Document document) {
        Element uncommon = null;
        if (this.align != Alignment.BIT_PACKED) {
            uncommon = document.createElement("uncommon");
            Element alignment = document.createElement("alignment");
            if (this.align == Alignment.BYTE_ALIGNED) {
                alignment.appendChild(document.createElement("byte"));
            } else if (this.align == Alignment.PRE_COMPRESSED) {
                alignment.appendChild(document.createElement("pre-compress"));
            }
            uncommon.appendChild(alignment);
        }
        return uncommon;
    }

    // ------------------------------------------------------------------------

    /**
     * Creates the EXI option's <code>preserve</code> XML element. This
     * element contains the fidelity options.
     *
     * @param document The DOM document.
     * @return The newly created element.
     */
    private Element buildPreserve(Document document) {
        Element preserve = null;
        List<Element> elems = new ArrayList<Element>( );
        if (this.preserveDTDs != PRESERVE_DTD_DEFAULT) {
            elems.add(document.createElement("dtd"));
        }
        if (this.preservePrefixes != PRESERVE_PREFIX_DEFAULT) {
            elems.add(document.createElement("prefixes"));
        }
        if (this.preserveLexicalValues != PRESERVE_LEXICAL_DEFAULT) {
            elems.add(document.createElement("lexicalValues"));
        }
        if (this.preserveComments != PRESERVE_COMMENTS_DEFAULT) {
            elems.add(document.createElement("comments"));
        }
        if (this.preservePIs != PRESERVE_PI_DEFAULT) {
            elems.add(document.createElement("pis"));
        }
        if (elems.size( ) > 0) {
            preserve = document.createElement("preserve");
            for (Element e : elems) {
                preserve.appendChild(e);
            }
        }
        return preserve;
    }

    // ------------------------------------------------------------------------

    /**
     * Creates the EXI option's <code>common</code> XML element.
     *
     * @param document The DOM document.
     * @return The newly created element.
     */
    private Element buildCommon(Document document) {
        Element common = null;
        List<Element> elems = new ArrayList<Element>( );
        if (this.useCompression != USE_COMPRESSION_DEFAULT) {
            elems.add(document.createElement("compression"));
        }
        if (elems.size( ) > 0) {
            common = document.createElement("common");
            for (Element e : elems) {
                common.appendChild(e);
            }
        }
        return common;
    }

    // ------------------------------------------------------------------------

    /**
     * Creates the EXI option's <code>strict</code> XML element. Does
     * currently nothing.
     *
     * @param document The DOM document.
     * @return The newly created element.
     */
    private Element buildStrict(Document document) {
        return null;
    }

    // ------------------------------------------------------------------------

    /**
     * Parses the XML DOM document's <code>lesscommon</code> element and
     * extracts the EXI options contained there.
     *
     * @param element The DOM element.
     * @throws ExiException If something goes wrong during parsing or option
     *         construction.
     */
    private void parseLessCommon(Element element) throws ExiException {
        NodeList childNodes = element.getChildNodes( );
        for (int i = 0; i < childNodes.getLength( ); ++i) {
            Node n = childNodes.item(i);
            if (n.getNodeType( ) != Node.ELEMENT_NODE) {
                continue;
            }

            Element e = (Element)n;
            String name = e.getTagName( );
            if (name.equals("uncommon")) {
                parseUncommon(e);
            } else if (name.equals("preserve")) {
                parsePreserve(e);
            } else if (name.equals("blockSize")) {
                this.blockSize = Integer.parseInt(e.getTextContent( ));
            }
        }
    }

    // ------------------------------------------------------------------------

    /**
     * Parses the XML DOM document's <code>uncommon</code> element and
     * extracts the EXI options contained there.
     *
     * @param element The DOM element.
     * @throws ExiException If something goes wrong during parsing or option
     *         construction.
     */
    private void parseUncommon(Element element) throws ExiException {
        NodeList childNodes = element.getChildNodes( );
        for (int i = 0; i < childNodes.getLength( ); ++i) {
            Node n = childNodes.item(i);
            if (n.getNodeType( ) != Node.ELEMENT_NODE) {
                continue;
            }

            Element e = (Element)n;
            String name = e.getTagName( );
            if (name.equals("alignment")) {
                parseAlignment(e);
            }
        }
    }

    // ------------------------------------------------------------------------

    /**
     * Parses the XML DOM document's <code>alignment</code> element containing
     * alignment options.
     *
     * @param element The alignment DOM element.
     */
    private void parseAlignment(Element element) {
        NodeList childNodes = element.getChildNodes( );
        for (int i = 0; i < childNodes.getLength( ); ++i) {
            Node n = childNodes.item(i);
            if (n.getNodeType( ) != Node.ELEMENT_NODE) {
                continue;
            }

            Element e = (Element)n;
            String name = e.getTagName( );
            if (name.equals("byte")) {
                this.align = Alignment.BYTE_ALIGNED;
            } else if (name.equals("pre-compress")) {
                this.align = Alignment.PRE_COMPRESSED;
            }
        }
    }

    // ------------------------------------------------------------------------

    /**
     * Parses the XML DOM document's <code>preserve</code> element containing
     * the fidelity options and extracts them.
     *
     * @param element The DOM element.
     * @throws ExiException If something goes wrong during parsing or option
     *         construction.
     */
    private void parsePreserve(Element element) throws ExiException {
        NodeList childNodes = element.getChildNodes( );
        for (int i = 0; i < childNodes.getLength( ); ++i) {
            Node n = childNodes.item(i);
            if (n.getNodeType( ) != Node.ELEMENT_NODE) {
                continue;
            }

            Element e = (Element)n;
            String name = e.getTagName( );
            FidelityOption o;
            if (name.equals("dtd")) {
                o = FidelityOption.PRESERVE_DTDS;
            } else if (name.equals("prefixes")) {
                o = FidelityOption.PRESERVE_PREFIXES;
            } else if (name.equals("lexicalValues")) {
                o = FidelityOption.PRESERVE_LEXICAL_VALUES;
            } else if (name.equals("comments")) {
                o = FidelityOption.PRESERVE_COMMENTS;
            } else if (name.equals("pis")) {
                o = FidelityOption.PRESERVE_PROCESSING_INSTRUCTIONS;
            } else {
                throw new ExiFidelityOptionException("Unknown fidelity option element: " + name);
            }
            set(o, true);
        }
    }

    // ------------------------------------------------------------------------

    /**
     * Parses the XML DOM document's <code>common</code> element and extracts
     * the EXI options contained there.
     *
     * @param element The DOM element.
     * @throws ExiException If something goes wrong during parsing or option
     *         construction.
     */
    private void parseCommon(Element element) throws ExiException {
        NodeList childNodes = element.getChildNodes( );
        for (int i = 0; i < childNodes.getLength( ); ++i) {
            Node n = childNodes.item(i);
            if (n.getNodeType( ) != Node.ELEMENT_NODE) {
                continue;
            }

            Element e = (Element)n;
            String name = e.getTagName( );
            if (name.equals("compression")) {
                this.useCompression = true;
            }
        }
    }

    // ------------------------------------------------------------------------

    /**
     * Parses the XML DOM document's <code>strict</code> element and extracts
     * the EXI options contained there. Does nothing for now.
     *
     * @param element The DOM element.
     * @throws ExiException If something goes wrong during parsing or option
     *         construction.
     */
    private void parseStrict(Element element) throws ExiException {
        // nothing to do
    }

    // ------------------------------------------------------------------------

    /**
     * Print a log message after a fidelity option manipulation.
     *
     * @param type A string which describes the changed option.
     * @param value The new value.
     */
    private void showFidelityLogMessage(String type, boolean value) {
        log.debug(String.format("Fidelity option %s set to %s", type, value));
    }
}
