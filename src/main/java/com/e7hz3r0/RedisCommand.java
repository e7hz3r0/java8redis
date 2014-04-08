package com.e7hz3r0;

import com.e7hz3r0.RedisCommand.RedisCommandEnum;

/**
 * Encapsulates a command sent to the Redis server. 
 * The string representation of this object is its RESP representation.
 * 
 * Currently only supports SET and GET.
 * 
 * @author Ethan Urie
 *
 */
public class RedisCommand {
    public enum RedisCommandEnum {
        SET("SET"),
        GET("GET"), 
        DEL("DEL");
        
        private final String cmd;
        private RedisCommandEnum(String cmd) {
            this.cmd = cmd;
        }
        
        @Override
        public String toString() {
            return cmd;
        }
    }

    private final String key;
    private final String value;
    private final RedisCommandEnum command;
    
    public final String ARRAY_CHAR = "*";
    public final String BULK_STRING_CHAR = "$";
    public final String DELIMITER = "\r\n";
    

    /**
     * Constructor.
     * @param cmd The command (SET, GET) to send to Redis
     * @param key The key for the command
     */
    public RedisCommand(RedisCommandEnum cmd, String key) {
        this(cmd, key, null);
    }

    /**
     * Constructor.
     * @param cmd The command (SET, GET) to send to Redis
     * @param key The key for the command
     * @param value The value to set for the associated key
     */
    public RedisCommand(RedisCommandEnum cmd, String key, String value) {
        this.command = cmd;
        this.key = key;
        this.value = value;
    }

    /**
     * Simple accessor
     * @return The value associated with the key
     */
    public String getValue() {
        return value;
    }

    /**
     * Simple accessor
     * @return The key for this command
     */
    public String getKey() {
        return key;
    }
    
    /**
     * Simple accessor
     * @return The command enum that will be sent
     */
    public RedisCommandEnum getCommand() {
        return command;
    }
    
    @Override
    public String toString() {
        int count = 2;
        if ( value != null ) {
            count++;
        }
        StringBuilder sb = new StringBuilder(ARRAY_CHAR + count + DELIMITER);
        addStringToStringBuilder(sb, command.toString());
        addStringToStringBuilder(sb, key);
        addStringToStringBuilder(sb, value);
        
        return sb.toString();
    }
    
    private void addStringToStringBuilder(StringBuilder sb, final String str) {
        if (str == null) {
            return;
        }
        sb.append(BULK_STRING_CHAR + str.getBytes().length + DELIMITER + str + DELIMITER);
    }

}
