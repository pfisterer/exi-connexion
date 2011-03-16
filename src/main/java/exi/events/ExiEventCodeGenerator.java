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

import exi.ExiException;

/**
 * Automatic generator for EXI event codes.
 *
 * @author Marco Wegner
 */
public class ExiEventCodeGenerator {

    // ------------------------------------------------------------------------
    // Member variables
    // ------------------------------------------------------------------------

    /**
     * The parts of an EXI event code.
     */
    private int[] p = new int[] { 0, 0, 0 };

    // ------------------------------------------------------------------------

    /**
     * The most recent event code length used.
     */
    private int length;

    // ------------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------------

    /**
     * Creates a new event code generator.
     */
    public ExiEventCodeGenerator( ) {
        reset( );
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    /**
     * Resets the values for the parts and the length to its initial values.
     */
    public void reset( ) {
        this.p[0] = this.p[1] = this.p[2] = 0;
        this.length = 0;
    }

    // ------------------------------------------------------------------------

    /**
     * Generates the next event code with the specified length.
     *
     * @param l The new length.
     * @return The newly generated event code.
     * @throws ExiException If something goes wrong during event code
     *         generation.
     * @throws IllegalArgumentException If the length parameter is not within
     *         the correct bounds (1 <= l <= 3).
     */
    public ExiEventCode getNextCode(int l) throws ExiException {
        if (l < 1 || l > 3) {
            throw new IllegalArgumentException("(1 <= l <= 3)");
        }
        if (this.length == 0) {
            this.length = l;
        } else if (l == this.length) {
            this.p[l-1]++;
        } else if (l == this.length+1) {
            this.length = l;
            this.p[l-2]++;
            this.p[l-1] = 0;
        } else {
            this.length = l;
            this.p[l-3]++;
            this.p[l-2] = 0;
            this.p[l-1] = 0;
        }

        return makeCode(l);
    }

    // ------------------------------------------------------------------------

    /**
     * <p>
     * Generates the event code with the specified length and the previously
     * adapted parts.
     * </p>
     *
     * <p>
     * This method assumes that the parameter numParts is in correct bounds!
     * </p>
     *
     * @param l The new length.
     * @return The newly generated event code.
     * @throws ExiException If something goes wrong during event code
     *         generation.
     */
    private ExiEventCode makeCode(int l) throws ExiException {
        if (l == 1) {
            return new ExiEventCode(this.p[0]);
        } else if (l == 2) {
            return new ExiEventCode(this.p[0], this.p[1]);
        } else {
            return new ExiEventCode(this.p[0], this.p[1], this.p[2]);
        }
    }
}
