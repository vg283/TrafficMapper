package org.simple;

import junit.framework.TestCase;
import org.json.JSONObject;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.Objects;

public class DefaultV2LogParserTest extends TestCase {

    @Test
    public void testDefaultV2LogParserUnsupportedFields() throws FileNotFoundException {
        String logFileUnsupportedField = "./src/test/resources/invalidDefaultV2LogUnsupportedField.txt";
        DefaultV2LogParser defaultV2LogParser = new DefaultV2LogParser(logFileUnsupportedField);
        assert defaultV2LogParser.hasNextLog();
        JSONObject log = defaultV2LogParser.nextLog();
        assert log == null;
    }

    @Test
    public void testValidSingleFlowLogDefaultV2() throws FileNotFoundException {
        String validSingleFlowLogDefaultV2 = "./src/test/resources/validSingleFlowLogDefaultV2.txt";
        DefaultV2LogParser defaultV2LogParser = new DefaultV2LogParser(validSingleFlowLogDefaultV2);
        assert defaultV2LogParser.hasNextLog();
        JSONObject log = defaultV2LogParser.nextLog();
        assert Objects.equals(log.getString(DefaultV2LogParser.Fields.PROTOCOL.name), IANAProtocolMap.protoNumToNameTable.get(6));
        assert Objects.equals(log.getString(DefaultV2LogParser.Fields.DESTINATION_PORT.name), "49156");
    }
}