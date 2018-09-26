package com.megacreep.naxx.nio;

import java.nio.channels.SocketChannel;

public class Session {
    public SocketChannel channel;

    public Object data;

    public Session() {
    }

    public Session(SocketChannel channel, Object data) {
        this.channel = channel;
        this.data = data;
    }
}
