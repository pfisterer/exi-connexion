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

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import exi.ExiException;

/**
 * Test class for {@link ExiEventCodeGenerator}.
 *
 * @author Marco Wegner
 */
public class ExiEventCodeGeneratorTest {

    // ------------------------------------------------------------------------
    // Member variables
    // ------------------------------------------------------------------------

    /**
     * Instance for testing.
     */
    private ExiEventCodeGenerator g;

    // ------------------------------------------------------------------------
    // JUnit stuff
    // ------------------------------------------------------------------------

    /**
     * @throws Exception If something goes wrong.
     */
    @Before
    public void setUp( ) throws Exception {
        this.g = new ExiEventCodeGenerator( );
    }

    // ------------------------------------------------------------------------

    /**
     * @throws Exception If something goes wrong.
     */
    @After
    public void tearDown( ) throws Exception {
        this.g = null;
    }

    // ------------------------------------------------------------------------
    // Test methods for getNextCode(int)
    // ------------------------------------------------------------------------

    /**
     * Checks for correct code generation if the previous length has been 0 and
     * we want a code of length 1.
     *
     * @throws ExiException If something goes wrong during event code creation.
     */
    @Test
    public void testGetNextCode01( ) throws ExiException {
        assertEquals("0", this.g.getNextCode(1).toString( ));
    }

    // ------------------------------------------------------------------------

    /**
     * Checks for correct code generation if the previous length has been 0 and
     * we want a code of length 2.
     *
     * @throws ExiException If something goes wrong during event code creation.
     */
    @Test
    public void testGetNextCode02( ) throws ExiException {
        assertEquals("0.0", this.g.getNextCode(2).toString( ));
    }

    // ------------------------------------------------------------------------

    /**
     * Checks for correct code generation if the previous length has been 0 and
     * we want a code of length 3.
     *
     * @throws ExiException If something goes wrong during event code creation.
     */
    @Test
    public void testGetNextCode03( ) throws ExiException {
        assertEquals("0.0.0", this.g.getNextCode(3).toString( ));
    }

    // ------------------------------------------------------------------------

    /**
     * Simple test examples.
     *
     * @throws ExiException If something goes wrong during event code creation.
     */
    @Test
    public void testGetNextCode04( ) throws ExiException {
        assertEquals("0", this.g.getNextCode(1).toString( ));
        assertEquals("1", this.g.getNextCode(1).toString( ));
        assertEquals("2", this.g.getNextCode(1).toString( ));
        assertEquals("3", this.g.getNextCode(1).toString( ));
    }

    // ------------------------------------------------------------------------

    /**
     * Simple test examples.
     *
     * @throws ExiException If something goes wrong during event code creation.
     */
    @Test
    public void testGetNextCode05( ) throws ExiException {
        assertEquals("0", this.g.getNextCode(1).toString( ));
        assertEquals("1.0", this.g.getNextCode(2).toString( ));
    }

    // ------------------------------------------------------------------------

    /**
     * Simple test examples.
     *
     * @throws ExiException If something goes wrong during event code creation.
     */
    @Test
    public void testGetNextCode06( ) throws ExiException {
        assertEquals("0", this.g.getNextCode(1).toString( ));
        assertEquals("1", this.g.getNextCode(1).toString( ));
        assertEquals("2.0", this.g.getNextCode(2).toString( ));
        assertEquals("2.1", this.g.getNextCode(2).toString( ));
        assertEquals("2.2", this.g.getNextCode(2).toString( ));
        assertEquals("2.3.0", this.g.getNextCode(3).toString( ));
        assertEquals("2.3.1", this.g.getNextCode(3).toString( ));
        assertEquals("2.3.2", this.g.getNextCode(3).toString( ));
        assertEquals("2.3.3", this.g.getNextCode(3).toString( ));
    }
}
