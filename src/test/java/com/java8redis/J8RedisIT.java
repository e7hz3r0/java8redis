package com.java8redis;

import static org.junit.Assert.*;
import junit.framework.TestCase;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.e7hz3r0.J8Redis;

public class J8RedisIT {
    private static Process redisProc;

    @BeforeClass
    public static void beforeClass() throws Exception {
        ProcessBuilder pb = new ProcessBuilder().command("redis-server");
        redisProc = pb.start();
    }

    @AfterClass
    public static void afterClass() throws Exception {
        redisProc.destroy();
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() {
        String key = "MyKey";
        String value = "Myvalue";
        J8Redis redis = new J8Redis("127.0.0.1");
        redis.connect();
        redis.set(key, value, (errorMsg) -> {
          if (errorMsg == null) {
              redis.disconnect();
          } else {
              redis.disconnect();
              fail("Unexpected error: " + errorMsg);
          }
        });
    }

}
