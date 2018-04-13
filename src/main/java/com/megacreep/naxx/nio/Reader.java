package com.megacreep.naxx.nio;

import com.megacreep.naxx.coder.Decoder;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;


public class Reader implements Runnable {

    private Selector selector;
    private ConcurrentLinkedQueue<SocketChannel> accepted;
    private Writer writer;

    public Reader(int num, Writer writer) {
        try {
            this.writer = writer;
            selector = Selector.open();
            accepted = new ConcurrentLinkedQueue<>();
            new Thread(this, "Reader-" + num).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            while (true) {
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
                        Decoder.decode(readyKey.attachment());
                        writer.readed((SocketChannel) readyKey.channel());
                        readyKeys.remove();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void read(SelectionKey key) {
        try {
            System.out.println(Thread.currentThread().getName() + " read ...");
            SocketChannel channel = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(512);
            int readCount = channel.read(buffer);
            int total = readCount;
            byte[] bytes = buffer.array();
            while (readCount == buffer.capacity()) {
                // 读满一个缓存区，再读另一个
                buffer.clear();
                readCount = channel.read(buffer);
                total += readCount;
                int pos = bytes.length;
                bytes = Arrays.copyOf(bytes, bytes.length + readCount);
                System.arraycopy(buffer.array(), 0, bytes, pos, readCount);
            }
            ((HashMap<String, Object>) key.attachment()).put("content", bytes);
            System.out.println("\r\ntotal read count:" + total + ", bytes.length=" + bytes.length);
        } catch (Exception e) {
            e.printStackTrace();
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
            SelectionKey key = channel.register(selector, SelectionKey.OP_READ);
            key.attach(new HashMap<String, Object>());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
