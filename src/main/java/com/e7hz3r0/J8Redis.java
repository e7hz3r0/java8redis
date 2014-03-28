package com.e7hz3r0;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class J8Redis {
	private static final String LOCALHOST = "127.0.0.1";
	private static final int DEFAULT_PORT = 6379;
	
	private String host;
	private int port;
	private ChannelFuture future;

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
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workerGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            future = bootstrap.connect(host, port);
		} finally {
            workerGroup.shutdownGracefully();
		}
	}
	
	public void disconnect() {
		if (future != null) {
			future.channel().disconnect();
			try {
				future.sync();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public String getHost() {
		return this.host;
	}
	
	public int getPort() {
		return this.port;
	}
}
