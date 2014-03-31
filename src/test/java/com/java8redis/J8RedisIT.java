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
        String key2 = "MyKey2";
        String value = "Myvalue";
        String value2 = "Myvalue2";

        J8Redis redis = new J8Redis("127.0.0.1");
        redis.connect();
        redis.set(key, value, (errorMsg) -> {
            assertNull(errorMsg);

            redis.set(key2, value2, (errorMsg2) -> {
                assertNull(errorMsg2);

                redis.get(key, (val) -> {
                    assertEquals(value, val); 
                    redis.disconnect();
                });
            });
        });
    }

}
