package com.megacreep.naxx.http;

import java.util.Arrays;

/**
 * 请求行和标题必须以<CR><LF>作为结尾。空行内必须只有<CR><LF>而无其他空格。在HTTP/1.1协议中，所有的请求头，除Host外，都是可选的。
 * 结合Reader优化为流式解析http报文，放弃对 multipart 的支持
 */
public class HttpParser {

    private static byte space = 0x20;
    private static byte lf = 0x0A;
    private static byte cr = 0x0D;
    private static byte[] crlf = {cr, lf};

    private static String parseMethod(ByteArray byteArray) {
        byte[] bytes = byteArray.bytes;
        byte b;
        int pos = byteArray.pos;
        for (; byteArray.pos < bytes.length; ) {
            b = bytes[byteArray.pos++];
            if (b == space) {
                byte[] methods = new byte[byteArray.pos - 1];// 不要空格
                System.arraycopy(bytes, pos, methods, 0, methods.length);
                System.out.println(new String(methods));
                return new String(methods);
            }
        }
        return "UNKNOWN";
    }

    private static String parsePath(ByteArray byteArray) {
        byte[] bytes = byteArray.bytes;
        byte b;
        int pos = byteArray.pos;
        for (; byteArray.pos < bytes.length; ) {
            b = bytes[byteArray.pos++];
            if (b == space) {
                byte[] bpath = new byte[byteArray.pos - pos - 1];// 不要空格
                System.arraycopy(bytes, pos, bpath, 0, bpath.length);
                System.out.println(new String(bpath));
                return new String(bpath);
            }
        }
        return "UNKNOWN";
    }

    private static Param parseParam(String path) {
        Param p = new Param();
        if (path.contains("?")) {
            String[] parts = path.split("\\?");
            System.out.println(Arrays.toString(parts));
            if (parts.length > 1) {
                String params = parts[1];
                String[] kvs = params.split("&");
                for (String temp : kvs) {
                    System.out.println("parseParam " + temp);
                    String[] kv = temp.split("=");
                    p.put(kv[0], kv[1]);
                }
            }
        }
        return p;
    }

    private static String parseVersion(ByteArray byteArray) {
        byte[] bytes = byteArray.bytes;
        byte b;
        int pos = byteArray.pos;
        for (; byteArray.pos < bytes.length; ) {
            b = bytes[byteArray.pos++];
            if (b == lf) {
                if (bytes[byteArray.pos - 2] == cr) {
                    byte[] vbytes = new byte[byteArray.pos - pos];
                    System.arraycopy(bytes, pos, vbytes, 0, vbytes.length);
                    System.out.println(new String(vbytes));
                    return new String(vbytes);
                }
            }
        }
        return "UNKNOWN";
    }

    private static Header parseHeaders(ByteArray byteArray) {
        byte[] bytes = byteArray.bytes;
        byte b;
        int pos = byteArray.pos;
        Header header = new Header();
        for (; byteArray.pos < bytes.length; ) {
            b = bytes[byteArray.pos++];
            if (b == lf) {
                if (bytes[byteArray.pos - 2] == cr) {
                    byte[] hbytes = new byte[byteArray.pos - pos];
                    System.arraycopy(bytes, pos, hbytes, 0, hbytes.length);
                    pos = byteArray.pos;
                    if (!Arrays.equals(crlf, hbytes)) {
                        System.out.println(new String(hbytes));
                        String kvs = new String(hbytes);
                        String[] kv = kvs.split(": ");
                        header.put(kv[0], kv[1]);
                    } else {
                        break;
                    }
                }
            }
        }
        return header;
    }

    private static Body parseBody(ByteArray byteArray) {
        Body body = new Body();
        byte[] bbytes = new byte[byteArray.bytes.length - byteArray.pos];
        System.arraycopy(byteArray.bytes, byteArray.pos, bbytes, 0, bbytes.length);
        return body;
    }

    public static HttpRequest parseHttpRequest(byte[] bytes) {
        ByteArray byteArray = new ByteArray(bytes, 0);
        HttpRequest httpRequest = new HttpRequest();
        httpRequest.setMethod(parseMethod(byteArray));
        String uri = parsePath(byteArray);
        httpRequest.setUri(uri.split("\\?")[0]);
        httpRequest.setVersion(parseVersion(byteArray));
        httpRequest.setHeader(parseHeaders(byteArray));
        httpRequest.setBody(parseBody(byteArray));
        httpRequest.setParam(parseParam(uri));
        return httpRequest;
    }
}

class ByteArray {

    byte[] bytes;
    int pos;

    public ByteArray() {
    }

    public ByteArray(byte[] bytes, int pos) {
        this.bytes = bytes;
        this.pos = pos;
    }
}
