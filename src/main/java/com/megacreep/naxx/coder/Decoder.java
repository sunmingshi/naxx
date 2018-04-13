package com.megacreep.naxx.coder;

import java.util.HashMap;

public class Decoder {

    public static void decode(Object att) {
        HashMap<String, Object> map = (HashMap<String, Object>) att;
        System.out.println(new String((byte[]) map.get("content")));
    }
}
