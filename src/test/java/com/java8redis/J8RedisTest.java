package com.java8redis;

import static org.junit.Assert.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import mockit.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.e7hz3r0.j8redis.J8Redis;
import com.e7hz3r0.j8redis.RedisClientInitializer;

public class J8RedisTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testDefaultCreation() {
        J8Redis redis = new J8Redis();
        assertEquals("127.0.0.1", redis.getHost());
        assertEquals(6379, redis.getPort());
    }

    @Test
    public void testCreationWithHost() {
        J8Redis redis = new J8Redis("192.168.1.1");
        assertEquals("192.168.1.1", redis.getHost());
        assertEquals(6379, redis.getPort());
    }

    @Test
    public void testCreationWithHostAndPort() {
        J8Redis redis = new J8Redis("192.168.1.1", 7654);
        assertEquals("192.168.1.1", redis.getHost());
        assertEquals(7654, redis.getPort());
    }

    @Test
    public void testConnect(@Mocked final Bootstrap bootstrap,
                            @Mocked final ChannelFuture future) {
        new NonStrictExpectations() {
            {
                bootstrap.connect("127.0.0.1", 6379);
                returns(future);
                times = 1;
            }
        };

        J8Redis redis = new J8Redis();
        redis.connect(ch -> {
            
        });

        new Verifications() {
            {
                new Bootstrap();
                times = 1;
                bootstrap.handler(withInstanceOf(RedisClientInitializer.class));
                times = 1;
                bootstrap.connect("127.0.0.1", 6379);
                times = 1;
            }
        };
    }

    @Test
    public void testDisconnect(@Mocked final Channel channel,
            @Mocked final NioEventLoopGroup group
            ){

        try {
            new NonStrictExpectations() { {
                channel.close();
                times = 1;
                group.shutdownGracefully();
                times = 1;
            }};

            J8Redis redis = new J8Redis();
            Deencapsulation.setField(redis, "workerGroup", group);
            Deencapsulation.setField(redis, "channel", channel);
            redis.disconnect();

            new Verifications() { {
                channel.close();
                times = 1;
                group.shutdownGracefully();
                times = 1;
            }};
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected exception: " + e.getMessage());
        }
    }

}
