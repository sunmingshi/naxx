package com.megacreep.naxx.nio;

import com.megacreep.naxx.api.Encoder;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Writer implements Runnable {
    private Selector selector;
    private ConcurrentLinkedQueue<Session> toWrite;
    private Encoder encoder;
    public Reader reader;

    public Writer(int num, Encoder encoder) {
        try {
            this.encoder = encoder;
            toWrite = new ConcurrentLinkedQueue<>();
            selector = Selector.open();
            new Thread(this, "Writer-" + num).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                int n = selector.select(10);
                Session t = null;
                while ((t = toWrite.poll()) != null) {
                    registerWrite(t.channel, t.data);
                }
                if (n > 0) {
                    Iterator<SelectionKey> readyKeys = selector.selectedKeys().iterator();
                    SelectionKey readyKey = null;
                    while (readyKeys.hasNext()) {
                        readyKey = readyKeys.next();
                        if (readyKey.isWritable()) {
                            write(readyKey);
                        }
                        readyKeys.remove();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void write(SelectionKey key) {
        try {
            SocketChannel channel = (SocketChannel) key.channel();
            System.out.println("write " + channel.hashCode());
            Object data = key.attachment();
            byte[] response = encoder.encode(data);
            ByteBuffer buffer = ByteBuffer.allocate(response.length);
            buffer.put(response);
            buffer.rewind();
            channel.write(buffer);
            System.out.println("write finish");
            key.cancel();
            reader.registerRead(channel);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                key.channel().close();
            } catch (Exception e1) {
                // ignored close exception
            }
        }
    }

    public void offerWrite(Session s) {
        toWrite.offer(s);
    }

    protected void registerWrite(SocketChannel channel, Object data) {
        try {
            channel.configureBlocking(false);
            channel.socket().setReuseAddress(true);
            channel.socket().setTcpNoDelay(true);
            channel.socket().setKeepAlive(true);
            SelectionKey k = channel.register(selector, SelectionKey.OP_WRITE);
            k.attach(data);
        } catch (Exception e) {
            e.printStackTrace();
            Closer.close(channel);
        }
    }
}
