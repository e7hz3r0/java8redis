package com.java8redis;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.e7hz3r0.j8redis.J8Redis;

public class J8RedisIT {
    private static Process redisProc;
    private String key = "MyKey";
    private String key2 = "MyKey2";
    private String value = "Myvalue";
    private String value2 = "Myvalue2";
    private J8Redis redis;
    private static int BOOTUP_WAIT_TIME = 6000; //milliseconds

    @BeforeClass
    public static void beforeClass() throws Exception {
        ProcessBuilder pb = new ProcessBuilder().command("/opt/local/bin/redis-server");
        redisProc = pb.start();
        Thread.sleep(BOOTUP_WAIT_TIME);
    }

    @AfterClass
    public static void afterClass() throws Exception {
        redisProc.destroy();
    }

    @Before
    public void setUp() throws Exception {
        assertTrue(redisProc.isAlive());
        CountDownLatch cdl = new CountDownLatch(1);
        redis = new J8Redis("127.0.0.1");
        redis.connect(ch -> {
            redis.del(key, (r, error) -> {});
            redis.del(key2, (r, error) -> {});
            cdl.countDown();
        });
        cdl.await();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() {

        try {
            CountDownLatch cdl = new CountDownLatch(1);
            redis.set(key, value, (result, error) -> {
                assertNull(error);

                redis.get(key, (result1, error1) -> {
                    assertNull(error1);
                    assertEquals(value, result1);

                    redis.set(key2, value2, (result2, error2) -> {
                        assertNull(error2);

                        redis.get(key, (val, error3) -> {
                            assertNull(error3);
                            assertEquals(value, val); 
                            cdl.countDown();
                        });
                    });
                });
            });
            assertTrue(cdl.await(2000, TimeUnit.MILLISECONDS));
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        } finally {
            redis.disconnect();
            redisProc.destroy();
        }
    }

}
