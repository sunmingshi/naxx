package com.megacreep.naxx.nio;

import java.nio.channels.SocketChannel;

public class X {

    public SocketChannel channel;

    public Object data;

    public X() {
    }

    public X(SocketChannel channel, Object data) {
        this.channel = channel;
        this.data = data;
    }
}
