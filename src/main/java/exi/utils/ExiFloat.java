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
package exi.utils;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * This class represents a float value in EXI. It contains additional
 * characteristics as compared to the underlying {@link BigDecimal}.
 *
 * @author Marco Wegner
 */
public class ExiFloat extends BigDecimal {

    // ------------------------------------------------------------------------
    // Constants
    // ------------------------------------------------------------------------

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = -7532313701870290591L;

    // ------------------------------------------------------------------------
    // Member variables
    // ------------------------------------------------------------------------

    /**
     * Specifies whether this EXI float represents NaN (not a number.
     */
    private boolean isNaN = false;

    // ------------------------------------------------------------------------

    /**
     * Specifies whether this EXI float is positive infinite.
     */
    private boolean isPositiveInfinite = false;

    // ------------------------------------------------------------------------

    /**
     * Specifies whether this EXI float is negative infinite.
     */
    private boolean isNegativeInfinite = false;

    // ------------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------------

    /**
     * Constructs a new EXI float from an unscaled {@link BigInteger} and a
     * scale value.
     *
     * @param unscaledVal The unscaled BigInteger value.
     * @param scale The scale.
     */
    public ExiFloat(BigInteger unscaledVal, int scale) {
        super(unscaledVal, scale);
    }

    // ------------------------------------------------------------------------

    /**
     * Constructs an EXI float from a double value.
     *
     * @param val The double value.
     */
    public ExiFloat(double val) {
        super(val);
    }

    // ------------------------------------------------------------------------

    /**
     * Constructs a new EXI float from a string.
     *
     * @param val The string representing the EXI float.
     */
    public ExiFloat(String val) {
        super(val);
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    /**
     * Sets this EXI float to represent NaN (not a number).
     *
     * @param isNaN <code>true</code> if this EXI float represents NaN, else
     *        false.
     */
    protected void setNaN(boolean isNaN) {
        this.isNaN = isNaN;
    }

    // ------------------------------------------------------------------------

    /**
     * Specifies whether this EXI float represents NaN (not a number).
     *
     * @return <code>true</code> if this EXI float represents NaN, else false.
     */
    public boolean isNaN( ) {
        return this.isNaN;
    }

    // ------------------------------------------------------------------------

    /**
     * Set this EXI float's value as positive infinite.
     *
     * @param isPositiveInfinite <code>true</code> if this EXI float is going
     *        to be positive infinite, else false.
     */
    protected void setPositiveInfinite(boolean isPositiveInfinite) {
        this.isPositiveInfinite = isPositiveInfinite;
    }

    // ------------------------------------------------------------------------

    /**
     * Specifies whether this EXI float is positive infinite.
     *
     * @return <code>true</code> if this EXI float is positive infinite, else
     *         false.
     */
    public boolean isPositiveInfinite( ) {
        return this.isPositiveInfinite;
    }

    // ------------------------------------------------------------------------

    /**
     * Set this EXI float's value as negative infinite.
     *
     * @param isNegativeInfinite <code>true</code> if this EXI float is going
     *        to be negative infinite, else false.
     */
    protected void setNegativeInfinite(boolean isNegativeInfinite) {
        this.isNegativeInfinite = isNegativeInfinite;
    }

    // ------------------------------------------------------------------------

    /**
     * Specifies whether this EXI float is negative infinite.
     *
     * @return <code>true</code> if this EXI float is negative infinite, else
     *         false.
     */
    public boolean isNegativeInfinite( ) {
        return this.isNegativeInfinite;
    }
}
