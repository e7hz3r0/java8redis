package com.java8redis;

import static org.junit.Assert.*;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.EmptyByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import mockit.Deencapsulation;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;

import org.junit.Test;

import com.e7hz3r0.j8redis.CommandEncoder;
import com.e7hz3r0.j8redis.RedisCommand;
import com.e7hz3r0.j8redis.RedisCommand.RedisCommandEnum;

public class CommandEncoderTest {

    @Test
    public void testBasicCommandEncoding( @Mocked ChannelHandlerContext ctx,
                                         @Mocked ChannelPromise promise) {
        CommandEncoder enc = new CommandEncoder();
        String key = "key";
        String value = "value";
        RedisCommand cmd = new RedisCommand(RedisCommandEnum.SET, key, value);
        String expected = "*3\r\n$3\r\nSET\r\n$3\r\nkey\r\n$5\r\nvalue\r\n";
        List<Object> out = new ArrayList<>();
        
        Deencapsulation.invoke(enc, "encode", ctx, cmd, out);
        
        assertEquals(expected, (String)out.get(0));
    }

}
