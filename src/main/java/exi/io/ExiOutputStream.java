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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

import exi.utils.ExiFloat;

/**
 * EXI output stream.
 *
 * @author Marco Wegner
 */
public class ExiOutputStream extends ByteArrayOutputStream {

    // ------------------------------------------------------------------------
    // Member variables
    // ------------------------------------------------------------------------

    /**
     * Specifies whether this stream stores values aligned to bytes or not.
     */
    private boolean byteAligned;

    // ------------------------------------------------------------------------

    /**
     * The write buffer.
     */
    private int buffer = 0;

    // ------------------------------------------------------------------------

    /**
     * This variable represents the number of bits already written in the
     * current buffer. It also represents the position of the next bit to be
     * written in the buffer.
     */
    private int bitPos = 0;

    // ------------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------------

    /**
     * Constructs a new EXI output stream.
     */
    public ExiOutputStream( ) {
        this(false);
    }

    // ------------------------------------------------------------------------

    /**
     * Constructs a new EXI output stream using the specified alignment.
     *
     * @param byteAligned <code>true</code> if this stream is to be initially
     *        byte-aligned, else <code>false</code>.
     */
    protected ExiOutputStream(boolean byteAligned) {
        super( );
        this.byteAligned = byteAligned;
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    /**
     * Sets this stream to writing values byte-aligned. This cannot be undone.
     * The stream is flushed to get unwritten bits in the buffer written to the
     * stream.
     *
     * @throws IOException If some goes wrong during flushing.
     */
    public void setByteAligned( ) throws IOException {
        flush( );
        this.byteAligned = true;
    }

    // ------------------------------------------------------------------------

    /**
     * Writes up to eight bits to the stream. If this output stream is in
     * byte-aligned mode, then the full eight bits are written even if a smaller
     * number is specified as parameter.
     *
     * @param value The value to be written.
     * @param bits The number of bits to use.
     */
    public void writeBits(int value, int bits) {
        if (bits < 0 || bits > 8) {
            throw new IllegalArgumentException("Value for bits must be between 0 and 8!");
        }
        if (bits == 0) {
            // do nothing
            return;
        }

        if (this.byteAligned) {
            write(value);
        } else {    // bit-packed alignment
            if (this.bitPos + bits > 8) {
                // value to be written crosses the byte border

                // the number of bits that will go into the next buffer byte
                int writeNext = this.bitPos + bits - 8;

                // fill the current buffer byte, then write it to the stream
                this.buffer |= ((value >> writeNext) & 0xFF);
                write(this.buffer);
                // put the remaining bits into the next buffer byte
                this.buffer = (value << (8 - writeNext)) & 0xFF;
                this.bitPos = writeNext;
            } else {
                // value to be written completely fits into the buffer
                this.buffer |= (value << (8 - this.bitPos - bits));
                this.bitPos += bits;
            }
        }
    }

    // ------------------------------------------------------------------------

    /**
     * <p>
     * Writes Binary data to the stream.
     * </p>
     * <p>
     * Binary data is encoded as described in
     * {@link <a href="http://www.w3.org/TR/exi/#encodingBinary">section 7.1.1</a>}
     * of the EXI documentation.
     * </p>
     *
     * @param array The Binary data to be written, represented as an array of
     *            bytes.
     */
    public void writeBinary(byte[] array) {
        /*
         * 7.1.1 Binary
         *
         * Values typed as Binary are represented as a length-prefixed sequence
         * of octets representing the binary content. The length is represented
         * as an Unsigned Integer (see 7.1.6 Unsigned Integer).
         */

        writeUnsignedInteger(BigInteger.valueOf(array.length));
        for (byte b : array) {
            writeBits(b, 8);
        }
    }

    // ------------------------------------------------------------------------

    /**
     * <p>
     * Writes a Boolean value to the stream.
     * </p>
     * <p>
     * Boolean values are encoded as described in
     * {@link <a href="http://www.w3.org/TR/exi/#encodingBoolean">section 7.1.2</a>}
     * of the EXI documentation.
     * </p>
     *
     * @param value The Boolean value to be written.
     */
    public void writeBoolean(boolean value) {
        /*
         * 7.1.2 Boolean
         *
         * When the EXI compression option is set to false, values typed as
         * Boolean are represented using one bit, otherwise they are represented
         * using one byte. The value zero (0) represents false and the value one
         * (1) represents true.
         */

        byte byteValue = (byte)(value ? 1 : 0);
        writeBits(byteValue, 1);
    }

    // ------------------------------------------------------------------------

    /**
     * <p>
     * Writes a Decimal value to the stream.
     * </p>
     * <p>
     * Decimal values are encoded as described in
     * {@link <a href="http://www.w3.org/TR/exi/#encodingDecimal">section 7.1.3</a>}
     * of the EXI documentation.
     * </p>
     *
     * @param value The decimal value to be written.
     */
    public void writeDecimal(BigDecimal value) {
        /*
         * 7.1.3 Decimal
         *
         * Values typed as Decimal are represented as a Boolean sign (see 7.1.2
         * Boolean) followed by two Unsigned Integers (see 7.1.6 Unsigned
         * Integer). A sign value of zero (0) is used to represent positive
         * Decimal values and a sign value of one (1) is used to represent
         * negative Decimal values. The first Unsigned Integer represents the
         * integral portion of the Decimal value. The second Unsigned Integer
         * represents the fractional portion of the Decimal value with the
         * digits in reverse order to preserve leading zeros.
         */

        String plain = value.abs( ).toPlainString( );
        String[] arr = plain.split("\\.");

        BigInteger integral = new BigInteger(arr[0]);
        BigInteger fractional = BigInteger.ZERO;

        // the array can have length 1 if the decimal value is e.g. 123
        if (arr.length > 1) {
            StringBuffer sb = new StringBuffer( );
            // reverse the digits
            for (char c : arr[1].toCharArray( )) {
                sb.insert(0, c);
            }
            fractional = new BigInteger(sb.toString( ));
        }

        writeBoolean(value.signum( ) < 0);
        writeUnsignedInteger(integral);
        writeUnsignedInteger(fractional);
    }

    // ------------------------------------------------------------------------

    /**
     * <p>
     * Writes a Float value to the stream.
     * </p>
     * <p>
     * Float values are encoded as described in
     * {@link <a href="http://www.w3.org/TR/exi/#encodingFloat">section 7.1.4</a>}
     * of the EXI documentation.
     * </p>
     *
     * @param value The Float value to be written.
     */
    public void writeFloat(ExiFloat value) {
        /*
         * 7.1.4 Float
         *
         * Values typed as Float are represented as two consecutive Integers
         * (see 7.1.5 Integer). The first Integer represents the mantissa of the
         * floating point number and the second Integer represents the base-10
         * exponent of the floating point number. The range of the mantissa is
         * -(2^63) to 2^63-1 and the range of the exponent is -(2^14-1) to
         * 2^14-1. Values typed as Float with a mantissa or exponent outside the
         * accepted range are represented as schema-invalid values.
         */

        // special exponent value -(2^14) for NaN, Infinity and -Infinity
        final BigInteger errExpo = BigInteger.ONE.shiftLeft(14).negate( );

        BigInteger mantissa = value.unscaledValue( );
        BigInteger exponent = BigInteger.valueOf(value.scale( ));

        if (value.isNaN( )) {
            mantissa = BigInteger.ZERO; // can be anything aside from 1 and -1
            exponent = errExpo;
        } else if (value.isPositiveInfinite( )) {
            mantissa = BigInteger.ONE;
            exponent = errExpo;
        } else if (value.isNegativeInfinite( )) {
            mantissa = BigInteger.ONE.negate( );
            exponent = errExpo;
        }

        writeInteger(mantissa);
        writeInteger(exponent);
    }

    // ------------------------------------------------------------------------

    /**
     * <p>
     * Writes an Integer value to the stream.
     * </p>
     * <p>
     * Integer values are encoded as described in
     * {@link <a href="http://www.w3.org/TR/exi/#encodingInteger">section 7.1.5</a>}
     * of the EXI documentation.
     * </p>
     *
     * @param value The value to be written.
     */
    public void writeInteger(BigInteger value) {
        /*
         * 7.1.5 Integer
         *
         * The Integer type supports signed integer numbers of arbitrary
         * magnitude. Values typed as Integer are represented as a Boolean sign
         * (see 7.1.2 Boolean) followed by an Unsigned Integer (see 7.1.6
         * Unsigned Integer). A sign value of zero (0) is used to represent
         * positive integers and a sign value of one (1) is used to represent
         * negative integers. For non-negative values, the Unsigned Integer
         * holds the magnitude of the value. For negative values, the Unsigned
         * Integer holds the magnitude of the value minus 1.
         */

        boolean isNegative = value.signum( ) < 0;
        BigInteger magnitude = value.abs( );

        writeBoolean(isNegative);
        writeUnsignedInteger(isNegative ? magnitude.subtract(BigInteger.ONE) : magnitude);
    }

    // ------------------------------------------------------------------------

    /**
     * Writes an Unsigned Integer value to the stream.
     *
     * Unsigned Integer values are encoded as described in
     * {@link <a href="http://www.w3.org/TR/exi/#encodingUnsignedInteger">section 7.1.6</a>}
     * of the EXI documentation.
     *
     * @param value The value to be written.
     */
    public void writeUnsignedInteger(BigInteger value) {
        /*
         * 7.1.6 Unsigned Integer
         *
         * The Unsigned Integer type supports unsigned integer numbers of
         * arbitrary magnitude. Values typed as Unsigned Integer are represented
         * using a sequence of octets. The sequence is terminated by an octet
         * with its most significant bit set to 0. The value of the unsigned
         * integer is stored in the least significant 7 bits of the octets as a
         * sequence of 7-bit bytes, with the least significant byte first.
         */

        BigInteger x7F = BigInteger.valueOf(0x7F);      // 0111 1111
        BigInteger x80 = BigInteger.valueOf(0x80);      // 1000 0000

        BigInteger temp = value;
        while (temp.compareTo(x7F) > 0) {
            writeBits(temp.and(x7F.or(x80)).byteValue( ), 8);
            temp = temp.shiftRight(7);
        }
        writeBits(temp.and(x7F).byteValue( ), 8);
    }

    // ------------------------------------------------------------------------

    /**
     * <p>
     * Writes an n-bit Unsigned Integer value to the stream.
     * </p>
     * <p>
     * n-bit Unsigned Integer values are encoded as described in
     * {@link <a href="http://www.w3.org/TR/exi/#encodingBoundedUnsigned">section 7.1.9</a>}
     * of the EXI documentation.
     * </p>
     *
     * @param value The value to be written.
     * @param bits The number of bits to write.
     */
    public void writeNBitUnsignedInteger(BigInteger value, int bits) {
        /*
         * 7.1.9 n-bit Unsigned Integer
         *
         * When the value of compression option is false and the value
         * bit-packed is used for alignment options, values of type n-bit
         * Unsigned Integer are represented as an unsigned binary integer using
         * n bits. Otherwise, they are represented as an unsigned integer using
         * the minimum number of bytes required to store n bits. Bytes are
         * ordered with the least significant byte first.
         */

        BigInteger temp = value;
        int b = bits;
        while (b > 8) {
            writeBits(temp.and(BigInteger.valueOf(0xFF)).byteValue( ), 8);
            temp.shiftRight(8);
            b -= 8;
        }
        writeBits(temp.byteValue( ), b);
    }

    // ------------------------------------------------------------------------

    /**
     * <p>
     * Writes a String value to the stream.
     * </p>
     * <p>
     * String values are encoded as described in
     * {@link <a href="http://www.w3.org/TR/exi/#encodingString">section 7.1.10</a>}
     * of the EXI documentation.
     * </p>
     *
     * @param s The string to be written.
     */
    public void writeString(String s) {
        /*
         * 7.1.10 String
         *
         * Values of type String are encoded as a length prefixed sequence of
         * characters. The length represents the number of characters in the
         * string and is encoded as an Unsigned Integer (see 7.1.6 Unsigned
         * Integer) and each character in the string is encoded as an Unsigned
         * Integer (see 7.1.6 Unsigned Integer) representing its UCS code point.
         */

        writeString(s, 0);
    }

    // ------------------------------------------------------------------------

    /**
     * <p>
     * Writes a string to the stream where the length can be the real value
     * incrementes by the specified number.
     * </p>
     * <p>
     * This method is mostly used for "value miss" and "local-name miss".
     * </p>
     *
     * @param s The string to be written.
     * @param increment The value by which the actual string length is to be
     *        increased.
     */
    public void writeString(String s, int increment) {
        int len = s.length( );
        writeUnsignedInteger(BigInteger.valueOf(len + increment));
        for (int i = 0; i < len; ++i) {
            writeUnsignedInteger(BigInteger.valueOf(s.codePointAt(i)));
        }
    }

    // ------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see exi.io.ExiOutputStream#flush()
     */
    @Override
    public void flush( ) throws IOException {
        if (!this.byteAligned) {
            if (this.bitPos > 0) {
                write(this.buffer);
                this.buffer = 0;
                this.bitPos = 0;
            }
        }
        super.flush( );
    }
}
