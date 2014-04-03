package com.e7hz3r0;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class RedisClientHandler extends SimpleChannelInboundHandler<String> {
    private enum ParsingState {
        EMPTY,
        BATCH_STRING,
        LIST
    }

    private Object response = null;
    private Deque<ParsingState> states = new ArrayDeque<>();
    private Deque<Integer> itemsInList = new ArrayDeque<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg)
            throws Exception {
        if(isInitialState()) {
            response = null;
        }

        final char type = msg.charAt(0);
        switch (type) {
        case '*':
            int len = Integer.parseInt(msg.substring(1));
            if (len == 0) {
                addToResponse(new ArrayList<Object>());
            } else if (len == -1) {
                addToResponse(null);
            } else {
                itemsInList.push(len);
                addToResponse(new ArrayList<Object>(len));
                states.push(ParsingState.LIST);
            }
            break;
        case '+':
            addToResponse(msg.substring(1));
            break;
        case ':':
            addToResponse(Integer.parseInt(msg.substring(1)));
            break;
        case '-':
            addToResponse(new Exception(msg.substring(1)));
            break;
        case '$':
            len = Integer.parseInt(msg.substring(1));
            if (len == 0){
                addToResponse("");
            } else if (len == -1) {
                addToResponse(null);
            } else {
                states.push(ParsingState.BATCH_STRING);
            }
            break;
        default:
            if (states.peek() == ParsingState.BATCH_STRING) {
                states.pop();
                addToResponse(msg);
            } 
            break;
        }
    }
    
    private void addToResponse(Object obj) {
        if(states.isEmpty()) {
            response = obj;
        } else if (states.peek() == ParsingState.LIST) {
            ((List<Object>)response).add(obj);
            Integer len = itemsInList.pop();
            len--;
            itemsInList.push(len);
            if(len == 0) {
                itemsInList.pop();
                states.pop();
            }
        }
    }
    
    private boolean isInitialState() {
        return itemsInList.isEmpty() && states.isEmpty();
    }
    
    public boolean isResponseReady(){
        return states.isEmpty() && itemsInList.isEmpty();
    }
    
    public Object getResponse() {
        return response;
    }
    
}
