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

import java.io.IOException;
import java.math.BigInteger;
import java.util.LinkedList;

import javax.xml.namespace.QName;

import exi.ExiConstants;
import exi.ExiOptions;
import exi.events.ExiEventCode;
import exi.grammar.ExiGrammarGroup.Size;
import exi.io.compress.ExiOutputBlock;
import exi.io.compress.ExiStructureOutputChannel;

/**
 * EXI writer for compressed streams.
 * 
 * @author Marco Wegner
 */
public class ExiCompressedWriter extends ExiWriter {

    // ------------------------------------------------------------------------
    // Member variables
    // ------------------------------------------------------------------------

    /**
     * The list of EXI blocks.
     */
    private LinkedList<ExiOutputBlock> blocks = new LinkedList<ExiOutputBlock>( );

    // ------------------------------------------------------------------------

    /**
     *
     */
    private boolean useDeflate;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /**
     * @param options TODO
     */
    public ExiCompressedWriter(ExiOptions options) {
        super( );
        initialize(options);
    }

    /**
     * @param other
     * @param options TODO
     */
    public ExiCompressedWriter(ExiOutputStream other, ExiOptions options) {
        super(other);
        initialize(options);
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    /**
     * @param options
     */
    private void initialize(ExiOptions options) {
        this.useDeflate = options.useCompression( );
    }

    // ------------------------------------------------------------------------

    /**
     * Returns the currently used block.
     *
     * @return The currently used block.
     */
    private ExiOutputBlock getCurrentBlock( ) {
        ExiOutputBlock current;
        if (this.blocks.isEmpty( )) {
            current = makeNewBlock( );
        } else {
            current = this.blocks.getLast( );
            // TODO: use actual block size
            if (current.getSize( ) > ExiConstants.BLOCK_SIZE_DEFAULT) {
                current = makeNewBlock( );
            }
        }
        return current;
    }

    // ------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see exi.io.ExiWriter#writeEventCode(exi.events.ExiEventCode, exi.grammar.ExiGrammarGroup.Size)
     */
    @Override
    public void writeEventCode(ExiEventCode eventCode, Size groupSize) {
        ExiOutputStream os = getCurrentBlock( ).getStructureChannel( );
        for (int i = 0; i < eventCode.getLength( ); i++) {
            int value = eventCode.getPart(i);
            int bits = (int)Math.ceil(Math.log(groupSize.getPartSize(i))/Math.log(2));
            os.writeNBitUnsignedInteger(BigInteger.valueOf(value), bits);
        }
    }

    // ------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see exi.io.ExiWriter#writeCompactStringHit(int, int)
     */
    @Override
    public void writeCompactStringHit(int id, int size) {
        int bits = (int)Math.ceil(Math.log(size+1)/Math.log(2));
        getCurrentBlock( ).getStructureChannel( ).writeNBitUnsignedInteger(BigInteger.valueOf(id + 1), bits);
    }

    // ------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see exi.io.ExiWriter#writeCompactStringMiss(java.lang.String, int)
     */
    @Override
    public void writeCompactStringMiss(String s, int size) {
        ExiOutputStream os = getCurrentBlock( ).getStructureChannel( );
        os.writeNBitUnsignedInteger(BigInteger.ZERO, (int)Math.ceil(Math.log(size+1)/Math.log(2)));
        os.writeString(s);
    }

    // ------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see exi.io.ExiWriter#writeLocalNameHit(int, int)
     */
    @Override
    public void writeLocalNameHit(int id, int size) {
        ExiOutputStream os = getCurrentBlock( ).getStructureChannel( );
        os.writeUnsignedInteger(BigInteger.ZERO);
        int bits = (int)Math.ceil(Math.log(size)/Math.log(2));
        os.writeNBitUnsignedInteger(BigInteger.valueOf(id), bits);
    }

    // ------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see exi.io.ExiWriter#writeLocalNameMiss(java.lang.String)
     */
    @Override
    public void writeLocalNameMiss(String s) {
        getCurrentBlock( ).getStructureChannel( ).writeString(s, 1);
    }

    // ------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see exi.io.ExiWriter#writeValueHitLocal(int, int)
     */
    @Override
    public void writeValueHitLocal(QName qname, int id, int size) {
        getCurrentBlock( ).getValueChannel(qname).addValueHitLocal(id, size);
    }

    // ------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see exi.io.ExiWriter#writeValueHitGlobal(int, int)
     */
    @Override
    public void writeValueHitGlobal(QName qname, int id, int size) {
        getCurrentBlock( ).getValueChannel(qname).addValueHitGlobal(id, size);
    }

    // ------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see exi.io.ExiWriter#writeValueMiss(javax.xml.namespace.QName, java.lang.String)
     */
    @Override
    public void writeValueMiss(QName qname, String s) {
        getCurrentBlock( ).getValueChannel(qname).addValueMiss(s);
    }

    // ------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see exi.io.ExiWriter#writeComment(java.lang.String)
     */
    @Override
    public void writeComment(String comment) {
        getCurrentBlock( ).getStructureChannel( ).writeString(comment);
    }

    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    
    @Override
    public void writeProcessingInstruction(String target, String data) {
        ExiStructureOutputChannel esc = getCurrentBlock( ).getStructureChannel( );
        esc.writeString(target);
        esc.writeString(data);
    }

    /* (non-Javadoc)
     * @see exi.io.ExiWriter#toByteArray()
     */
    @Override
    public byte[] toByteArray( ) throws IOException {
        for (ExiOutputBlock b : this.blocks) {
            b.writeChannels(getOutputStream( ), this.useDeflate);
        }
        return super.toByteArray( );
    }

    // ------------------------------------------------------------------------

    /**
     * Creates and adds a new block.
     *
     * @return The newly created block.
     */
    private ExiOutputBlock makeNewBlock( ) {
        ExiOutputBlock current = new ExiOutputBlock( );
        this.blocks.add(current);
        return current;
    }
}
