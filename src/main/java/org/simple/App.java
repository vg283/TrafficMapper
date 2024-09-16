package org.simple;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * This is the main application class. Has the following responsibilities
 *      1. Ensures command line args are correct
 *      2. Instantiates the DefaultV2LogParser and TrafficTypeToTagMapping by
 *         providing the appropriate file names
 *      3. Instantiates the main driver class
 */

public class App {
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Incorrect number of arguments, 3 expected");
            System.out.println("1. Log filename with path");
            System.out.println("2. Lookup table name with path");
            System.out.println("3. Name of the output file");
            System.exit(1);
        }
        final String logFileName = args[0];
        final String lookupTableName = args[1];
        final String outputFileName = args[2];

        DefaultV2LogParser defaultV2LogParser = null;
        TrafficTypeToTagMapping trafficTypeToTagMapping = null;
        try {
            defaultV2LogParser = new DefaultV2LogParser(logFileName);
            trafficTypeToTagMapping = new TrafficTypeToTagMapping(lookupTableName);
            TrafficMapper trafficMapper = new TrafficMapper(defaultV2LogParser,
                    trafficTypeToTagMapping, outputFileName);
            trafficMapper.process();
        } catch (IOException e) {
            System.out.println("Exiting, Exception reading/processing file: " + e.getMessage());
        }
        if (defaultV2LogParser != null) {
            defaultV2LogParser.closeFile();
        }
    }
}