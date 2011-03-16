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
package exi.io;

import javax.xml.namespace.QName;

/**
 * Abstract base class for all EXI readers.
 * 
 * @author Marco Wegner
 */
public abstract class ExiReader {

    /**
     * This reader's input stream.
     */
    private final ExiInputStream is;

    // ------------------------------------------------------------------------

    /**
     * @param array
     */
    public ExiReader(byte[] array) {
        this.is = new ExiInputStream(array);
    }

    // ------------------------------------------------------------------------

    /**
     * @param other
     */
    public ExiReader(ExiInputStream other) {
        this.is = other;
    }

    // ------------------------------------------------------------------------

    /**
     * Returns this reader's input stream.
     *
     * @return The input stream.
     */
    public ExiInputStream getInputStream( ) {
        return this.is;
    }

    // ------------------------------------------------------------------------

    /**
     * Reads a event code part from the stream.
     * 
     * @param partSize The current size of this partition.
     * @return The event code part.
     */
    public abstract int readEventCodePart(int partSize);

    // ------------------------------------------------------------------------

    /**
     * Reads a compact string code from the stream.
     * 
     * @param size The current number of strings in the table.
     * @return The compact string code.
     */
    public abstract int readCompactStringCode(int size);

    // ------------------------------------------------------------------------

    /**
     * Reads a compact string from the stream.
     * 
     * @return The string.
     */
    public abstract String readCompactString( );

    // ------------------------------------------------------------------------

    /**
     * Reads a local name code from the stream.
     * 
     * @return The local name code.
     */
    public abstract int readLocalNameCode( );

    // ------------------------------------------------------------------------

    /**
     * Reads a local name index from the stream.
     * 
     * @param size The number of strings currently in the string table.
     * @return The local name index.
     */
    public abstract int readLocalNameIndex(int size);

    // ------------------------------------------------------------------------

    /**
     * Reads a local name from the stream.
     * 
     * @param code The code associated to this local name.
     * @return The local name.
     */
    public abstract String readLocalName(int code);

    // ------------------------------------------------------------------------

    /**
     * Reads a value string code from the stream.
     * 
     * @param qname The qualified name associated to this value code.
     * @return The value string code.
     */
    public abstract int readValueStringCode(QName qname);

    // ------------------------------------------------------------------------

    /**
     * Reads a value string index from the stream.
     * 
     * @param qname The qualified name associated to this value index.
     * @param size The number of indexes in the string table.
     * @return The value string index.
     */
    public abstract int readValueStringIndex(QName qname, int size);

    // ------------------------------------------------------------------------

    /**
     * Reads a value string from the stream.
     * 
     * @param qname The qualified name associated to this value.
     * @param code The value code.
     * @return The value string.
     */
    public abstract String readValueString(QName qname, int code);

    // ------------------------------------------------------------------------

    /**
     * Reads a comment from the stream.
     * 
     * @return The comment.
     */
    public abstract String readComment( );
}
