package com.e7hz3r0;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

public class RedisClientInitializer extends ChannelInitializer<SocketChannel> {
    public static final String DECODER = "decoder";
    public static final String ENCODER = "encoder";
    public static final String FRAMER = "framer";
    public static final String HANDLER = "handler";

    public static final int MAX_FRAME_LEN = 8162;
    public static final ByteBuf DELIMITER = Unpooled.wrappedBuffer(new byte[] {'\r', '\n'});

    private static final DelimiterBasedFrameDecoder REDIS_FRAMER = new DelimiterBasedFrameDecoder(MAX_FRAME_LEN, true, false, DELIMITER);
    private static final RedisClientHandler REDIS_CLIENT_HANDLER = new RedisClientHandler();
    private static final StringDecoder REDIS_DECODER = new StringDecoder(CharsetUtil.UTF_8);
    private static final StringEncoder REDIS_ENCODER = new StringEncoder(CharsetUtil.UTF_8);

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(FRAMER, REDIS_FRAMER);
        pipeline.addLast(DECODER, REDIS_DECODER);
        pipeline.addLast(ENCODER, REDIS_ENCODER);
        pipeline.addLast(HANDLER, REDIS_CLIENT_HANDLER);
    }

}
