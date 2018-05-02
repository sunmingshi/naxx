package com.megacreep.naxx.nio;

import com.megacreep.naxx.http.HttpDecoder;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * 注册Accept事件，当有Accept事件ready时，把相应的 channel 传递给Reader
 */
public class Acceptor implements Runnable {

    private static final int SOCKET_BACKLOG = 10240;
    private static final int DEFAULT_IO_TIMEOUT_MILLIS = 30000;
    private Selector selector;
    private boolean started;
    private Reader reader;
    private Writer writer;
    private int handlers;
    private Reader[] readers;
    private Writer[] writers;

    public Acceptor(int handlers) {
        try {
            this.handlers = handlers;
            writers = new Writer[handlers];
            readers = new Reader[handlers];
            selector = Selector.open();
            started = false;
            for (int i = 0; i < handlers; i++) {
                writer = new Writer(i);
                reader = new Reader(i, new HttpDecoder());
                writers[i] = writer;
                readers[i] = reader;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            int h = 0;
            while (true) {
                int n = selector.select();
                System.out.println("nonblocking");
                if (n > 0) {
                    Iterator<SelectionKey> readyKeys = selector.selectedKeys().iterator();
                    while (readyKeys.hasNext()) {
                        SelectionKey readyKey = readyKeys.next();
                        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) readyKey.channel();
                        SocketChannel socketChannel = serverSocketChannel.accept();
                        readers[h++].accepted(socketChannel);
                        readyKeys.remove();
                        if (h == handlers) {
                            h = 0;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void bind(InetSocketAddress addr, int timeout) {
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().setReuseAddress(true);
            serverSocketChannel.socket().bind(addr, SOCKET_BACKLOG);
            SelectionKey key = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            key.attach(timeout != 0 ? timeout : DEFAULT_IO_TIMEOUT_MILLIS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动接收器
     */
    public synchronized void start() {
        try {
            if (started) {
                return;
            }
            started = true;
            new Thread(this, "MainLoop").start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
