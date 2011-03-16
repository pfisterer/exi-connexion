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

import javax.xml.namespace.QName;

import exi.events.ExiEventCode;
import exi.grammar.ExiGrammarGroup;

/**
 * Abstract EXI writer.
 * 
 * @author Marco Wegner
 */
public abstract class ExiWriter {

    /**
     * The output stream.
     */
    private final ExiOutputStream os;

    /**
     * Creates a new writer instance.
     */
    public ExiWriter( ) {
        this.os = new ExiOutputStream( );
    }

    /**
     * Wraps a new writer around an already used stream.
     * 
     * @param other The other stream.
     */
    public ExiWriter(ExiOutputStream other) {
        this.os = other;
    }

    /**
     * Returns this writer's output stream.
     *
     * @return The output stream.
     */
    public ExiOutputStream getOutputStream( ) {
        return this.os;
    }

    /**
     * Writes an event code to the stream.
     *
     * @param code The code to be written.
     * @param groupSize Instance which keeps information about the sizes of the
     *        different code parts.
     */
    public abstract void writeEventCode(ExiEventCode code, ExiGrammarGroup.Size groupSize);

    // ------------------------------------------------------------------------

    /**
     * Writes a string table hit for compact strings to the stream.
     * 
     * @param id The string ID in the table.
     * @param size The partition size.
     */
    public abstract void writeCompactStringHit(int id, int size);

    // ------------------------------------------------------------------------

    /**
     * Writes a string table miss for compact strings to the stream.
     * 
     * @param s The string.
     * @param size The current string table partition size.
     */
    public abstract void writeCompactStringMiss(String s, int size);

    // ------------------------------------------------------------------------

    /**Writes a string table hit for local names to the stream.
     * @param id The string ID in the table.
     * @param size The current string table partition size.
     */
    public abstract void writeLocalNameHit(int id, int size);

    // ------------------------------------------------------------------------

    /**
     * Writes a string table miss for local name strings to the stream.
     * 
     * @param s The string.
     */
    public abstract void writeLocalNameMiss(String s);

    // ------------------------------------------------------------------------

    /**
     * Writes a string table hit for a value string to the stream. The local
     * table ist used.
     * 
     * @param qname The qualified name.
     * @param id The string's ID in the table.
     * @param size The current partition size.
     */
    public abstract void writeValueHitLocal(QName qname, int id, int size);

    // ------------------------------------------------------------------------

    /**
     * Writes a string table hit for a value string to the stream. The global
     * table ist used.
     * 
     * @param qname The qualified name.
     * @param id The string's ID in the table.
     * @param size The current partition size.
     */
    public abstract void writeValueHitGlobal(QName qname, int id, int size);

    // ------------------------------------------------------------------------

    /**
     * Writes a string table miss for value strings to the stream.
     * 
     * @param qname The qualified name.
     * @param s The string.
     */
    public abstract void writeValueMiss(QName qname, String s);

    // ------------------------------------------------------------------------

    /**
     * Writes a comment to the stream.
     * 
     * @param comment The comment.
     */
    public abstract void writeComment(String comment);

    // ------------------------------------------------------------------------
    
    /**
     * Writes a processing instruction to the stream.
     * 
     * @param target The PI's target.
     * @param data The PI's data.
     */
    public abstract void writeProcessingInstruction(String target, String data);
    
    // ------------------------------------------------------------------------

    /**
     * Returns the current stream contents as a byte array.
     * 
     * @return The byte array.
     * @throws IOException If something goes wrong during I/O.
     */
    public byte[] toByteArray( ) throws IOException {
        this.os.flush( );
        return this.os.toByteArray( );
    }
}
