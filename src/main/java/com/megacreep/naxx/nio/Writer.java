package com.megacreep.naxx.nio;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Writer implements Runnable {

    private Selector selector;

    private ConcurrentLinkedQueue<X> readed;

    Reader reader;

    private static String version = "HTTP/1.1 200";

    private static String header = "Content-Type: text/html;charset=UTF-8";

    private static String crlf = "\r\n";

    public Writer(int num) {
        try {
            selector = Selector.open();
            readed = new ConcurrentLinkedQueue<>();
            new Thread(this, "Writer-" + num).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                int n = selector.select(100);
                SocketChannel channel = null;
                X x = null;
                while ((x = readed.poll()) != null) {
                    registerWrite(x.channel, x.data);
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
            System.out.println(Thread.currentThread().getName() + " write ...");
            SocketChannel channel = (SocketChannel) key.channel();
            Object data = key.attachment();
            System.out.println("write key attachment " + data.getClass().getSimpleName());
            byte[] response = (version + crlf + header + crlf + crlf + data).getBytes();
            ByteBuffer buffer = ByteBuffer.allocate(response.length);
            buffer.clear();
            buffer.put(response);
            buffer.rewind();
            channel.write(buffer);
            reader.accepted((SocketChannel) key.channel());
        } catch (Exception e) {
            e.printStackTrace();
            try {
                key.channel().close();
            } catch (Exception e1) {
                // ignored close exception
            }
        }
    }

    public void readed(X x) {
        readed.offer(x);
        System.out.println("writer selector.wakeup();");
        selector.wakeup();
    }

    protected void registerWrite(SocketChannel channel, Object data) {
        try {
            channel.configureBlocking(false);
            channel.socket().setReuseAddress(true);
            channel.socket().setTcpNoDelay(true);
            channel.socket().setKeepAlive(true);
            SelectionKey key = channel.register(selector, SelectionKey.OP_WRITE);
            key.attach(data);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                channel.close();
            } catch (Exception e1) {
                // ignored close exception
            }
        }
    }
}
