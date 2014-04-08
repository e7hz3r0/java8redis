package com.java8redis;

import static org.junit.Assert.*;

import java.util.concurrent.CountDownLatch;

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
        ProcessBuilder pb = new ProcessBuilder().command("/opt/local/bin/redis-server");
        redisProc = pb.start();
    }

    @AfterClass
    public static void afterClass() throws Exception {
        redisProc.destroy();
    }

    @Before
    public void setUp() throws Exception {
//        assertTrue(redisProc.isAlive());
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

        try {
            J8Redis redis = new J8Redis("127.0.0.1");
            redis.connect(ch -> {
                redis.set(key, value, (result, error) -> {
                    assertNull(error);

                    redis.set(key2, value2, (result2, error2) -> {
                        assertNull(error2);

                        redis.get(key, (val, error3) -> {
                            assertNull(error3);
                            assertEquals(value, val); 
                            redis.disconnect();
                        });
                    });
                });
            });
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        } finally {
            redisProc.destroy();
        }
    }

}
