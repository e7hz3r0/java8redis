package com.e7hz3r0;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.function.BiConsumer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class RedisClientHandler extends SimpleChannelInboundHandler<String> {
    private Object response = null;
    private boolean parsingBatchString = false;
    private Deque<ListItem> lists = new ArrayDeque<>();
    
    private BlockingQueue<BiConsumer<Object, Exception>> callbackQueue = new ArrayBlockingQueue<>(20, true);
    
    public void addResponseListener(BiConsumer<Object, Exception> listener) {
        callbackQueue.add(listener);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg)
            throws Exception {
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
                parsingBatchString = true;
            }
            break;
        default:
            if (parsingBatchString){
                parsingBatchString = false;
                addToResponse(msg);
            } else {
                //this is an unknown response, for now, swallow it
            }
            break;
        }
        if(isResponseReady()) {
            BiConsumer<Object, Exception> consumer = callbackQueue.remove();
            if (response instanceof Exception) {
                consumer.accept(null, (Exception) response);
            } else {
                consumer.accept(response, null);
            }
            response = null;
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
        return lists.isEmpty() && !parsingBatchString;
    }
    
    public boolean isResponseReady(){
        return isInitialState() && response != null;
    }
    
    public Object getResponse() {
        return response;
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
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
