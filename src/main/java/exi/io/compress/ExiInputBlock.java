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

import java.util.LinkedList;

import javax.xml.namespace.QName;

/**
 * Represents a block extracted from a compressed input stream.
 * 
 * @author Marco Wegner
 */
public class ExiInputBlock {

    /**
     * The structure channel.
     */
    private ExiStructureInputChannel chStruct;
    
    /**
     * The list of value channels.
     */
    private final LinkedList<ExiValueInputChannel> chValue = new LinkedList<ExiValueInputChannel>( );
    
    /**
     * 
     */
    public ExiInputBlock( ) {
        //
    }
    
    /**
     * Sets this block's structure channel.
     * 
     * @param sc The structure channel.
     */
    public void setStructureChannel(ExiStructureInputChannel sc) {
        sc.reset( );
        this.chStruct = sc;
    }
    
    /**
     * Returns this block's structure channel.
     * 
     * @return The structure channel.
     */
    public ExiStructureInputChannel getStructureChannel( ) {
        return chStruct;
    }
    
    /**
     * Adds a new value channel to the block
     * 
     * @param vc The value channel
     */
    public void addValueChannel(ExiValueInputChannel vc) {
        vc.reset( );
        chValue.add(vc);
    }
    
    /**
     * Returns the value channel associated to the QName.
     * 
     * @param qname The QName.
     * @return The value channel.
     */
    public ExiValueInputChannel getValueChannel(QName qname) {
        ExiValueInputChannel ch = null;
        for (ExiValueInputChannel vc : chValue) {
            if (vc.getQualifiedName( ).equals(qname)) {
                ch = vc;
            }
        }
        return ch;
    }

    // ------------------------------------------------------------------------
    
    /**
     * Returns the number of values channels in this block.
     * 
     * @return The number of values channels in this block.
     */
    public int getNumberOfValueChannels( ) {
        return this.chValue.size( );
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
        for (ExiValueInputChannel vc : this.chValue) {
            size += vc.getSize( );
        }
        return size;
    }
}
