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
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

import exi.events.ExiAttribute;
import exi.events.ExiCharacters;
import exi.events.ExiComment;
import exi.events.ExiEndDocument;
import exi.events.ExiEndElement;
import exi.events.ExiNamespaceDeclaration;
import exi.events.ExiProcessingInstruction;
import exi.events.ExiStartDocument;
import exi.events.ExiStartElement;
import exi.utils.ExiNamespaceTable;

/**
 * This class behaves as an adapter between SAX and EXI. The SAX events
 * generated here are translated into corresponding EXI events which are then
 * handled in an EXI event handler.
 *
 * @author Marco Wegner
 */
public class ExiDocumentHandler extends DefaultHandler implements LexicalHandler {

    // ------------------------------------------------------------------------
    // Static stuff
    // ------------------------------------------------------------------------

    /**
     * The EXI logger user for logging stuff.
     */
    private static Logger log = ExiLogger.getLogger(ExiDocumentHandler.class);

    // ------------------------------------------------------------------------
    // Member variables
    // ------------------------------------------------------------------------

	/**
     * The EXI event handler used for executing the EXI part of the SAX events.
     */
	private final ExiEncoder encoder;

    // ------------------------------------------------------------------------

	/**
	 *
	 */
	private final ExiNamespaceTable table;

    // ------------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------------

	/**
     * Creates a new EXI document handler.
     *
     * @param encoder The EXI encoder for interpreting the SAX events generated
     *        in this class.
     * @throws Exception If something goes wrong during namespace map
     *         initialization.
     */
    public ExiDocumentHandler(ExiEncoder encoder) throws Exception {
        this.encoder = encoder;
        this.table = new ExiNamespaceTable( );
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#setDocumentLocator(org.xml.sax.Locator)
     */
    @Override
    public void setDocumentLocator(Locator locator) {
        //
    }

    // ------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#processingInstruction(java.lang.String, java.lang.String)
     */
    @Override
    public void processingInstruction(String target, String data) throws SAXException {
        ExiProcessingInstruction event = new ExiProcessingInstruction(target, data);
        try {
            this.encoder.handle(event);
        } catch (ExiException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    // ------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
     */
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        ExiCharacters event = new ExiCharacters(extractString(ch, start, length));
        try {
            this.encoder.handle(event);
        } catch (ExiException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    // ------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see org.xml.sax.ext.LexicalHandler#comment(char[], int, int)
     */
    public void comment(char[] ch, int start, int length) throws SAXException {
        try {
            this.encoder.handle(new ExiComment(extractString(ch, start, length)));
        } catch (ExiException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    // ------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#ignorableWhitespace(char[], int, int)
     */
    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        log.debug(String.format("whitespace: \"%s\"", extractString(ch, start, length)));
    }

    // ------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see org.xml.sax.ext.LexicalHandler#startCDATA()
     */
    public void startCDATA( ) throws SAXException {
        //
    }

    // ------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see org.xml.sax.ext.LexicalHandler#endCDATA()
     */
    public void endCDATA( ) throws SAXException {
        //
    }

    // ------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#startDocument()
     */
    @Override
    public void startDocument( ) throws SAXException {
        try {
            this.encoder.handle(new ExiStartDocument( ));
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    // ------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#endDocument()
     */
    @Override
    public void endDocument( ) throws SAXException {
        try {
            this.encoder.handle(new ExiEndDocument( ));
        } catch (ExiException e) {
            e.printStackTrace( );
            System.exit(-1);
        }
    }

    // ------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see org.xml.sax.ext.LexicalHandler#startDTD(java.lang.String, java.lang.String, java.lang.String)
     */
    public void startDTD(String name, String publicId, String systemId) throws SAXException {
        //
    }

    // ------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see org.xml.sax.ext.LexicalHandler#endDTD()
     */
    public void endDTD( ) throws SAXException {
        //
    }

    // ------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    @Override
    public void startElement(String uri, String localName, String name, Attributes atts) throws SAXException {

        /*
         * It's necessary that the namespace URI is known before the element's
         * start tag is handled.
         */
        for (int i = 0; i < atts.getLength( ); ++i) {

            String attQName = atts.getQName(i);
            String attValue = atts.getValue(i);

            if (attQName.toLowerCase( ).equals("xmlns")) {
                this.table.put("", URI.create(attValue));
            } else if (attQName.toLowerCase( ).startsWith("xmlns")) {
                this.table.put(attQName.substring(6), URI.create(attValue));
            }
        }

        try {
            this.encoder.handle(new ExiStartElement(generateQName(name)));

            for (int i = 0; i < atts.getLength( ); ++i) {

                String attQName = atts.getQName(i);
                String attValue = atts.getValue(i);

                if (attQName.toLowerCase( ).equals("xmlns")) {
                    this.encoder.handle(new ExiNamespaceDeclaration("", URI.create(attValue)));
                } else if (attQName.toLowerCase( ).startsWith("xmlns:")) {
                    this.encoder.handle(new ExiNamespaceDeclaration(attQName.substring(6), URI.create(attValue)));
                } else {
                    this.encoder.handle(new ExiAttribute(generateQName(attQName), attValue));
                }
            }
        } catch (ExiException e) {
            e.printStackTrace( );
            System.exit(-1);
        }
    }

    // ------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void endElement(String uri, String localName, String name) throws SAXException {
        try {
            this.encoder.handle(new ExiEndElement( ));
        } catch (ExiException e) {
            e.printStackTrace( );
            System.exit(-1);
        }
    }

    // ------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see org.xml.sax.ext.LexicalHandler#startEntity(java.lang.String)
     */
    public void startEntity(String name) throws SAXException {
        //
    }

    // ------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see org.xml.sax.ext.LexicalHandler#endEntity(java.lang.String)
     */
    public void endEntity(String name) throws SAXException {
        //
    }

    // ------------------------------------------------------------------------
    // Private helpers
    // ------------------------------------------------------------------------

    /**
     * Extracts a string from a character array with defined start position and
     * length.
     *
     * @param ch The character array.
     * @param start The string's start position in the array.
     * @param length The string's length.
     * @return The extracted string.
     */
    private String extractString(char[] ch, int start, int length) {
        return String.valueOf(ch).substring(start, start+length);
    }

    // ------------------------------------------------------------------------

    /**
     * <p>
     * Generates a QName from the specified string.
     * </p>
     * <p>
     * Prefix and local part ar extracted from the string itself, the namespace
     * URI is retrieved from the {@link ExiNamespaceTable}.
     * </p>
     *
     * @param name The string.
     * @return The generated QName.
     */
    private QName generateQName(String name) {
        String prefix = "";

        int index = name.indexOf(":");
        if (index > -1) {
            prefix = name.substring(0, index);
        }

        String namespaceURI = this.table.getNamespaceURI(prefix).toString( );
        String localPart = name.substring(index+1);
        QName qname = new QName(namespaceURI, localPart, prefix);
        return qname;
    }
}
