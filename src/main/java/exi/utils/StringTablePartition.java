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
package exi.utils;

import java.util.ArrayList;

/**
 * This clas represents a partition in a string table.
 *
 * @author Marco Wegner
 */
public abstract class StringTablePartition {

    // ------------------------------------------------------------------------
    // Member variables
    // ------------------------------------------------------------------------

    /**
     * The list where the strings are actually stored in.
     */
    private final ArrayList<String> list;

    // ------------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------------

    /**
     * Creates a new string table partition.
     */
    protected StringTablePartition( ) {
        this.list = new ArrayList<String>( );
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    /**
     * Look up a string in the string table.
     *
     * @param s The string to be looked up.
     * @return <code>true</code> if the string is contained in the string
     *         table, else <code>false</code>.
     */
    public boolean lookup(String s) {
        return this.list.contains(s);
    }

    // ------------------------------------------------------------------------

    /**
     * Returns the compact identifier of the specified string in this string
     * table.
     *
     * @param s The string to be looked up.
     * @return The string's compact identifier.
     */
    public int getID(String s) {
        return this.list.indexOf(s);
    }

    // ------------------------------------------------------------------------

    /**
     * Adds a string to the string table partition.
     *
     * @param s The string to be added.
     */
    public void add(String s) {
        this.list.add(s);
    }

    // ------------------------------------------------------------------------

    /**
     * Returns the number of strings currently in this string table partition.
     *
     * @return The number of strings in this partition.
     */
    public int getSize( ) {
        return this.list.size( );
    }

    // ------------------------------------------------------------------------

    /**
     * Returns the string value for the specified index from this string table
     * partition.
     *
     * @param index The index.
     * @return The string value.
     */
    public String getValue(int index) {
        return this.list.get(index);
    }
}
