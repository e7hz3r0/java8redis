package com.java8redis;

import static org.junit.Assert.*;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Map;

import mockit.Deencapsulation;
import mockit.Mocked;
import mockit.NonStrictExpectations;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.e7hz3r0.RedisClientInitializer;

public class RedisClientInitializerTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testInitChannelSocketChannel(@Mocked ChannelHandlerContext ctx) {
        SocketChannel channel = new NioSocketChannel();

        RedisClientInitializer initializer = new RedisClientInitializer();
        try {
            Deencapsulation.invoke(initializer, "initChannel", channel);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        Map<String, ChannelHandler> pipeline = channel.pipeline().toMap();
        assertEquals(4, pipeline.size());
    }

}
