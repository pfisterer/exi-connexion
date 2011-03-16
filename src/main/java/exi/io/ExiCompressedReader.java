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

import java.util.LinkedList;

import javax.xml.namespace.QName;

import exi.ExiOptions;
import exi.io.compress.ExiBlockSplitter;
import exi.io.compress.ExiInputBlock;

/**
 * EXI reader for compressed streams.
 * 
 * @author Marco Wegner
 */
public class ExiCompressedReader extends ExiReader {
    
    /**
     * The list of blocks.
     */
    private LinkedList<ExiInputBlock> blocks = new LinkedList<ExiInputBlock>( );

    // ------------------------------------------------------------------------
    
    /**
     * The index of the currently used block.
     */
    private int currentBlockIndex;

    // ------------------------------------------------------------------------
    
    /**
     * The number of values read up to this point.
     */
    private int valuesRead;

    // ------------------------------------------------------------------------

    /**
     * Creates a new EXI reader for compressed streams.
     * 
     * @param array The byte array of data.
     * @param options The EXI options.
     * @throws Exception If something goes wrong during reading the stream.
     */
    public ExiCompressedReader(byte[] array, ExiOptions options) throws Exception {
        super(array);
        
        initialize(options);
    }

    // ------------------------------------------------------------------------

    /**Constructs a reader wrapped around an already used stream instance.
     * @param other The stream already in use.
     * @param options The EXI options.
     * @throws Exception If something goes wrong during reading the stream.
     */
    public ExiCompressedReader(ExiInputStream other, ExiOptions options) throws Exception {
        super(other);
        
        initialize(options);
    }

    // ------------------------------------------------------------------------

    /**
     * Initializes the reader using the EXI options.
     * 
     * @param options The EXI options.
     * @throws Exception If something goes wrong during reading the stream.
     */
    private void initialize(ExiOptions options) throws Exception {
        ExiBlockSplitter splitter = new ExiBlockSplitter(getInputStream( ), options);
        this.blocks = splitter.getBlocks( );
        this.currentBlockIndex = 0;
        this.valuesRead = 0;
    }

    // ------------------------------------------------------------------------
    
    /**
     * Returns the block currently in use for reading.
     * 
     * @return The current block
     */
    private ExiInputBlock getCurrentBlock( ) {
        return this.blocks.get(this.currentBlockIndex);
    }

    // ------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see exi.io.ExiReader#readComment()
     */
    @Override
    public String readComment( ) {
        return getCurrentBlock( ).getStructureChannel( ).readString( );
    }

    // ------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see exi.io.ExiReader#readCompactString()
     */
    @Override
    public String readCompactString( ) {
        return getCurrentBlock( ).getStructureChannel( ).readString( );
    }

    // ------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see exi.io.ExiReader#readCompactStringCode(int)
     */
    @Override
    public int readCompactStringCode(int size) {
        int bits = (int)Math.ceil(Math.log(size+1)/Math.log(2));
        return getCurrentBlock( ).getStructureChannel( ).readNBitUnsignedInteger(bits).intValue( );
    }

    // ------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see exi.io.ExiReader#readEventCodePart(int)
     */
    @Override
    public int readEventCodePart(int partSize) {
        int bits = (int)Math.ceil(Math.log(partSize)/Math.log(2));
        return getCurrentBlock( ).getStructureChannel( ).readNBitUnsignedInteger(bits).intValue( );
    }

    // ------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see exi.io.ExiReader#readLocalName(int)
     */
    @Override
    public String readLocalName(int code) {
        return getCurrentBlock( ).getStructureChannel( ).readString(code - 1);
    }

    // ------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see exi.io.ExiReader#readLocalNameCode()
     */
    @Override
    public int readLocalNameCode( ) {
        return getCurrentBlock( ).getStructureChannel( ).readUnsignedInteger( ).intValue( );
    }

    // ------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see exi.io.ExiReader#readLocalNameIndex(int)
     */
    @Override
    public int readLocalNameIndex(int size) {
        int bits = (int)Math.ceil(Math.log(size)/Math.log(2));
        return getCurrentBlock( ).getStructureChannel( ).readNBitUnsignedInteger(bits).intValue( );
    }

    // ------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see exi.io.ExiReader#readValueString(int)
     */
    @Override
    public String readValueString(QName qname, int code) {
        String s = getCurrentBlock( ).getValueChannel(qname).readString(code - 2);
        this.valuesRead++;
        return s;
    }

    // ------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see exi.io.ExiReader#readValueStringCode()
     */
    @Override
    public int readValueStringCode(QName qname) {
        checkEndOfBlock( );
        return getCurrentBlock( ).getValueChannel(qname).readUnsignedInteger( ).intValue( );
   }

    // ------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see exi.io.ExiReader#readValueStringIndex(int)
     */
    @Override
    public int readValueStringIndex(QName qname, int size) {
        int bits = (int)Math.ceil(Math.log(size)/Math.log(2));
        int index = getCurrentBlock( ).getValueChannel(qname).readNBitUnsignedInteger(bits).intValue( );
        this.valuesRead++;
        return index;
    }

    /**
     * 
     */
    private void checkEndOfBlock( ) {
        if (valuesRead == this.blocks.get(this.currentBlockIndex).getSize( )) {
            this.valuesRead = 0;
            this.currentBlockIndex++;
        }
    }
}
