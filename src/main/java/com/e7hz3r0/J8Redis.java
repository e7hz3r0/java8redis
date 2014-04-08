package com.e7hz3r0;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * The main interface for the Redis client library.
 * @author Ethan Urie
 *
 */
public class J8Redis {
    private static final String LOCALHOST = "127.0.0.1";
    private static final int DEFAULT_PORT = 6379;

    private String host;
    private int port;
    private Channel channel;
    private Bootstrap bootstrap;
    private EventLoopGroup workerGroup;
    private RedisClientHandler handler;
    
    /**
     * Default contructor that connects to the Redis server running locally on the default port (6379).
     */
    public J8Redis() {
        this(LOCALHOST);
    }

    /**
     * Constructor that connects to the Redis server running on the specified host on the default port (6379).
     * @param host The hostname or IP address of the machine that is running the Redis server.
     */
    public J8Redis(String host) {
        this(host, DEFAULT_PORT);
    }

    /**
     * Constructor that connects to the Redis server running on the specified host and port.
     * @param host The hostname or IP address of the machine that is running the Redis server.
     * @param port The port that the Redis server is using on the target machine.
     */
    public J8Redis(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Connects to the Redis server
     * @param listener The lambda that is executed when connection completes. The argument is the Channel that is created.
     */
    public void connect(Consumer<Channel> listener) {
        workerGroup = new NioEventLoopGroup();
        try {
            bootstrap = new Bootstrap();
            bootstrap.group(workerGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.handler(new RedisClientInitializer());
            ChannelFuture future = bootstrap.connect(host, port);
            future.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture cf) throws Exception {
                    channel = future.channel();
                    handler = (RedisClientHandler) channel.pipeline().get(RedisClientInitializer.HANDLER);
                    listener.accept(cf.channel());
                }
                
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Closes the connection to the Redis server.
     */
    public void disconnect() {
        try {
            if (channel != null){
                channel.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
    
    /**
     * Simple endpoint to save a value to Redis
     * @param key
     * @param value
     * @param listener Lambda expression that is executed when the send completes or fails. First argument is the result, if successful. Second argument is the error string if not.
     */
    public void set(String key, String value, BiConsumer<Object, Exception> listener) {
        sendCommand(new RedisCommand(RedisCommand.RedisCommandEnum.SET, key, value), listener);
    }
    
    /**
     * Simple endpoint to retrieve a string from Redis
     * @param key
     * @param listener
     */
    public void get(String key, BiConsumer<Object, Exception> listener) {
        sendCommand(new RedisCommand(RedisCommand.RedisCommandEnum.GET, key), listener);
    }
    
    /**
     * Generic endpoint to send a RedisCommand to the server.
     * @param cmd
     * @param listener
     */
    public void sendCommand(RedisCommand cmd, BiConsumer<Object, Exception> listener) {
        ChannelFuture sendFuture = channel.write(cmd);
        handler.addResponseListener(listener);
        channel.flush();
    }

    /**
     * Returns the host (IP or hostname) that this instanc will connect to
     * @return The hostname or IP address of the server
     */
    public String getHost() {
        return this.host;
    }

    /**
     * Returns the port this connect is configured to connect to.
     * @return The port number
     */
    public int getPort() {
        return this.port;
    }
}
