package org.simple;

import java.util.HashMap;
import java.util.Map;

public class IANAProtocolMap {
    public static final Map<Integer, String> protoNumToNameTable = new HashMap<>();
    static {
        protoNumToNameTable.put(1, "icmp");
        protoNumToNameTable.put(6, "tcp");
        protoNumToNameTable.put(17, "udp");
    }
}
