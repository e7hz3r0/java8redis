package com.e7hz3r0;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class CommandEncoder extends MessageToByteEncoder<RedisCommand> {

    @Override
    protected void encode(ChannelHandlerContext ctx, RedisCommand command,
            ByteBuf buffer) throws Exception {
        buffer.writeBytes(command.toString().getBytes());
    }
}
