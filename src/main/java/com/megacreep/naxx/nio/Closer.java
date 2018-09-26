package com.megacreep.naxx.nio;

import java.nio.channels.Channel;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Closer implements Runnable {

    private static ConcurrentLinkedQueue<Channel> toclose = new ConcurrentLinkedQueue<>();

    @Override
    public void run() {
        try {
            Channel channel;
            while ((channel = toclose.poll()) != null) {
                try {
                    channel.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void close(Channel channel) {
        toclose.offer(channel);
    }
}
