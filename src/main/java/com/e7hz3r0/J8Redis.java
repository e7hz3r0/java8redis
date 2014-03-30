package com.e7hz3r0;

import java.util.function.Consumer;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class J8Redis {
    private static final String LOCALHOST = "127.0.0.1";
    private static final int DEFAULT_PORT = 6379;

    private String host;
    private int port;
    private ChannelFuture future;
    private Bootstrap bootstrap;

    public J8Redis() {
        this(LOCALHOST);
    }

    public J8Redis(String host) {
        this(host, DEFAULT_PORT);
    }

    public J8Redis(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void connect() {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            bootstrap = new Bootstrap();
            bootstrap.group(workerGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {

                }
            });
            future = bootstrap.connect(host, port);
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    public void disconnect() {
        try {
            if (future != null && future.channel() != null) {
                future.channel().close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void set(String key, String value, Consumer<String> listener) {
        listener.accept(null);
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }
}
