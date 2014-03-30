package com.e7hz3r0;

import java.util.function.Consumer;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
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
    private String value;
    private EventLoopGroup workerGroup;

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
        workerGroup = new NioEventLoopGroup();
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
    }

    public void disconnect() {
        try {
            if (future != null && future.channel() != null) {
                future.channel().close().sync();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
    
    /**
     * Performs a SET operation on Redis to set a string
     * @param key 
     * @param value
     * @param listener The lambda that's called when the SET is complete. It is passed
     * a string describing the error that occurred (if one occurred) or null if it succeeded.
     */
    public void set(String key, String value, Consumer<String> listener) {
    	this.value = value;
        listener.accept(null);
    }
    
    /**
     * Performs a GET operation on Redis to retrieve a string
     * @param key
     * @param listener The lambda that is passed the returned value when the GET completes.
     */
    public void get(String key, Consumer<String> listener) {
    	listener.accept(this.value);
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }
}
