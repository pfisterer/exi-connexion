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

import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * This is the logger class for the Connexion project.
 *
 * @author Marco Wegner
 */
public final class ExiLogger {

    // ------------------------------------------------------------------------
    // Static stuff
    // ------------------------------------------------------------------------

    /**
     * The location of the logging properties files within this project
     */
    private static final String LOGGING_PROPERTIES = "exi/connexion.log4j.properties";

    // ------------------------------------------------------------------------

    static {
        // BasicConfigurator.configure( );
        setProperties(LOGGING_PROPERTIES);
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    /**
     * Sets the logging properties for this logger.
     *
     * @param filename The logging properties' filename.
     */
    public static void setProperties(String filename) {
        setProperties(ExiLogger.class.getClassLoader( ).getResource(filename));
    }

    // ------------------------------------------------------------------------

    /**
     * Sets the logging properties for this logger.
     *
     * @param url The logging properties' URL.
     */
    public static void setProperties(URL url) {
        if (url != null) {
            PropertyConfigurator.configure(url);
        } else {
            System.err.println("WARNING: property file not found");
        }
    }

    // ------------------------------------------------------------------------

    /**
     * Sets the logging properties for this logger.
     *
     * @param props The logging properties.
     */
    public static void setProperties(Properties props) {
        PropertyConfigurator.configure(props);
    }

    // ------------------------------------------------------------------------

    /**
     * Get a logger instance for the specified string id.
     *
     * @param s Ths string.
     * @return The logger instance.
     */
    public static Logger getLogger(String s) {
        return Logger.getLogger(s);
    }

    // ------------------------------------------------------------------------

    /**
     * Get a logger instance for the specified object.
     *
     * @param o The object.
     * @return The logger instance.
     */
    public static Logger getLogger(Object o) {
        return getLogger(o.getClass( ));
    }

    // ------------------------------------------------------------------------

    /**
     * Get a logger instance for the specified class.
     *
     * @param c The class.
     * @return The logger instance.
     */
    public static Logger getLogger(Class<? extends Object> c) {
        return Logger.getLogger(c);
    }

    // ------------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------------

    /**
     * Instantiation not permitted.
     */
    private ExiLogger( ) {
        // nothing to do
    }
}
