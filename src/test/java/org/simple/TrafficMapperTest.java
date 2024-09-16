package org.simple;


import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TrafficMapperTest {
    @Test
    public void testEndToEndHappyPath() throws IOException {
        final String happyPathFile = "./src/test/resources/happyPathOutput.txt";
        final String referenceHappyPathFile = "./src/test/resources/referenceHappyPathOutput.txt";
        DefaultV2LogParser defaultV2LogParser = new DefaultV2LogParser("./src/test/resources/testFlowLogFile.txt");
        TrafficTypeToTagMapping trafficTypeToTagMapping = new TrafficTypeToTagMapping("./src/test/resources/testLookupTable.csv");

        TrafficMapper trafficMapper = new TrafficMapper(defaultV2LogParser, trafficTypeToTagMapping, happyPathFile);
        trafficMapper.process();

        Path testOutputPath = Paths.get(happyPathFile);
        assert testOutputPath.toFile().exists();

        Path referencePath = Paths.get(referenceHappyPathFile);
        assert Files.mismatch(testOutputPath, referencePath) == -1;
    }
}