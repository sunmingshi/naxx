package com.megacreep.naxx.nio;

import com.megacreep.naxx.api.Decoder;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;


public class Reader implements Runnable {


    private Selector selector;
    private ConcurrentLinkedQueue<SocketChannel> toRead;
    private Decoder decoder;
    public Writer writer;

    public Reader(int num, Decoder decoder) {
        try {
            this.decoder = decoder;
            toRead = new ConcurrentLinkedQueue<>();
            selector = Selector.open();
            new Thread(this, "Reader-" + num).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            while (true) {
                int n = selector.select(10);
                SocketChannel channel = null;
                while ((channel = toRead.poll()) != null) {
                    registerRead(channel);
                }
                if (n > 0) {
                    Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                    SelectionKey key = null;
                    while (keys.hasNext()) {
                        key = keys.next();
                        if (key.isReadable()) {
                            read(key);
                        }
                        keys.remove();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void read(SelectionKey key) {
        try {
            SocketChannel channel = (SocketChannel) key.channel();
            System.out.println("read " + channel.hashCode());
            int initCapacity = 512;
            ByteBuffer buffer = ByteBuffer.allocate(initCapacity);
            int readCount = channel.read(buffer);
            // 读取为空
            if (readCount < 0) {
                key.cancel();
                return;
            }
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
            key.cancel();
            Object data = decoder.decode(bytes);
            Session s = new Session(channel, data);
            writer.offerWrite(s);
        } catch (Exception e) {
            e.printStackTrace();
            Closer.close(key.channel());
        }
    }

    public void offerRead(SocketChannel socketChannel) {
        toRead.offer(socketChannel);
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
        }
    }
}
