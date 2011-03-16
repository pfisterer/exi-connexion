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
package exi;

/**
 * Constants used in EXI.
 *
 * @author Marco Wegner
 */
public interface ExiConstants {

    // ------------------------------------------------------------------------
    // General constants
    // ------------------------------------------------------------------------

    /**
     * The EXI format version.
     */
    public static final String formatVersion = "1";

    // ------------------------------------------------------------------------
    // Default values for EXI options (including fidelity options)
    // ------------------------------------------------------------------------

    /**
     * Default value for the EXI option alignment.
     */
    public static final ExiOptions.Alignment ALIGN_DEFAULT = ExiOptions.Alignment.BIT_PACKED;

    // ------------------------------------------------------------------------

    /**
     * Default value for the EXI option compression.
     */
    public static final boolean USE_COMPRESSION_DEFAULT = false;

    // ------------------------------------------------------------------------

    /**
     * Default value for the EXI option fragment.
     */
    public static final boolean USE_FRAGMENTS_DEFAULT = false;

    // ------------------------------------------------------------------------

    /**
     * Default value for the EXI option schemaID.
     */
    public static final String SCHEMA_ID_DEFAULT = null;

    // ------------------------------------------------------------------------

    /**
     * Default value for the EXI option codecMap.
     */
    public static final Object CODEC_MAP_DEFAULT = null;

    // ------------------------------------------------------------------------

    /**
     * Default value for the EXI option blockSize.
     */
    public static final int BLOCK_SIZE_DEFAULT = 1000000;

    // ------------------------------------------------------------------------

    /**
     * Default value for the fidelity option Preserve.comments.
     */
    public static final boolean PRESERVE_COMMENTS_DEFAULT = false;

    // ------------------------------------------------------------------------

    /**
     * Default value for the fidelity option Preseve.pis.
     */
    public static final boolean PRESERVE_PI_DEFAULT = false;

    // ------------------------------------------------------------------------

    /**
     * Default value for the fidelity option Preserve.dtd.
     */
    public static final boolean PRESERVE_DTD_DEFAULT = false;

    // ------------------------------------------------------------------------

    /**
     * Default value for the fidelity option Preserve.prefixes.
     */
    public static final boolean PRESERVE_PREFIX_DEFAULT = false;

    // ------------------------------------------------------------------------

    /**
     * Default value for the fidelity option Preserve.lexicalValues.
     */
    public static final boolean PRESERVE_LEXICAL_DEFAULT = false;
}
