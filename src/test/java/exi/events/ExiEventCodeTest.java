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
package exi.events;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link ExiEventCode}.
 *
 * @author Marco Wegner
 */
public class ExiEventCodeTest {

    // ------------------------------------------------------------------------
    // JUnit stuff
    // ------------------------------------------------------------------------

    /**
     * @throws Exception If something goes wrong.
     */
    @Before
    public void setUp( ) throws Exception {
        //
    }

    // ------------------------------------------------------------------------

    /**
     * @throws Exception If something goes wrong.
     */
    @After
    public void tearDown( ) throws Exception {
        //
    }

    // ------------------------------------------------------------------------
    // Test methods for ExiEventCode(String)
    // ------------------------------------------------------------------------

    /**
     * <p>
     * Test method for {@link exi.events.ExiEventCode#ExiEventCode(java.lang.String)}.
     * </p>
     *
     * <p>
     * Tests whether an empty string correctly leads to an exception.
     * </p>
     *
     * @throws ExiMalformedEventCodeException Should be thrown if everything
     *         goes fine.
     */
    @Test(expected=ExiMalformedEventCodeException.class)
    public void testExiEventCodeString01( ) throws ExiMalformedEventCodeException {
        new ExiEventCode("");
    }

    // ------------------------------------------------------------------------

    /**
     * <p>
     * Test method for {@link exi.events.ExiEventCode#ExiEventCode(java.lang.String)}.
     * </p>
     *
     * <p>
     * Tests whether a string consisting of just a dot correctly leads to an
     * exception.
     * </p>
     *
     * @throws ExiMalformedEventCodeException Should be thrown if everything
     *         goes fine.
     */
    @Test(expected=ExiMalformedEventCodeException.class)
    public void testExiEventCodeString02( ) throws ExiMalformedEventCodeException {
        new ExiEventCode(".");
    }

    // ------------------------------------------------------------------------

    /**
     * <p>
     * Test method for {@link exi.events.ExiEventCode#ExiEventCode(java.lang.String)}.
     * </p>
     *
     * <p>
     * Tests whether a string consisting of just a character correctly leads to
     * an exception.
     * </p>
     *
     * @throws ExiMalformedEventCodeException Should be thrown if everything
     *         goes fine.
     */
    @Test(expected=ExiMalformedEventCodeException.class)
    public void testExiEventCodeString03( ) throws ExiMalformedEventCodeException {
        new ExiEventCode("m");
    }

    // ------------------------------------------------------------------------

    /**
     * <p>
     * Test method for {@link exi.events.ExiEventCode#ExiEventCode(java.lang.String)}.
     * </p>
     *
     * <p>
     * Tests whether a string consisting of the correct number of parts &mdash;
     * but some parts having characters as the content &mdash; correctly leads
     * to an exception.
     * </p>
     *
     * @throws ExiMalformedEventCodeException Should be thrown if everything
     *         goes fine.
     */
    @Test(expected=ExiMalformedEventCodeException.class)
    public void testExiEventCodeString04( ) throws ExiMalformedEventCodeException {
        new ExiEventCode("0.m.2");
    }

    // ------------------------------------------------------------------------

    /**
     * <p>
     * Test method for {@link exi.events.ExiEventCode#ExiEventCode(java.lang.String)}.
     * </p>
     *
     * <p>
     * Tests whether a string consisting of just a leading dot and a number
     * correctly leads to an exception.
     * </p>
     *
     * @throws ExiMalformedEventCodeException Should be thrown if everything
     *         goes fine.
     */
    @Test(expected=ExiMalformedEventCodeException.class)
    public void testExiEventCodeString05( ) throws ExiMalformedEventCodeException {
        new ExiEventCode(".0");
    }

    // ------------------------------------------------------------------------

    /**
     * <p>
     * Test method for {@link exi.events.ExiEventCode#ExiEventCode(java.lang.String)}.
     * </p>
     *
     * <p>
     * Tests whether a string consisting of just a number and a trailing dot
     * correctly leads to an exception.
     * </p>
     *
     * @throws ExiMalformedEventCodeException Should be thrown if everything
     *         goes fine.
     */
    @Test(expected=ExiMalformedEventCodeException.class)
    public void testExiEventCodeString06( ) throws ExiMalformedEventCodeException {
        new ExiEventCode("0.");
    }

    // ------------------------------------------------------------------------

    /**
     * <p>
     * Test method for {@link exi.events.ExiEventCode#ExiEventCode(java.lang.String)}.
     * </p>
     *
     * <p>
     * Tests whether a string consisting of three correct parts and a trailing
     * dot correctly leads to an exception.
     * </p>
     *
     * @throws ExiMalformedEventCodeException Should be thrown if everything
     *         goes fine.
     */
    @Test(expected=ExiMalformedEventCodeException.class)
    public void testExiEventCodeString07( ) throws ExiMalformedEventCodeException {
        new ExiEventCode("0.1.2.");
    }

    // ------------------------------------------------------------------------

    /**
     * <p>
     * Test method for {@link exi.events.ExiEventCode#ExiEventCode(java.lang.String)}.
     * </p>
     *
     * <p>
     * Tests whether a string consisting of a too large number of parts
     * correctly leads to an exception.
     * </p>
     *
     * @throws ExiMalformedEventCodeException Should be thrown if everything
     *         goes fine.
     */
    @Test(expected=ExiMalformedEventCodeException.class)
    public void testExiEventCodeString08( ) throws ExiMalformedEventCodeException {
        new ExiEventCode("0.1.2.3");
    }

    // ------------------------------------------------------------------------

    /**
     * <p>
     * Test method for {@link exi.events.ExiEventCode#ExiEventCode(java.lang.String)}.
     * </p>
     *
     * <p>
     * Tests whether a string consisting of just a negative number correctly
     * leads to an exception.
     * </p>
     *
     * @throws ExiMalformedEventCodeException Should be thrown if everything
     *         goes fine.
     */
    @Test(expected=ExiMalformedEventCodeException.class)
    public void testExiEventCodeString09( ) throws ExiMalformedEventCodeException {
        new ExiEventCode("-1");
    }

    // ------------------------------------------------------------------------

    /**
     * <p>
     * Test method for {@link exi.events.ExiEventCode#ExiEventCode(java.lang.String)}.
     * </p>
     *
     * <p>
     * Tests whether a string consisting of the correct number of parts &mdash;
     * but some parts having negative numbers as the content &mdash; correctly
     * leads to an exception.
     * </p>
     *
     * @throws ExiMalformedEventCodeException Should be thrown if everything
     *         goes fine.
     */
    @Test(expected=ExiMalformedEventCodeException.class)
    public void testExiEventCodeString10( ) throws ExiMalformedEventCodeException {
        new ExiEventCode("0.-1.2");
    }

    // ------------------------------------------------------------------------

    /**
     * <p>
     * Test method for {@link exi.events.ExiEventCode#ExiEventCode(java.lang.String)}.
     * </p>
     *
     * <p>
     * Tests whether an event code of length one is parsed and generated
     * correctly.
     * </p>
     *
     * @throws ExiMalformedEventCodeException If something went wrong during
     *         parsing the event code string.
     */
    @Test
    public void testExiEventCodeString11( ) throws ExiMalformedEventCodeException {
        String s = "0";
        ExiEventCode code = new ExiEventCode(s);
        assertEquals(1, code.getLength( ));
        assertEquals(s, code.toString( ));
    }

    // ------------------------------------------------------------------------

    /**
     * <p>
     * Test method for {@link exi.events.ExiEventCode#ExiEventCode(java.lang.String)}.
     * </p>
     *
     * <p>
     * Tests whether an event code of length two is parsed and generated
     * correctly.
     * </p>
     *
     * @throws ExiMalformedEventCodeException If something went wrong during
     *         parsing the event code string.
     */
    @Test
    public void testExiEventCodeString12( ) throws ExiMalformedEventCodeException {
        String s = "0.3";
        ExiEventCode code = new ExiEventCode(s);
        assertEquals(2, code.getLength( ));
        assertEquals(s, code.toString( ));
    }

    // ------------------------------------------------------------------------

    /**
     * <p>
     * Test method for {@link exi.events.ExiEventCode#ExiEventCode(java.lang.String)}.
     * </p>
     *
     * <p>
     * Tests whether an event code of length three is parsed and generated
     * correctly.
     * </p>
     *
     * @throws ExiMalformedEventCodeException If something went wrong during
     *         parsing the event code string.
     */
    @Test
    public void testExiEventCodeString13( ) throws ExiMalformedEventCodeException {
        String s = "0.6.0";
        ExiEventCode code = new ExiEventCode(s);
        assertEquals(3, code.getLength( ));
        assertEquals(s, code.toString( ));
    }

    // ------------------------------------------------------------------------
    // Test methods for ExiEventCode(int)
    // ------------------------------------------------------------------------

    /**
     * <p>
     * Test method for {@link exi.events.ExiEventCode#ExiEventCode(int)}.
     * </p>
     *
     * <p>
     * Tests whether supplying a negative number for the single event code's
     * part correctly results in a thrown exception.
     * </p>
     *
     * @throws ExiMalformedEventCodeException Should be thrown if everything
     *         goes fine.
     */
    @Test(expected=ExiMalformedEventCodeException.class)
    public void testExiEventCodeInt01( ) throws ExiMalformedEventCodeException {
        new ExiEventCode(-1);
    }

    // ------------------------------------------------------------------------

    /**
     * <p>
     * Test method for {@link exi.events.ExiEventCode#ExiEventCode(int)}.
     * </p>
     *
     * <p>
     * Tests whether supplying a correct value results in a correct EXI event
     * code.
     * </p>
     *
     * @throws ExiMalformedEventCodeException If something went wrong during
     *         parsing the event code string.
     */
    @Test
    public void testExiEventCodeInt02( ) throws ExiMalformedEventCodeException {
        ExiEventCode code = new ExiEventCode(0);
        assertEquals(1, code.getLength( ));
        assertEquals("0", code.toString( ));
    }

    // ------------------------------------------------------------------------
    // Test methods for ExiEventCode(int, int)
    // ------------------------------------------------------------------------

    /**
     * <p>
     * Test method for {@link exi.events.ExiEventCode#ExiEventCode(int, int)}.
     * </p>
     *
     * <p>
     * Tests whether supplying a negative number for the event code's first
     * part correctly results in a thrown exception.
     * </p>
     *
     * @throws ExiMalformedEventCodeException Should be thrown if everything
     *         goes fine.
     */
    @Test(expected=ExiMalformedEventCodeException.class)
    public void testExiEventCodeIntInt01( ) throws ExiMalformedEventCodeException {
        new ExiEventCode(-1, 3);
    }

    // ------------------------------------------------------------------------

    /**
     * <p>
     * Test method for {@link exi.events.ExiEventCode#ExiEventCode(int, int)}.
     * </p>
     *
     * <p>
     * Tests whether supplying a negative number for the event code's second
     * part correctly results in a thrown exception.
     * </p>
     *
     * @throws ExiMalformedEventCodeException Should be thrown if everything
     *         goes fine.
     */
    @Test(expected=ExiMalformedEventCodeException.class)
    public void testExiEventCodeIntInt02( ) throws ExiMalformedEventCodeException {
        new ExiEventCode(0, -3);
    }

    // ------------------------------------------------------------------------

    /**
     * <p>
     * Test method for {@link exi.events.ExiEventCode#ExiEventCode(int, int)}.
     * </p>
     *
     * <p>
     * Tests whether correct values for both parts results in a correct EXI
     * event code.
     * </p>
     *
     * @throws ExiMalformedEventCodeException If something went wrong during
     *         parsing the event code string.
     */
    @Test
    public void testExiEventCodeIntInt03( ) throws ExiMalformedEventCodeException {
        ExiEventCode code = new ExiEventCode(0, 3);
        assertEquals(2, code.getLength( ));
        assertEquals("0.3", code.toString( ));
    }

    // ------------------------------------------------------------------------
    // Test methods for ExiEventCode(int, int, int)
    // ------------------------------------------------------------------------

    /**
     * <p>
     * Test method for {@link exi.events.ExiEventCode#ExiEventCode(int, int, int)}.
     * </p>
     *
     * <p>
     * Tests whether supplying a negative number for the event code's first
     * part correctly results in a thrown exception.
     * </p>
     *
     * @throws ExiMalformedEventCodeException Should be thrown if everything
     *         goes fine.
     */
    @Test(expected=ExiMalformedEventCodeException.class)
    public void testExiEventCodeIntIntInt01( ) throws ExiMalformedEventCodeException {
        new ExiEventCode(-1, 3, 0);
    }

    // ------------------------------------------------------------------------

    /**
     * <p>
     * Test method for {@link exi.events.ExiEventCode#ExiEventCode(int, int, int)}.
     * </p>
     *
     * <p>
     * Tests whether supplying a negative number for the event code's second
     * part correctly results in a thrown exception.
     * </p>
     *
     * @throws ExiMalformedEventCodeException Should be thrown if everything
     *         goes fine.
     */
    @Test(expected=ExiMalformedEventCodeException.class)
    public void testExiEventCodeIntIntInt02( ) throws ExiMalformedEventCodeException {
        new ExiEventCode(0, -3, 0);
    }

    // ------------------------------------------------------------------------

    /**
     * <p>
     * Test method for {@link exi.events.ExiEventCode#ExiEventCode(int, int, int)}.
     * </p>
     *
     * <p>
     * Tests whether supplying a negative number for the event code's third
     * part correctly results in a thrown exception.
     * </p>
     *
     * @throws ExiMalformedEventCodeException Should be thrown if everything
     *         goes fine.
     */
    @Test(expected=ExiMalformedEventCodeException.class)
    public void testExiEventCodeIntIntInt03( ) throws ExiMalformedEventCodeException {
        new ExiEventCode(0, 3, -1);
    }

    // ------------------------------------------------------------------------

    /**
     * <p>
     * Test method for {@link exi.events.ExiEventCode#ExiEventCode(int, int, int)}.
     * </p>
     *
     * <p>
     * Tests whether correct values for all three parts results in a correct
     * EXI event code.
     * </p>
     *
     * @throws ExiMalformedEventCodeException If something went wrong during
     *         parsing the event code string.
     */
    @Test
    public void testExiEventCodeIntIntInt04( ) throws ExiMalformedEventCodeException {
        ExiEventCode code = new ExiEventCode(0, 6, 0);
        assertEquals(3, code.getLength( ));
        assertEquals("0.6.0", code.toString( ));
    }

    // ------------------------------------------------------------------------
    // Test methods for toString( )
    // ------------------------------------------------------------------------

    /**
     * <p>
     * Test method for {@link exi.events.ExiEventCode#toString()}.
     * </p>
     *
     * @throws ExiMalformedEventCodeException If something went wrong during
     *         parsing the event code string.
     */
    @Test
    public void testToString01( ) throws ExiMalformedEventCodeException {
        String code = "0";
        assertEquals(code, new ExiEventCode(code).toString( ));
    }

    // ------------------------------------------------------------------------

    /**
     * <p>
     * Test method for {@link exi.events.ExiEventCode#toString()}.
     * </p>
     *
     * @throws ExiMalformedEventCodeException If something went wrong during
     *         parsing the event code string.
     */
    @Test
    public void testToString02( ) throws ExiMalformedEventCodeException {
        String code = "0.3";
        assertEquals(code, new ExiEventCode(code).toString( ));
    }

    // ------------------------------------------------------------------------

    /**
     * <p>
     * Test method for {@link exi.events.ExiEventCode#toString()}.
     * </p>
     *
     * @throws ExiMalformedEventCodeException If something went wrong during
     *         parsing the event code string.
     */
    @Test
    public void testToString03( ) throws ExiMalformedEventCodeException {
        String code = "0.6.0";
        assertEquals(code, new ExiEventCode(code).toString( ));
    }
}
