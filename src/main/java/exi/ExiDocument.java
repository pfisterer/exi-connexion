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
package exi;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.Writer;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

import exi.ExiOptions.Alignment;
import exi.io.ExiCompressedReader;
import exi.io.ExiCompressedWriter;
import exi.io.ExiHeaderReader;
import exi.io.ExiHeaderWriter;
import exi.io.ExiInputStream;
import exi.io.ExiOutputStream;
import exi.io.ExiReader;
import exi.io.ExiSimpleReader;
import exi.io.ExiSimpleWriter;
import exi.io.ExiWriter;

/**
 * This class represents an EXI document and provided methods for encoding and
 * decoding.
 *
 * @author Marco Wegner
 */
public final class ExiDocument {

    /**
     * Instantiation not permitted.
     */
    private ExiDocument( ) {
        // nothing to do here
    }

    /**
     * Encodes an XML document using standard EXI options. Since no option is
     * modified the options are not encoded themselves. If it is necessary to
     * use standard options and also encode them, then use
     * {@link #encode(String, ExiOptions)} instead with the standard options
     * (i.e. <code>new ExiOptions( )</code>) as argument.
     *
     * @param xmlFile The XML file to encode.
     * @return The byte array of encoded data.
     * @throws Exception If something goes wrong during encoding.
     */
    public static byte[] encode(String xmlFile) throws Exception {
        return encode(xmlFile, new ExiOptions( ), false);
    }

    // ------------------------------------------------------------------------

    /**
     * Encodes an XML document using the specified EXI options. These options
     * are encoded to the stream.
     *
     * @param xmlFile The XML file to encode.
     * @param options The EXI options to use.
     * @return The byte array of encoded data.
     * @throws Exception If something goes wrong during encoding.
     */
    public static byte[] encode(String xmlFile, ExiOptions options) throws Exception {
        return encode(xmlFile, options, true);
    }

    // ------------------------------------------------------------------------

    /**
     * Internal method for encoding. It is explicitly specified whether or not
     * to encode the options.
     *
     * @param xmlFile The XML file to encode.
     * @param options The EXI options to use.
     * @param encodeOptions <code>true</code> if the EXI options should be
     *        encoded to the stream, else <code>false</code>.
     * @return The byte array of encoded data.
     * @throws Exception If something goes wrong during encoding.
     */
    private static byte[] encode(String xmlFile, ExiOptions options, boolean encodeOptions) throws Exception {

        File file = new File(xmlFile);
        if (!file.canRead( )) {
            System.err.println("ERROR: No such file: " + xmlFile);
            System.exit(-1);
        }

        // header is always encoded without compression and using bit-packed alignment
        ExiWriter ew = new ExiHeaderWriter( );
        encodeHeader((ExiHeaderWriter)ew, options, encodeOptions);
        
        if (options.useCompression( ) || options.getAlign( ) != Alignment.BIT_PACKED) {
            ExiOutputStream outputStream = ew.getOutputStream( );
            outputStream.setByteAligned( );
            if (options.getAlign( ) == Alignment.BYTE_ALIGNED) {
                ew = new ExiSimpleWriter(outputStream);
            } else {
                ew = new ExiCompressedWriter(outputStream, options);
            }
        }

        ExiDocumentHandler edh = new ExiDocumentHandler(new ExiEncoder(ew, options));
        SAXParser parser = SAXParserFactory.newInstance( ).newSAXParser( );
        parser.getXMLReader( ).setProperty("http://xml.org/sax/properties/lexical-handler", edh);
        parser.parse(file, edh);

        return ew.toByteArray( );
    }

    // ------------------------------------------------------------------------

    /**
     * Encodes the EXI header to the stream.
     *
     * @param w The EXI stream writer.
     * @param options The EXI options to use.
     * @param encodeOptions <code>true</code> if the EXI options should be
     *        encoded to the stream, else <code>false</code>.
     * @throws Exception If something goes wrong during header encoding.
     */
    private static void encodeHeader(ExiHeaderWriter w, ExiOptions options, boolean encodeOptions) throws Exception {
        w.writeDistinguishingBits( );
        w.writeOptionsBit(encodeOptions);
        w.writeFormatVersion( );
        if (encodeOptions) {
            encodeOptions(w, options);
        }
    }

    // ------------------------------------------------------------------------

    /**
     * Encodes EXI options to the stream.
     *
     * @param w The EXI stream writer.
     * @param options The EXI options to use.
     * @throws Exception If something goes wrong during header encoding.
     */
    private static void encodeOptions(ExiHeaderWriter w, ExiOptions options) throws Exception {
        ExiEncoder encoder = new ExiEncoder(w, new ExiOptions( ));
        ExiDocumentHandler edh = new ExiDocumentHandler(encoder);
        SAXParser parser = SAXParserFactory.newInstance( ).newSAXParser( );
        parser.getXMLReader( ).setProperty("http://xml.org/sax/properties/lexical-handler", edh);
        parser.parse(new InputSource(new StringReader(options.toXML( ))), edh);
    }

    // ------------------------------------------------------------------------

    /**
     * Decodes an EXI document stream stored in an byte array.
     *
     * @param array The byte array.
     * @throws Exception If something goes wrong during decoding.
     */
    public static void decode(byte[] array) throws Exception {

        // header is always encoded without compression and using bit-packed alignment
        ExiReader er = new ExiHeaderReader(array);
        ExiOptions options = decodeHeader((ExiHeaderReader)er);

        if (options.useCompression( ) || options.getAlign( ) != Alignment.BIT_PACKED) {
            ExiInputStream inputStream = er.getInputStream( );
            inputStream.setByteAligned( );
            if (options.getAlign( ) == Alignment.BYTE_ALIGNED) {
                er = new ExiSimpleReader(inputStream);
            } else {
                er = new ExiCompressedReader(inputStream, options);
            }
        }

        ExiDocumentBuilder builder = new ExiDocumentBuilder( );
        ExiDecoder decoder = new ExiDecoder(builder, er, options);
        decoder.decode( );
        Document document = builder.getDocument( );

        BufferedWriter bw = new BufferedWriter(new FileWriter("/home/marco/temp/exi/decoded.xml"));
        printDOMDocument(document, bw);
        bw.flush( );
        bw.close( );
    }

    // ------------------------------------------------------------------------

    /**
     * Decodes an EXI header.
     *
     * @param r The stream reader to be used for reading from the EXI stream.
     * @return The options to be used for decoding the EXI body.
     * @throws Exception If something goes wrong during decoding the header.
     */
    private static ExiOptions decodeHeader(ExiHeaderReader r) throws Exception {
        r.readDistinguishingBits( );
        boolean hasOptions = r.readOptionBit( );
        r.readFormatVersion( );
        ExiOptions options = getOptions(r, hasOptions);
        return options;
    }

    // ------------------------------------------------------------------------

    /**
     * Returns EXI options. to be used for decoding the EXI body. If the EXI
     * header contains EXI options itself then these options are decoded and
     * returned. Else standard options are used.
     *
     * @param esr The EXI stream reader.
     * @param hasOptions Specifies whether the header contains options itself.
     * @return The options.
     * @throws Exception If something goes wrong during stream or XML
     *         operations.
     */
    private static ExiOptions getOptions(ExiHeaderReader esr, boolean hasOptions) throws Exception {
        ExiOptions options;
        if (hasOptions) {
            ExiDocumentBuilder builder = new ExiDocumentBuilder( );
            // use standard options for decoding the options themselves
            ExiDecoder decoder = new ExiDecoder(builder, esr, new ExiOptions( ));
            decoder.decode( );
            Document document = builder.getDocument( );
            printDOMDocument(document, new PrintWriter(System.out));
            options = new ExiOptions(document);
        } else {
            options = new ExiOptions( );
        }
        return options;
    }

    // ------------------------------------------------------------------------

    /**
     * Print the DOM document using the specified writer.
     *
     * @param document The DOM document to be printed.
     * @param w The writer to used.
     * @throws IOException If {@link XMLSerializer#serialize(Document)} throws
     *         an exception.
     */
    private static void printDOMDocument(Document document, Writer w) throws IOException {
        OutputFormat format = new OutputFormat(document);
        format.setIndenting(true);
        format.setIndent(2);
        XMLSerializer printer = new XMLSerializer(w, format);
        printer.setNamespaces(true);
        printer.serialize(document);
    }
}
