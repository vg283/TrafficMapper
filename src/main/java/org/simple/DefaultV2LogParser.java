package org.simple;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * This is the parser class for default format, version 2 flow logs
 * Only this class has the knowledge of log file format so the users of this class
 * need not know the details of log format
 * Responsibilities
 *      1. Reads flow logs line by line.
 *      2. Check if there are still unread logs
 *      3. Return the read log
 */
public class DefaultV2LogParser {

    /**
     * Enum that is tightly coupled with the Default format, version 2 flow logs
     * The names and ordering of enum variables should match with the specs
     * Specs: https://docs.aws.amazon.com/vpc/latest/userguide/flow-log-records.html
     */
    public enum Fields {
        VERSION("version"),
        ACCOUNT_ID("account-id"),
        INTERFACE_ID("interface-id"),
        SOURCE_ADDRESS("srcaddr"),
        DESTINATION_ADDRESS("dstaddr"),
        SOURCE_PORT("srcport"),
        DESTINATION_PORT("dstport"),
        PROTOCOL("protocol"),
        PACKETS("packets"),
        BYTES("bytes"),
        START("start"),
        END("end"),
        ACTION("action"),
        LOG_STATUS("log-status"),
        NUM_OF_FIELDS("num-of-fields"),;

        public String name;

        Fields(String name) {
            this.name = name;
        }
    }

    private FileReader fileReader;
    private BufferedReader logReader;
    private String currentLog;
    
    public DefaultV2LogParser(String logFileName) throws FileNotFoundException {
        try {
            fileReader = new FileReader(logFileName);
            logReader = new BufferedReader(fileReader);
        } catch (FileNotFoundException e) {
            System.out.println("Exception while reading log file");
            closeFile();
            throw e;
        }
    }

    /**
     * Method to check if end of file is reached
     * @return 
     *      True if there are still unread logs
     *      False if no more logs to be read
     */
    public boolean hasNextLog() {
        try {
            currentLog = logReader.readLine();
            return currentLog != null;
        } catch (IOException e) {
            closeFile();
            throw new RuntimeException("Exception while reading next log:" + e.getMessage());
        }
    }

    /**
     * Method to return the actual log
     * It also converts the protocol number to name using IANA convention
     * @return
     *      Returns a json with 2 fields - destination port and protocol
     */
    public JSONObject nextLog() {
        String[] tokens = currentLog.split(" ");
        //validate the number of fields
        if (tokens.length != Fields.NUM_OF_FIELDS.ordinal()) {
            System.out.println("Invalid log, ignoring");
            return null;
        }

        JSONObject flowLog = new JSONObject();
        flowLog.put(Fields.DESTINATION_PORT.name, tokens[Fields.DESTINATION_PORT.ordinal()]);
        int protoNumber = Integer.parseInt(tokens[Fields.PROTOCOL.ordinal()]);
        flowLog.put(Fields.PROTOCOL.name, IANAProtocolMap.protoNumToNameTable.get(protoNumber));
        return flowLog;
    }

    public void closeFile() {
        System.out.println("Closing log file");
        try {
            if (fileReader != null) {
                fileReader.close();
            }
            if (logReader != null) {
                logReader.close();
            }
        } catch (IOException e) {
            System.out.println("Exception while closing log file");
        }
    }
}
