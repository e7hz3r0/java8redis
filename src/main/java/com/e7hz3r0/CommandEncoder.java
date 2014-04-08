package com.e7hz3r0;

import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.CharsetUtil;

/**
 * Encodes a RedisCommand object to the RESP representation.
 * @author Ethan Urie
 *
 */
public class CommandEncoder extends MessageToMessageEncoder<RedisCommand> {

    @Override
    protected void encode(ChannelHandlerContext ctx, RedisCommand cmd,
            List<Object> out) throws Exception {
        out.add(cmd.toString());
    }
}
