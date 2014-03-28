package com.java8redis;

import static org.junit.Assert.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import mockit.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.e7hz3r0.J8Redis;

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
	public void testConnect(@Mocked final Bootstrap bootstrap) {
		new NonStrictExpectations() {{
			bootstrap.connect("127.0.0.1", 6379); times = 1;
		}};

		J8Redis redis = new J8Redis();
		redis.connect();
		
		new Verifications() {{
			new Bootstrap(); times = 1;
			bootstrap.connect("127.0.0.1", 6379); times = 1;
		}};
	}

	@Test
	public void testDisconnect(@Mocked final Channel channel, 
							   @Mocked("sync()") final ChannelFuture future,
							   @Mocked final Bootstrap bootstrap) {

		try {
			new NonStrictExpectations() {{
				bootstrap.connect(anyString, anyInt); returns(future);
				future.channel(); returns(channel);
				channel.close(); times = 1;
			}};
		
            J8Redis redis = new J8Redis();
            redis.connect();
            redis.disconnect();

            new Verifications() {{
                channel.close(); times = 1;
            }};
		} catch (Exception e) {
			fail("Unexpected exception");
		}
	}

}
