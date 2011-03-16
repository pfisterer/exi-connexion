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

import java.io.ByteArrayInputStream;
import java.math.BigInteger;

/**
 * EXI input stream.
 *
 * @author Marco Wegner
 */
public class ExiInputStream extends ByteArrayInputStream {

    // ------------------------------------------------------------------------
    // Member variables
    // ------------------------------------------------------------------------

    /**
     * Specifies whether this stream assumes values aligned to bytes or not.
     */
    private boolean byteAligned = false;

    // ------------------------------------------------------------------------

    /**
     * The read buffer.
     */
    private int buffer = 0;

    // ------------------------------------------------------------------------

    /**
     * This variable represents the number of bits already read from the current
     * buffer. It also represents the position of the next bit to be read in the
     * buffer.
     */
    private int bitPos = 0;

    // ------------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------------

    /**
     * Constructs a new EXI stream reader.
     *
     * @param array The byte array containing the encoded data.
     */
    public ExiInputStream(byte[] array) {
        super(array);
        this.buffer = read( );
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    /**
     * Sets this stream to reading values byte-aligned. This cannot be undone.
     * The current buffer byte is skipped.
     */
    public void setByteAligned( ) {
        skip( );
        this.byteAligned = true;
    }

    // ------------------------------------------------------------------------

    /**
     * Reads the specified number of bits from the stream. If this stream is in
     * byte-aligned mode, then a full byte is read instead.
     *
     * @param bits The number of bits.
     * @return The data contained in the bits just read.
     */
    public int readBits(int bits) {
        if (bits < 0 || bits > 8) {
            throw new IllegalArgumentException("Value for bits must be between 0 and 8!");
        }
        if (bits == 0) {
            return 0;
        }

        if (this.byteAligned) {
            return read( );
        }

        if (this.bitPos + bits > 8) {
            // the number of bits that will be read from the next buffer byte
            int readNext = this.bitPos + bits - 8;

            int result = this.buffer & ((1 << (8 - this.bitPos)) - 1);
            this.buffer = read( );
            result <<= readNext;
            this.bitPos = readNext;
            result |= (this.buffer >> (8 - this.bitPos));
            return result;
        }
        this.bitPos += bits;
        int result = (this.buffer >> (8 - this.bitPos)) & ((1 << bits) - 1);
        return result & 0xFF;
    }

    // ------------------------------------------------------------------------

    /**
     * Reads a unsigned integer from the stream.
     *
     * @return The unsigned integer's value.
     */
    public BigInteger readUnsignedInteger( ) {

        BigInteger result = BigInteger.ZERO;

        int current;
        BigInteger multi = BigInteger.ONE;
        do {
            current = readBits(8);
            result = result.add(BigInteger.valueOf(current & 0x7F).multiply(multi));
            multi = multi.shiftLeft(7);
        } while((current & 0x80) > 0);

        return result;
    }

    // ------------------------------------------------------------------------

    /**
     * Reads an n-bit unsigned integer from the stream.
     *
     * @param bits The number of bits this unsigned integer consists of.
     * @return The unsigned integer's value.
     */
    public BigInteger readNBitUnsignedInteger(int bits) {

        BigInteger result = BigInteger.ZERO;

        int b = bits;
        while (b > 8) {
            result = result.shiftLeft(8);
            result = result.or(BigInteger.valueOf(readBits(8)));
            b -= 8;
        }
        result = result.shiftLeft(b);
        result = result.or(BigInteger.valueOf(readBits(b)));

        return result;
    }

    // ------------------------------------------------------------------------

    /**
     * Reads a (length-prefixed) string from the stream.
     *
     * @return The string.
     */
    public String readString( ) {
        return readString(readUnsignedInteger( ).intValue( ));
    }

    // ------------------------------------------------------------------------

    /**
     * Reads a string of the specified length from the stream.
     *
     * @param len The string's length.
     * @return The string.
     */
    public String readString(int len) {
        StringBuffer sb = new StringBuffer( );
        for (int i = 0; i < len; ++i) {
            sb.appendCodePoint(readUnsignedInteger( ).intValue( ));
        }
        return sb.toString( );
    }

    // ------------------------------------------------------------------------

    /**
     * Ignores the remaining bits from the currently read byte. This is mostly
     * used for skipping padding bits.
     */
    public void skip( ) {
        if (this.byteAligned) {
            this.bitPos = 0;
        }
    }
}
