package com.java8redis;

import static org.junit.Assert.*;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import io.netty.channel.ChannelHandlerContext;
import mockit.Deencapsulation;
import mockit.Mocked;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.e7hz3r0.j8redis.RedisClientHandler;

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
        handler.addResponseListener((v, e) -> {
            assertEquals("OK", v);
        });
        Deencapsulation.invoke(handler, "channelRead0", ctx, msg);
    }

    @Test
    public void testInteger(@Mocked ChannelHandlerContext ctx) {
        RedisClientHandler handler = new RedisClientHandler();
        
        String msg = ":123";
        
        handler.addResponseListener((v, e) -> {
            assertEquals(123, v);
        });
        Deencapsulation.invoke(handler, "channelRead0", ctx, msg);
    }

    @Test
    public void testError(@Mocked ChannelHandlerContext ctx) {
        RedisClientHandler handler = new RedisClientHandler();
        
        String msg = "-Fake error";
        handler.addResponseListener((v, e) -> {
            assertEquals("Fake error", e.getMessage());
        });
        Deencapsulation.invoke(handler, "channelRead0", ctx, msg);
    }

    @Test
    public void testBatchString(@Mocked ChannelHandlerContext ctx) {
        RedisClientHandler handler = new RedisClientHandler();
    
        String msg = "$5";
        Deencapsulation.invoke(handler, "channelRead0", ctx, msg);
        msg = "Value";
        handler.addResponseListener((v, e) -> {
            assertEquals("Value", handler.getResponse());
        });
        Deencapsulation.invoke(handler, "channelRead0", ctx, msg);

        msg = "$0";
        handler.addResponseListener((v, e) -> {
            assertEquals("", v);
        });
        Deencapsulation.invoke(handler, "channelRead0", ctx, msg);

        msg = "$-1";

        handler.addResponseListener((v, e) -> {
            assertNull(e);
            assertNull(v);
        });

        Deencapsulation.invoke(handler, "channelRead0", ctx, msg);

    }

    @Test
    public void testList(@Mocked ChannelHandlerContext ctx) {
        RedisClientHandler handler = new RedisClientHandler();

        //Try an empty list
        String msg = "*0";
        handler.addResponseListener((v, e) -> {
            assertNull(e);
            assertTrue(v instanceof List<?>);
            List<Object> list = (List<Object>)v;
            assertEquals(0, list.size());
        });

        Deencapsulation.invoke(handler, "channelRead0", ctx, msg);

        //start a new list
        handler.addResponseListener((v, e) -> {
            assertNull(e);
            assertTrue(v instanceof List<?>);

            List<Object> list = (List<Object>)v;
            
            assertEquals(3, list.size());
            assertEquals(null, list.get(0));
            assertTrue(list.get(1) instanceof List<?>);
            assertEquals("Simple", list.get(2));

            List<Object> list2 = (List<Object>)list.get(1);
            assertEquals(2, list2.size());
            assertEquals("Key", list2.get(0));
            assertTrue(list2.get(1) instanceof List<?>);

            List<Object> list3 = (List<Object>)list2.get(1);
            assertEquals(0, list3.size());
        });

        msg = "*3";
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
        assertFalse(handler.isResponseReady());
        
        
        //Add a simple string to the base list
        msg = "+Simple";

        Deencapsulation.invoke(handler, "channelRead0", ctx, msg);
        
    }

}
