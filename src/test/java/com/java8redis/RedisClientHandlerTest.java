package com.java8redis;

import static org.junit.Assert.*;

import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import mockit.Deencapsulation;
import mockit.Mocked;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.e7hz3r0.RedisClientHandler;

public class RedisClientHandlerTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testSimpleString(@Mocked ChannelHandlerContext ctx) {
        RedisClientHandler handler = new RedisClientHandler();
        
        String msg = "+OK";
        Deencapsulation.invoke(handler, "channelRead0", ctx, msg);
        assertEquals("OK", handler.getResponse());
    }

    @Test
    public void testInteger(@Mocked ChannelHandlerContext ctx) {
        RedisClientHandler handler = new RedisClientHandler();
        
        String msg = ":123";
        Deencapsulation.invoke(handler, "channelRead0", ctx, msg);
        assertEquals(123, handler.getResponse());
    }

    @Test
    public void testError(@Mocked ChannelHandlerContext ctx) {
        RedisClientHandler handler = new RedisClientHandler();
        
        String msg = "-Fake error";
        Deencapsulation.invoke(handler, "channelRead0", ctx, msg);
        assertEquals("Fake error", ((Exception)handler.getResponse()).getMessage());
    }

    @Test
    public void testBatchString(@Mocked ChannelHandlerContext ctx) {
        RedisClientHandler handler = new RedisClientHandler();
        
        String msg = "$5";
        Deencapsulation.invoke(handler, "channelRead0", ctx, msg);
        assertFalse(handler.isResponseReady());
        assertEquals(null, handler.getResponse());
        msg = "Value";
        Deencapsulation.invoke(handler, "channelRead0", ctx, msg);
        assertEquals("Value", handler.getResponse());

        msg = "$0";
        Deencapsulation.invoke(handler, "channelRead0", ctx, msg);
        assertEquals("", handler.getResponse());

        msg = "$-1";
        Deencapsulation.invoke(handler, "channelRead0", ctx, msg);
        assertEquals(null, handler.getResponse());
    }

    @Test
    public void testList(@Mocked ChannelHandlerContext ctx) {
        RedisClientHandler handler = new RedisClientHandler();

        //start the list
        String msg = "*2";
        Deencapsulation.invoke(handler, "channelRead0", ctx, msg);
        assertFalse(handler.isResponseReady());

        //Add a null list
        msg = "*-1";
        Deencapsulation.invoke(handler, "channelRead0", ctx, msg);
        assertFalse(handler.isResponseReady());
        assertTrue(handler.getResponse() instanceof List<?>);
        
        
        //Add a 2-item list
        msg = "*2";
        Deencapsulation.invoke(handler, "channelRead0", ctx, msg);
        assertFalse(handler.isResponseReady());

        // Add a Batch string
        msg = "$3";
        Deencapsulation.invoke(handler, "channelRead0", ctx, msg);
        assertFalse(handler.isResponseReady());

        msg = "Key";
        Deencapsulation.invoke(handler, "channelRead0", ctx, msg);
        assertFalse(handler.isResponseReady());

        //Add an empty list
        msg = "*0";
        Deencapsulation.invoke(handler, "channelRead0", ctx, msg);
        assertTrue(handler.isResponseReady());
        assertTrue(handler.getResponse() instanceof List<?>);

    }

}
