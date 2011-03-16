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
package exi;

import java.util.Stack;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import exi.events.ExiAttribute;
import exi.events.ExiCharacters;
import exi.events.ExiComment;
import exi.events.ExiEndDocument;
import exi.events.ExiEndElement;
import exi.events.ExiNamespaceDeclaration;
import exi.events.ExiStartDocument;
import exi.events.ExiStartElement;

/**
 * This class generates a DOM document from a series of EXI events.
 *
 * @author Marco Wegner
 */
public class ExiDocumentBuilder {

    // ------------------------------------------------------------------------
    // Member variables
    // ------------------------------------------------------------------------

    /**
     * The DOM document.
     */
    private Document document;

    // ------------------------------------------------------------------------

    /**
     * The element stack. It is mainly used for querying the currently active
     * element. This element can then get attributes or character data if
     * necessary.
     */
    private Stack<Element> elementStack = new Stack<Element>( );

    // ------------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------------

    /**
     * Creates a new EXI DOM document builder.
     *
     * @throws Exception If a DOM document builder cannot be built.
     */
    public ExiDocumentBuilder( ) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance( );
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder( );
        this.document = builder.newDocument( );
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    /**
     * Returns the DOM document in its current state.
     *
     * @return The DOM document.
     */
    public Document getDocument( ) {
        return this.document;
    }

    // ------------------------------------------------------------------------

    /**
     * Handles an EXI StartDocument (SD) event.
     *
     * @param event The StartDocument (SD) event.
     */
    public void handle(ExiStartDocument event) {
        // nothing to do for now...
    }

    // ------------------------------------------------------------------------

    /**
     * Generates a new element and puts it on the stack. If another element
     * already exists on the top of the stack then the new element is added as
     * its child.
     *
     * @param event The EXI StartElement (SE) event.
     */
    public void handle(ExiStartElement event) {
        QName qname = event.getQualifiedName( );
        Element e = this.document.createElementNS(qname.getNamespaceURI( ),
                qname.getLocalPart( ));
        if (this.elementStack.size( ) > 0) {
            Element parent = getCurrentElement( );
            e.setPrefix(parent.getPrefix( ));
            parent.appendChild(e);
        } else {
            this.document.appendChild(e);
        }
        this.elementStack.push(e);
    }

    // ------------------------------------------------------------------------

    /**
     * Adds an attribute to the top-most element on the element stack.
     *
     * @param event The EXI Attribute (AT) event.
     */
    public void handle(ExiAttribute event) {
        QName qname = event.getQualifiedName( );
        getCurrentElement( ).setAttributeNS(qname.getNamespaceURI( ),
                qname.toString( ), event.getValue( ));
    }

    // ------------------------------------------------------------------------

    /**
     * Adds a namespace attribute to the top-most element on the stack.
     *
     * @param event The EXI Namespace (NS) event.
     */
    public void handle(ExiNamespaceDeclaration event) {
        getCurrentElement( ).setPrefix(event.getNamespacePrefix( ));
    }

    // ------------------------------------------------------------------------

    /**
     * Adds a text node to the top-most element on the element stack.
     *
     * @param event The Characters (CH) event.
     */
    public void handle(ExiCharacters event) {
        getCurrentElement( ).appendChild(
                this.document.createTextNode(event.getData( )));
    }

    // ------------------------------------------------------------------------

    /**
     * Handles an EXI EndElement (EE) event. This mainly pops the current element
     * from the element stack.
     *
     * @param event The EndElement (EE) event.
     */
    public void handle(ExiEndElement event) {
        this.elementStack.pop( );
    }

    // ------------------------------------------------------------------------

    /**
     * Handles an EXI EndDocument (ED) event.
     *
     * @param event The EndDocument (ED) event.
     */
    public void handle(ExiEndDocument event) {
        // nothing to do for now...
    }

    // ------------------------------------------------------------------------

    /**
     * Handles an EXI Comment (CM) event.
     *
     * @param event The EXI Comment (CM) event.
     */
    public void handle(ExiComment event) {
        Comment comment = this.document.createComment(event.getText( ));
        if (this.elementStack.size( ) > 0) {
            getCurrentElement( ).appendChild(comment);
        } else {
            this.document.appendChild(comment);
        }

    }

    // ------------------------------------------------------------------------

    /**
     * Returns the top-most element from the element stack.
     *
     * @return The top-most element from the element stack.
     */
    private Element getCurrentElement( ) {
        return this.elementStack.peek( );
    }
}
