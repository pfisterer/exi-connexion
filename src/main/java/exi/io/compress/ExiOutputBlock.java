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
package exi.io.compress;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.zip.DeflaterOutputStream;

import javax.xml.namespace.QName;

import exi.io.ExiOutputStream;

/**
 * This class represents a single block of EXI events used in EXI compression.
 *
 * @author Marco Wegner
 */
public class ExiOutputBlock {

    // ------------------------------------------------------------------------
    // Member variables
    // ------------------------------------------------------------------------

    /**
     * This block's structure channel.
     */
    private final ExiStructureOutputChannel chStruct = new ExiStructureOutputChannel( );

    // ------------------------------------------------------------------------

    /**
     * This block's value channels.
     */
    private final LinkedList<ExiValueOutputChannel> chValue = new LinkedList<ExiValueOutputChannel>( );

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /**
     * Constructs a new EXI block used in EXI compression.
     */
    public ExiOutputBlock( ) {
        super( );
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    /**
     * Returns this block's structure channel.
     *
     * @return This block's structure channel.
     */
    public ExiStructureOutputChannel getStructureChannel( ) {
        return this.chStruct;
    }

    // ------------------------------------------------------------------------

    /**
     * Returns this block's value channel for the specified QName. If no such
     * channel exists yet, a new one is created.
     *
     * @param qname The qualified name.
     * @return The value channel for the qualified name.
     */
    public ExiValueOutputChannel getValueChannel(QName qname) {
        ExiValueOutputChannel ch = null;
        for (ExiValueOutputChannel vc : this.chValue) {
            if (vc.getQualifiedName( ).equals(qname)) {
                ch = vc;
            }
        }
        if (ch == null) {
            ch = new ExiValueOutputChannel(qname);
            this.chValue.add(ch);
        }
        return ch;
    }

    // ------------------------------------------------------------------------

    /**
     * Returns this block's size. A block size is determined as the sum of the
     * values in each value channel.
     *
     * @return This block's size.
     */
    public int getSize( ) {
        int size = 0;
        for (ExiValueOutputChannel vc : this.chValue) {
            size += vc.getSize( );
        }
        return size;
    }

    // ------------------------------------------------------------------------

    /**
     * @param os
     * @param useDeflate
     * @throws IOException
     */
    public void writeChannels(ExiOutputStream os, boolean useDeflate) throws IOException {
        if (getSize( ) <= 100) {
            /*
             * if there are at most 100 values, then the block will contain only
             * stream containing the structure channel followed by all the value
             * channels
             */ 
            writeSingleChannel(os, useDeflate);
        } else {
            // write structure channel first and not combined with other channels
            writeCombinedChannel(getStructureChannel( ), os, useDeflate);

            // combine value channels with no more than 100 values
            ByteArrayOutputStream temp = new ByteArrayOutputStream( );
            boolean isCombined = false;
            for (ExiValueOutputChannel c : this.chValue) {
                if (c.getSize( ) <= 100) {
                    temp.write(c.toByteArray( ));
                    isCombined = true;
                }
            }
            if (isCombined) {
                writeCombinedChannel(temp, os, useDeflate);
            }

            // finally write uncombined channels consisting of more than 100 values
            for (ExiValueOutputChannel c : this.chValue) {
                if (c.getSize( ) > 100) {
                    writeCombinedChannel(c, os, useDeflate);
                }
            }
        }
    }

    // ------------------------------------------------------------------------

    /**
     * @param os
     * @param useDeflate
     * @throws IOException
     */
    private void writeSingleChannel(ExiOutputStream os, boolean useDeflate) throws IOException {

        // combine all the channels
        ByteArrayOutputStream temp = new ByteArrayOutputStream( );
        temp.write(getStructureChannel( ).toByteArray( ));
        for (ExiValueOutputChannel c : this.chValue) {
            temp.write(c.toByteArray( ));
        }

        writeCombinedChannel(temp, os, useDeflate);
    }

    // ------------------------------------------------------------------------

    /**
     * @param stream
     * @param os
     * @param useDeflate
     * @throws IOException
     */
    private void writeCombinedChannel(ByteArrayOutputStream stream, ExiOutputStream os, boolean useDeflate) throws IOException {
        if (useDeflate) {
            // write the channel with deflating beforehand
            ByteArrayOutputStream bout = new ByteArrayOutputStream( );
            DeflaterOutputStream dos = new DeflaterOutputStream(bout);
            stream.writeTo(dos);
            dos.finish( );
            dos.close( );
            os.write(bout.toByteArray( ));
        } else {
            // write the channel without deflating
            os.write(stream.toByteArray( ));
        }
    }
}
