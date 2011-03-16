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

/**
 * Reader for EXI headers.
 * 
 * @author Marco Wegner
 */
public class ExiHeaderReader extends ExiSimpleReader {

    /**
     * Creates a new reader.
     * 
     * @param array The byte array comtaining the stream data.
     */
    public ExiHeaderReader(byte[] array) {
        super(array);
    }

    // ------------------------------------------------------------------------

    /**
     * Reads the distinguishing bits from the stream.
     *
     * @see <a href="http://www.w3.org/TR/exi/#DistinguishingBits">Exi
     *      documentation</a>
     */
    public void readDistinguishingBits( ) {
        int db = getInputStream( ).readBits(2);
        if (db != 2) {
            throw new RuntimeException( );
        }
    }

    // ------------------------------------------------------------------------

    /**
     * Reads the option bit from the EXI stream. This bits specifies whether
     * encoded options are going to follow or not.
     *
     * @return <code>true</code> if the stream contains encoded EXI options,
     *         else <code>false</code>.
     * @see <a href="http://www.w3.org/TR/exi/#header">Exi documentation</a>
     */
    public boolean readOptionBit( ) {
        return (getInputStream( ).readBits(1) == 1);
    }

    // ------------------------------------------------------------------------

    /**
     * Reads the EXI format version from the stream.
     *
     * @see <a href="http://www.w3.org/TR/exi/#version">Exi documentation</a>
     */
    public void readFormatVersion( ) {
        // TODO: more exactly, please!!!
        int db = getInputStream( ).readBits(5);
        if (db != 16) {
            throw new RuntimeException( );
        }
    }

}
