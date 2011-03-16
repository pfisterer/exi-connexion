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
package exi.events;


/**
 * <p>
 * This class represents a fixed EXI event code as described in
 * {@link <a href="http://www.w3.org/TR/exi/#fixedEventCodes">Section 9.1.1</a>}
 * of the EXI documentation.
 * </p>
 *
 * @author Marco Wegner
 */
public class ExiEventCode {

    // ------------------------------------------------------------------------
    // Member variables
    // ------------------------------------------------------------------------

    /**
     * <p>
     * This event code's length. An event code's length is defined as the
     * number of parts in a given event code.
     * </p>
     */
    private int length = -1;

    // ------------------------------------------------------------------------

    /**
     * <p>
     * The parts of the EXI event code.
     * </p>
     */
    private final int[] parts = new int[] { 0, 0, 0 };

    // ------------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------------

    /**
     * <p>
     * Creates a new EXI event code from parsing an event code string.
     * </p>
     *
     * <p>
     * This constructor is being tested in ExiEventCodeTest.
     * </p>
     *
     * @param code The event code string.
     * @throws ExiMalformedEventCodeException If the string has a form which
     *         does not represent a valid EXI event code.
     */
    public ExiEventCode(String code) throws ExiMalformedEventCodeException {
        parseCode(code);
    }

    // ------------------------------------------------------------------------

    /**
     * <p>
     * Creates a new EXI event code of length one.
     * </p>
     *
     * <p>
     * This constructor is being tested in ExiEventCodeTest.
     * </p>
     *
     * @param p1 The first part of the EXI event code.
     * @throws ExiMalformedEventCodeException If any of the event code's parts
     *         is negative.
     */
    public ExiEventCode(int p1) throws ExiMalformedEventCodeException {
        if (p1 < 0) {
            throw new ExiMalformedEventCodeException(String.format("%d", p1));
        }

        this.length = 1;
        this.parts[0] = p1;
    }

    // ------------------------------------------------------------------------

    /**
     * <p>
     * Creates a new EXI event code of length two.
     * </p>
     *
     * <p>
     * This constructor is being tested in ExiEventCodeTest.
     * </p>
     *
     * @param p1 The first part of the EXI event code.
     * @param p2 The second part of the EXI event code.
     * @throws ExiMalformedEventCodeException If any of the event code's parts
     *         is negative.
     */
    public ExiEventCode(int p1, int p2) throws ExiMalformedEventCodeException {
        if (p1 < 0 || p2 < 0) {
            throw new ExiMalformedEventCodeException(String.format("%d, %d", p1, p2));
        }

        this.length = 2;
        this.parts[0] = p1;
        this.parts[1] = p2;
    }

    // ------------------------------------------------------------------------

    /**
     * <p>
     * Creates a new EXI event code of length three.
     * </p>
     *
     * <p>
     * This constructor is being tested in ExiEventCodeTest.
     * </p>
     *
     * @param p1 The first part of the EXI event code.
     * @param p2 The second part of the EXI event code.
     * @param p3 The third part of the EXI event code.
     * @throws ExiMalformedEventCodeException If any of the event code's parts
     *         is negative.
     */
    public ExiEventCode(int p1, int p2, int p3) throws ExiMalformedEventCodeException {
        if (p1 < 0 || p2 < 0 || p3 < 0) {
            throw new ExiMalformedEventCodeException(String.format("%d, %d, %d", p1, p2, p3));
        }

        this.length = 3;
        this.parts[0] = p1;
        this.parts[1] = p2;
        this.parts[2] = p3;
    }

    // ------------------------------------------------------------------------
    // Setters and getters
    // ------------------------------------------------------------------------

    /**
     * <p>
     * Returns this event code's length.
     * </p>
     *
     * @return This event code's length.
     */
    public int getLength( ) {
        return this.length;
    }

    // ------------------------------------------------------------------------

    /**
     * Returns the value of the specified part.
     *
     * @param n The part number.
     * @return The part's value.
     */
    public int getPart(int n) {
        if (n < 0 || n > 2) {
            throw new IllegalArgumentException("Part number must be either 0, 1, or 2!");
        }
        return this.parts[n];
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    /**
     * Increments the event code in the specified part.
     *
     * @param part The part number.
     */
    public void increment(int part) {
        if (this.length <= part) {
            throw new IllegalArgumentException("Part number must have a value <= " + (this.length - 1));
        }
        this.parts[part]++;
    }

    // ------------------------------------------------------------------------

    /**
     * <p>
     * Parsing an event code from a string.
     * </p>
     *
     * @param code The event code string.
     * @throws ExiMalformedEventCodeException If the string has a form which
     *         does not represent a valid EXI event code.
     */
    private void parseCode(String code) throws ExiMalformedEventCodeException {

        int[] temp = new int[3];

        /*
         * The second parameter of the method String.split( ) is needed since
         * otherwise trailing empty strings would be disregarded. Which is not
         * the correct behaviour since such strings as "0.1.2." would then be
         * viewed as having only three parts.
         */
        String[] sarray = code.split("\\.", -1);
        int l = sarray.length;

        if (l == 0 || l > 3) {
            throw new ExiMalformedEventCodeException(code);
        }

        for (int i = 0; i < l; ++i) {
            try {
                temp[i] = Integer.parseInt(sarray[i]);
            } catch (NumberFormatException e) {
                throw new ExiMalformedEventCodeException(code);
            }

            if (temp[i] < 0) {
                throw new ExiMalformedEventCodeException(code);
            }
        }

        this.length = l;
        for (int i = 0; i < l; ++i) {
            this.parts[i] = temp[i];
        }
    }

    // ------------------------------------------------------------------------
    // Overwritten methods
    // ------------------------------------------------------------------------

    /**
     * <p>
     * Returns the string representation of this event code.
     * </p>
     *
     * <p>
     * This method is being tested in ExiEventCodeTest.
     * </p>
     */
    @Override
    public String toString( ) {
        StringBuffer sb = new StringBuffer( );
        for (int i = 0; i < this.length; ++i) {
            sb.append(String.format("%s%d", (i > 0 ? "." : ""), this.parts[i]));
        }
        return sb.toString( );
    }

    // ------------------------------------------------------------------------

    /**
     * Event codes are viewed as equal if the length is the same in both objects
     * and the value for the parts is the same as well.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ExiEventCode) {
            ExiEventCode code = (ExiEventCode)obj;
            if (this.length == code.length) {
                boolean isEqual = true;
                for (int i = 0; i < this.length; ++i) {
                    isEqual = isEqual && this.parts[i] == code.parts[i];
                }
                return isEqual;
            }
            return false;
        }
        return false;
    }
}
