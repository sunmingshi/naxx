package com.megacreep.naxx.http;

import com.megacreep.naxx.api.Decoder;

public class HttpDecoder implements Decoder {
    @Override
    public Object decode(byte[] bytes) {
        HttpRequest req = HttpParser.parseHttpRequest(bytes);
        return Context.invoke(req);
    }
}
