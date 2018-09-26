package com.megacreep.naxx.nio;

import com.megacreep.naxx.api.Decoder;
import com.megacreep.naxx.api.Encoder;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class Acceptor implements Runnable {

    private static final int SOCKET_BACKLOG = 1024;
    private Selector selector;
    private boolean started;
    private Reader[] readers;
    private Writer[] writers;
    private int threads;

    public Acceptor(int threads, Decoder decoder, Encoder encoder) {
        try {
            this.threads = threads;
            writers = new Writer[threads];
            readers = new Reader[threads];
            selector = Selector.open();
            started = false;
            for (int i = 0; i < threads; i++) {
                Writer writer = new Writer(i, encoder);
                Reader reader = new Reader(i, decoder);
                writer.reader = reader;
                reader.writer = writer;
                writers[i] = writer;
                readers[i] = reader;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            int h = 0;
            for (; ; ) {
                int n = selector.select();
                if (n > 0) {
                    Iterator<SelectionKey> readyKeys = selector.selectedKeys().iterator();
                    while (readyKeys.hasNext()) {
                        SelectionKey readyKey = readyKeys.next();
                        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) readyKey.channel();
                        SocketChannel socketChannel = serverSocketChannel.accept();
                        readers[h++].offerRead(socketChannel);
                        readyKeys.remove();
                        if (h == threads) {
                            h = 0;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void bind(InetSocketAddress bindAddress) {
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().setReuseAddress(true);
            serverSocketChannel.socket().bind(bindAddress, SOCKET_BACKLOG);
            SelectionKey key = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void start() {
        try {
            if (started) {
                return;
            }
            started = true;
            new Thread(this, "Acceptor").start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
