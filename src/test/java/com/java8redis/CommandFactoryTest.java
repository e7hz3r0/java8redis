package com.java8redis;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.e7hz3r0.j8redis.CommandFactory;
import com.e7hz3r0.j8redis.RedisCommand;

public class CommandFactoryTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testCreateSetCommand() {
        String key = "Key";
        String value = "Value";
        RedisCommand cmd = CommandFactory.setCommand(key, value);
        assertEquals(key, cmd.getKey());
        assertEquals(value, cmd.getValue());
        assertEquals("SET", cmd.getCommand().toString());
    }

    @Test
    public void testCreateGetCommand() {
        String key = "Key";
        RedisCommand cmd = CommandFactory.getCommand(key);
        assertEquals(key, cmd.getKey());
        assertNull(cmd.getValue());
        assertEquals("GET", cmd.getCommand().toString());
    }

}
