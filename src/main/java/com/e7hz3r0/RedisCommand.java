package com.e7hz3r0;

import com.e7hz3r0.RedisCommand.RedisCommandEnum;

public class RedisCommand {
    public enum RedisCommandEnum {
        SET("SET"),
        GET("GET");
        
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
    public final String DELIMITER = "\\r\\n";
    

    public RedisCommand(RedisCommandEnum cmd, String key) {
        this(cmd, key, null);
    }

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
    
    public RedisCommandEnum getCommand() {
        return command;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(ARRAY_CHAR + 3 + DELIMITER);
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
