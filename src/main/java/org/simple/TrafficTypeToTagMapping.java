package org.simple;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class reads from a lookup table file and maintains a map.
 * Key for the map is a combination of destination port and protocol name
 * Key format: <destination-port>:<protocol-name>
 * Value is the tag
 */
public class TrafficTypeToTagMapping {
    private Map<String, String> trafficTypeToTagMap;
    private final String UNTAGGED = "untagged";

    /**
     * Constructor which reads the full lookup table and populates
     * a Map, which is an internal representation of the info in the file
     * @param lookupTableName name of the file which has the traffic type to tag mapping
     */
    public TrafficTypeToTagMapping(String lookupTableName) throws IOException {
        Path lookupTableFilePath = Paths.get(lookupTableName);
        trafficTypeToTagMap = new HashMap<>();
        try {
            List<String> allLines = Files.readAllLines(lookupTableFilePath);
            /* TODO validate the column names with fields */
            for (int i = 1; i < allLines.size(); i++) {
                String line = allLines.get(i);
                String[] split = line.split(",");
                String key = buildTrafficTypeKey(split[0], split[1]);
                trafficTypeToTagMap.put(key, split[2]);
            }
        } catch (IOException e) {
            System.out.println("Exception while reading lookup table file");
            throw e;
        }
    }

    /**
     * Builds key for this map with ":" as delimiter
     * @param destinationPort is the destination port from flow log
     * @param protocol is the protocol name from the flow log
     * @return key in the format: <destination-port>:<protocol-name>
     */
    public String buildTrafficTypeKey(String destinationPort, String protocol) {
        return destinationPort + ":" + protocol;
    }

    /**
     * Given a key, this method returns the value, which is the tag
     * info for the log
     * @param key in the format: <destination-port>:<protocol-name>
     * @return tag value read from the lookup table file
     */
    public String getTrafficTypeTagForKey(String key) {
        if (trafficTypeToTagMap.containsKey(key)) {
            return trafficTypeToTagMap.get(key);
        }
        return UNTAGGED;
    }
}
