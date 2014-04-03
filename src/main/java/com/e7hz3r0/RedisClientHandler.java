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
    private Deque<ListItem> lists = new ArrayDeque<>();

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
            ListItem item = new ListItem(len, new ArrayList<Object>());
            if (len != -1) {
                addToResponse(item);
            } else {
                addToResponse(null);
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
            } else {
                //this is an unknown response, for now, swallow it
            }
            break;
        }
    }
    
    private void addToResponse(Object obj) {
        if(lists.isEmpty()) {
            if(obj instanceof ListItem) {
                ListItem li = (ListItem)obj;
                response = li.getList();
                lists.push(li);
            } else {
                response = obj;
            }
        } else {
            ListItem top = lists.peek();
            if(obj instanceof ListItem) {
                top.addToList(((ListItem) obj).getList());
                lists.push((ListItem)obj);
            } else {
                top.addToList(obj);
            }

        }

        // This should pop off all full lists, including empty lists with max length of 0
        while(lists.peek() != null && lists.peek().isFull()){
            lists.pop();
        }
    }
    
    private boolean isInitialState() {
        return lists.isEmpty() && states.isEmpty();
    }
    
    public boolean isResponseReady(){
        return states.isEmpty() && lists.isEmpty();
    }
    
    public Object getResponse() {
        return response;
    }
    
    /**
     * Represents a single list item to be placed on the stack
     * @author ethan
     *
     */
    private static class ListItem {
        private final int numberOfItems;
        private final List<Object> list;
        
        public ListItem(final int numItems, final List<Object> list) {
            this.numberOfItems = numItems;
            this.list = list;
        }

        public void addToList(Object obj) {
            getList().add(obj);
        }
        
        public boolean isFull() {
            return getList().size() >= numberOfItems;
        }

        public List<Object> getList() {
            return list;
        }
    }
    
}
