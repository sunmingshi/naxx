package com.megacreep.naxx;

import com.megacreep.naxx.nio.Acceptor;
import java.net.InetSocketAddress;

public class Start {

    public static void main(String[] args) {
        Acceptor acceptor = new Acceptor(8);
        acceptor.bind(new InetSocketAddress(8001), 3000);
        acceptor.start();
    }
}
