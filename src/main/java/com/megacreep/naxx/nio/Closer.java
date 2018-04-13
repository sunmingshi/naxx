package com.megacreep.naxx.nio;

import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 任何发生了异常的 channel 都应该被关闭
 */
public class Closer implements Runnable {

    private ConcurrentLinkedQueue<SocketChannel> toclose;

    public Closer(ConcurrentLinkedQueue<SocketChannel> toclose) {
        this.toclose = toclose;
    }

    @Override
    public void run() {
        try {
            SocketChannel channel;
            while ((channel = toclose.poll()) != null) {
                channel.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close(SocketChannel channel) {
        toclose.offer(channel);
    }
}
