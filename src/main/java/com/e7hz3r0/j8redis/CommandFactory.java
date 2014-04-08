package com.e7hz3r0.j8redis;

public class CommandFactory {
    public static RedisCommand setCommand(String key, String value) {
        return new RedisCommand(RedisCommand.RedisCommandEnum.SET, key, value);
    }

    public static RedisCommand getCommand(String key) {
        return new RedisCommand(RedisCommand.RedisCommandEnum.GET, key);
    }
}
