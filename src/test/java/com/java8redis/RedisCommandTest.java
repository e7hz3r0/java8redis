package com.java8redis;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.e7hz3r0.RedisCommand;

public class RedisCommandTest {
    private String myKey;
    private String myValue;
    private RedisCommand setCommand;
    private RedisCommand getCommand;

    @Before
    public void setUp() throws Exception {
        myValue = "Value!";
        myKey = "Key!";
        setCommand = new RedisCommand(RedisCommand.RedisCommandEnum.SET, myKey, myValue);
        getCommand = new RedisCommand(RedisCommand.RedisCommandEnum.GET, myKey);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testCreation() {
        assertEquals("SET", setCommand.getCommand().toString());
        assertEquals(myKey, setCommand.getKey());
        assertEquals(myValue, setCommand.getValue());
    }
    
    @Test
    public void testToString() {
        String expectedString = "*3\r\n$3\r\nSET\r\n$4\r\nKey!\r\n$6\r\nValue!\r\n";
        assertEquals(expectedString, setCommand.toString());
    }

    @Test
    public void testGetToString() {
        String expectedString = "*3\r\n$3\r\nGET\r\n$4\r\nKey!\r\n";
        assertEquals(expectedString, getCommand.toString());
    }

}
