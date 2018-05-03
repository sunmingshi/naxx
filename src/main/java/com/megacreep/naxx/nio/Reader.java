package com.megacreep.naxx.nio;

import com.megacreep.naxx.api.Decoder;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;


public class Reader implements Runnable {

    private Selector selector;
    private ConcurrentLinkedQueue<SocketChannel> accepted;
    Writer writer;
    private Decoder decoder;

    public Reader(int num, Decoder decoder) {
        try {
            this.decoder = decoder;
            selector = Selector.open();
            accepted = new ConcurrentLinkedQueue<>();
            new Thread(this, "Reader-" + num).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            try {
                int n = selector.select(100);
                SocketChannel channel = null;
                while ((channel = accepted.poll()) != null) {
                    registerRead(channel);
                }
                if (n > 0) {
                    Iterator<SelectionKey> readyKeys = selector.selectedKeys().iterator();
                    SelectionKey readyKey = null;
                    while (readyKeys.hasNext()) {
                        readyKey = readyKeys.next();
                        if (readyKey.isReadable()) {
                            read(readyKey);
                        }
                        readyKeys.remove();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void read(SelectionKey key) {
        try {
            System.out.println(Thread.currentThread().getName() + " read ...");
            SocketChannel channel = (SocketChannel) key.channel();
            int initCapacity = 512;
            ByteBuffer buffer = ByteBuffer.allocate(initCapacity);
            int readCount = channel.read(buffer);
            int total = readCount;
            byte[] bytes = new byte[readCount];
            System.arraycopy(buffer.array(), 0, bytes, 0, total);
            while (readCount == initCapacity) {
                // 读满一个缓存区，再读另一个
                buffer.clear();
                readCount = channel.read(buffer);
                total += readCount;
                int pos = bytes.length;
                bytes = Arrays.copyOf(bytes, bytes.length + readCount);
                System.arraycopy(buffer.array(), 0, bytes, pos, readCount);
            }
            Object result = decoder.decode(bytes);
            System.out.println("invoke result=" + result);
            X x = new X(channel, result);
            writer.readed(x);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                key.channel().close();
            } catch (IOException e1) {
                // ignored close exception
            }
        }
    }

    public void accepted(SocketChannel socketChannel) {
        accepted.offer(socketChannel);
        selector.wakeup();
    }

    protected void registerRead(SocketChannel channel) {
        try {
            channel.configureBlocking(false);
            channel.socket().setReuseAddress(true);
            channel.socket().setTcpNoDelay(true);
            channel.socket().setKeepAlive(true);
            channel.register(selector, SelectionKey.OP_READ);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                channel.close();
            } catch (IOException e1) {
                // ignored close exception
            }
        }
    }
}
