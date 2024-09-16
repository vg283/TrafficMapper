package org.simple;


import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This is the main driver class.
 * Responsibilities:
 *      1. Uses the DefaultV2LogParser object to read from the log file.
 *      2. Uses the TrafficTypeToTagMapping object to generate the key using Traffic type ((destination port and protocol)) fields
 *      3. Gets the corresponding tag
 *      4. Populates the necessary data structures
 *          * Map with key as tag and value as the number of logs that were mapped to that tag
 *          * Map with key as Traffic type and value as the number of logs that were mapped to that
 *      5. Dumps the required info to a file in the current directory
 */

public class TrafficMapper {
    DefaultV2LogParser defaultV2LogParser;
    TrafficTypeToTagMapping trafficTypeToTagMapping;
    Map<String, Integer> tagCount;
    Map<String, Integer> trafficTypeCount;
    String outputFileName;

    public TrafficMapper(DefaultV2LogParser logParser,
                         TrafficTypeToTagMapping trafficTypeToTagMapping,
                         String outputFileName) {
        this.defaultV2LogParser = logParser;
        this.trafficTypeToTagMapping = trafficTypeToTagMapping;
        tagCount = new HashMap<>();
        trafficTypeCount = new HashMap<>();
        this.outputFileName = outputFileName;
    }

    /**
     * The main driver method of this class. Parses the input files and generates the output file
     */
    public void process() {
        while (defaultV2LogParser.hasNextLog()) {
            JSONObject nextLog = defaultV2LogParser.nextLog();
            String trafficTypeKey = trafficTypeToTagMapping.buildTrafficTypeKey(
                    nextLog.getString(DefaultV2LogParser.Fields.DESTINATION_PORT.name),
                    nextLog.getString(DefaultV2LogParser.Fields.PROTOCOL.name));
            String tag = trafficTypeToTagMapping.getTrafficTypeTagForKey(trafficTypeKey);
            tagCount.put(tag, tagCount.getOrDefault(tag, 0) + 1);
            trafficTypeCount.put(trafficTypeKey, trafficTypeCount.getOrDefault(tag, 0) + 1);
        }
        dumpOutputToFile();
    }

    /**
     * Method to dump the required data into a file
     * in the current directory
     */
    void dumpOutputToFile() {
        try (FileWriter writer = new FileWriter(this.outputFileName)) {
            writer.write("Tag Counts:\n");
            writer.write("Tag,  Count\n");
            for (Map.Entry<String, Integer> entry : tagCount.entrySet()) {
                writer.write(entry.getKey() + ", " + entry.getValue() + "\n");
            }
            writer.write("\nPort/Protocol Combination Counts:\n");
            writer.write("Port,Protocol,Count\n");
            for (Map.Entry<String, Integer> entry : trafficTypeCount.entrySet()) {
                String[] trafficType = entry.getKey().split(":");
                writer.write(trafficType[0] + "," + trafficType[1] + "," + entry.getValue() + "\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
