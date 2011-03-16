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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;

import exi.ExiOptions.Alignment;
import exi.ExiOptions.FidelityOption;

/**
 * The main EXI execution class.
 *
 * @author Marco Wegner
 */
public class ExiMain {

    // ------------------------------------------------------------------------
    // Member variables
    // ------------------------------------------------------------------------

    /**
     * The EXI logger.
     */
    private static Logger log = ExiLogger.getLogger(ExiMain.class);

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    /**
     * The main method.
     *
     * @param args The command-line arguments.
     * @throws Exception If something goes wrong during encoding or decoding.
     */
    public static void main(String[] args) throws Exception {

        Options cliopt = buildOptions( );
        CommandLine cl = getCommandLine(args, cliopt);

        if (cl.hasOption("help") || cl.hasOption('h')) {
            new HelpFormatter( ).printHelp("java ExiMain", cliopt);
            System.exit(-1);
        }

        ExiOptions exiopt = generateExiOptions(cl);


        for (String xmlFile : cl.getArgs( )) {

            byte[] byteArray = ExiDocument.encode(xmlFile, exiopt);

            log.debug("+ ----------------------------------------- +");
            log.debug(String.format("| Length of transmitted data [byte]: %6d |", byteArray.length));
            log.debug("+ ----------------------------------------- +");

            ExiDocument.decode(byteArray);
        }
    }

    // ------------------------------------------------------------------------

    /**
     * Builds the command line options.
     *
     * @return The newly created command line options object.
     */
    private static Options buildOptions( ) {
        Options options = new Options( );

        OptionBuilder.withDescription("Preserve EXI Comment (CM) events");
        options.addOption(OptionBuilder.create("preserve_comments"));

        OptionBuilder.withDescription("Preserve prefixes and EXI Namespace (NS) events");
        options.addOption(OptionBuilder.create("preserve_prefixes"));

        OptionBuilder.withDescription("Preserve EXI Processing Instruction (PI) events");
        options.addOption(OptionBuilder.create("preserve_pis"));

        OptionBuilder.withDescription("Preserve EXI Doctype (DT) events");
        options.addOption(OptionBuilder.create("preserve_doctype"));

        OptionBuilder.withDescription("Preserve lexical values");
        options.addOption(OptionBuilder.create("preserve_lexical"));

        OptionBuilder.withDescription("Byte-aligned output");
        options.addOption(OptionBuilder.create("byte_aligned"));

        OptionBuilder.withDescription("Alignment as with compression without actually compressing the output");
        options.addOption(OptionBuilder.create("precompress"));

        OptionBuilder.withDescription("Use compression on the output");
        options.addOption(OptionBuilder.create("compress"));

        OptionBuilder.withLongOpt("help");
        OptionBuilder.withDescription("Show this help screen");
        options.addOption(OptionBuilder.create('h'));

        return options;
    }

    // ------------------------------------------------------------------------

    /**
     * Parses the command line arguments and returns the command line instance.
     *
     * @param args The command line arguments.
     * @param options The command line options.
     * @return The command line instance.
     */
    private static CommandLine getCommandLine(String[] args, Options options) {
        CommandLineParser parser = new GnuParser( );
        CommandLine cl = null;

        try {
            cl = parser.parse(options, args);
        } catch (ParseException e) {
            String s = String.format("%s\n\njava ExiMain", e.getMessage( ));
            new HelpFormatter( ).printHelp(s, options);
            System.exit(-1);
        }

        return cl;
    }

    // ------------------------------------------------------------------------

    /**
     * Creates the EXI options instance from the provided command line options.
     *
     * @param cl The command line instance.
     * @return The newly created EXI options instance.
     */
    private static ExiOptions generateExiOptions(CommandLine cl) {
        ExiOptions options = new ExiOptions( );

        options.set(FidelityOption.PRESERVE_COMMENTS, cl.hasOption("preserve_comments"));
        options.set(FidelityOption.PRESERVE_DTDS, cl.hasOption("preserve_doctype"));
        options.set(FidelityOption.PRESERVE_LEXICAL_VALUES, cl.hasOption("preserve_lexical"));
        options.set(FidelityOption.PRESERVE_PREFIXES, cl.hasOption("preserve_prefixes"));
        options.set(FidelityOption.PRESERVE_PROCESSING_INSTRUCTIONS, cl.hasOption("preserve_pis"));

        if (cl.hasOption("byte_aligned")) {
            options.setAlign(Alignment.BYTE_ALIGNED);
        } else if (cl.hasOption("precompress")) {
            options.setAlign(Alignment.PRE_COMPRESSED);
        }

        options.setCompression(cl.hasOption("compress"));

        return options;
    }
}
