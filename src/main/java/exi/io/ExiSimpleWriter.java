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
package exi.io;

import java.math.BigInteger;

import javax.xml.namespace.QName;

import exi.events.ExiEventCode;
import exi.grammar.ExiGrammarGroup;

/**
 * EXI writer for simple streams.
 * 
 * @author Marco Wegner
 */
public class ExiSimpleWriter extends ExiWriter {

    /**
     *
     */
    public ExiSimpleWriter( ) {
        super( );
    }

    /**
     * @param other
     */
    public ExiSimpleWriter(ExiOutputStream other) {
        super(other);
    }

    // ------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see exi.io.ExiStreamWriter#writeEventCode(exi.events.ExiEventCode, exi.grammar.ExiGrammarGroup.Size)
     */
    @Override
    public void writeEventCode(ExiEventCode eventCode, ExiGrammarGroup.Size groupSize) {
        for (int i = 0; i < eventCode.getLength( ); i++) {
            int value = eventCode.getPart(i);
            int bits = (int)Math.ceil(Math.log(groupSize.getPartSize(i))/Math.log(2));
            getOutputStream( ).writeNBitUnsignedInteger(BigInteger.valueOf(value), bits);
        }
    }

    // ------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see exi.io.ExiStreamWriter#writeCompactStringHit(int, int)
     */
    @Override
    public void writeCompactStringHit(int id, int size) {
        int bits = (int)Math.ceil(Math.log(size+1)/Math.log(2));
        getOutputStream( ).writeNBitUnsignedInteger(BigInteger.valueOf(id + 1), bits);
    }

    // ------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see exi.io.ExiStreamWriter#writeCompactStringMiss(java.lang.String, int)
     */
    @Override
    public void writeCompactStringMiss(String s, int size) {
        ExiOutputStream os = getOutputStream( );
        os.writeNBitUnsignedInteger(BigInteger.ZERO, (int)Math.ceil(Math.log(size+1)/Math.log(2)));
        os.writeString(s);
    }

    // ------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see exi.io.ExiStreamWriter#writeLocalNameHit(int, int)
     */
    @Override
    public void writeLocalNameHit(int id, int size) {
        ExiOutputStream os = getOutputStream( );
        os.writeUnsignedInteger(BigInteger.ZERO);
        int bits = (int)Math.ceil(Math.log(size)/Math.log(2));
        os.writeNBitUnsignedInteger(BigInteger.valueOf(id), bits);
    }

    // ------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see exi.io.ExiStreamWriter#writeLocalNameMiss(java.lang.String)
     */
    @Override
    public void writeLocalNameMiss(String s) {
        getOutputStream( ).writeString(s, 1);
    }

    // ------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see exi.io.ExiStreamWriter#writeValueHitLocal(int, int)
     */
    @Override
    public void writeValueHitLocal(QName qname, int id, int size) {
        writeValueHit(id, size, 0);
    }

    // ------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see exi.io.ExiStreamWriter#writeValueHitGlobal(int, int)
     */
    @Override
    public void writeValueHitGlobal(QName qname, int id, int size) {
        writeValueHit(id, size, 1);
    }

    // ------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see exi.io.ExiStreamWriter#writeValueMiss(java.lang.String)
     */
    @Override
    public void writeValueMiss(QName qname, String s) {
        getOutputStream( ).writeString(s, 2);
    }

    // ------------------------------------------------------------------------

    @Override
    public void writeComment(String comment) {
        getOutputStream( ).writeString(comment);
    }

    // ------------------------------------------------------------------------

    @Override
    public void writeProcessingInstruction(String target, String data) {
        ExiOutputStream os = getOutputStream( );
        os.writeString(target);
        os.writeString(data);
    }

    /**
     * @param id
     * @param size
     * @param flag
     */
    private void writeValueHit(int id, int size, int flag) {
        ExiOutputStream os = getOutputStream( );
        os.writeUnsignedInteger(BigInteger.valueOf(flag));
        int bits = (int)Math.ceil(Math.log(size)/Math.log(2));
        os.writeNBitUnsignedInteger(BigInteger.valueOf(id), bits);
    }
}
