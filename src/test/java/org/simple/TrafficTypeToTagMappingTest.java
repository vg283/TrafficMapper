package org.simple;

import junit.framework.TestCase;
import org.junit.Test;

import java.io.IOException;
import java.util.Objects;

public class TrafficTypeToTagMappingTest extends TestCase {

    @Test
    public void testValidSingleTrafficTypeMapping() throws IOException {
        String validSingleTrafficTypeFile = "./src/test/resources/validSingleTrafficTypeTagMap.csv";
        TrafficTypeToTagMapping trafficTypeToTagMapping = new TrafficTypeToTagMapping(validSingleTrafficTypeFile);
        String testKey = "25:tcp";
        assert Objects.equals(trafficTypeToTagMapping.getTrafficTypeTagForKey(testKey), "sv_P1");
    }

    @Test
    public void testTrafficTypeMappingWithUnsupportedKey() throws IOException {
        String validSingleTrafficTypeFile = "./src/test/resources/validSingleTrafficTypeTagMap.csv";
        TrafficTypeToTagMapping trafficTypeToTagMapping = new TrafficTypeToTagMapping(validSingleTrafficTypeFile);
        String testKey = "999:InvalidProto";
        assert Objects.equals(trafficTypeToTagMapping.getTrafficTypeTagForKey(testKey), "untagged");
    }
}