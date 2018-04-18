package com.megacreep.naxx.http;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * 请求行和标题必须以<CR><LF>作为结尾。空行内必须只有<CR><LF>而无其他空格。在HTTP/1.1协议中，所有的请求头，除Host外，都是可选的。
 * 测试数据换行为 <LF>
 * 结合Reader优化为流式解析http报文，放弃对 multipart 的支持
 */
public class X {

    private static byte space = 0x20;
    private static byte lf = 0x0A;
    private static byte cr = 0x0D;

    private static int parseMethod(ByteBuffer buffer) {
        byte b;
        int pos = buffer.position();
        while (buffer.position() < buffer.limit()) {
            b = buffer.get();
            if (b == space) {
                byte[] bytes = new byte[buffer.position()];
                buffer.position(pos);
                System.out.println("pos " + pos + ", length " + bytes.length);
                buffer.get(bytes, 0, bytes.length);
                buffer.mark();
                System.out.println(new String(bytes));
                return 3;
            }
        }
        return 1;
    }

    private static int parsePath(ByteBuffer buffer) {
        byte b;
        int pos = buffer.position();
        while (buffer.position() < buffer.limit()) {
            b = buffer.get();
            if (b == space) {
                byte[] bytes = new byte[buffer.position() - pos];
                buffer.reset();
                System.out.println("pos " + pos + ", length " + bytes.length);
                buffer.get(bytes, 0, bytes.length);
                buffer.mark();
                String path = new String(bytes);
                parseParam(path);
                return 3;
            }
        }
        return 1;
    }

    private static int parseParam(String path) {

        if (path.contains("?")) {
            String[] parts = path.split("\\?");
            System.out.println(Arrays.toString(parts));
        }

        return 1;
    }

    private static int parseVersion(ByteBuffer buffer) {
        byte b;
        int pos = buffer.position();
        while (buffer.position() < buffer.limit()) {
            b = buffer.get();
            if (b == lf) {
                byte[] bytes = new byte[buffer.position() - pos];
                buffer.reset();
                System.out.println("pos " + pos + ", length " + bytes.length);
                buffer.get(bytes, 0, bytes.length);
                buffer.mark();
                System.out.println(new String(bytes));
                return 3;
            }
        }
        return 1;
    }

    private static int parseHeader(ByteBuffer buffer) {
        byte b;
        int pos = buffer.position();
        while (buffer.position() < buffer.limit()) {
            b = buffer.get();
            if (b == lf) {
                byte[] bytes = new byte[buffer.position() - pos];
                buffer.reset();
                System.out.println("pos " + pos + ", length " + bytes.length);
                buffer.get(bytes, 0, bytes.length);
                buffer.mark();
                String kv = new String(bytes);
                if (kv.equalsIgnoreCase("\n")) {
                    System.out.println("header end");
                    return -1;
                }
                String[] kvs = kv.split(":");
                System.out.println("header " + kvs[0] + " :" + kvs[1]);
                parseHeader(buffer);
                return 3;
            }
        }
        return 1;
    }

    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(4096);
        byte[] bytes = ("GET /update/config? HTTP/1.1\n" +
                "AMIS_GROUP_KEY: spil_demo\n" +
                "AMIS_ROLES: %E7%B3%BB%E7%BB%9F%E7%AE%A1%E7%90%86%E5%91%98\n" +
                "AMIS_PERMS: admin%3Apermission,admin%3Arole,admin%3Auser%3Aview,admin%3Auser%3AviewAll,admin%3Auser%3Aedit,admin%3Auser%3Adelete,admin%3Auser%3Aexternal%3Aview,admin%3Auser%3Aexternal%3Aedit,admin%3Auser%3Aexternal%3Adelete,admin%3Aacl,admin%3Apage,admin%3Asettings,admin%3Amock,admin%3Acomponent,admin%3Arecord,admin%3Aorganization,page%3Aview%3Acode,debug%3Aapi,__is_uuap_user\n" +
                "AMIS_USER_IP: 172.19.247.46\n" +
                "AMIS_USER_TYPE: 0\n" +
                "AMIS_USER: sunmingshi\n" +
                "AMIS_IS_OWNER: true\n" +
                "Cookie: BAIDUID=1AF063C594A462A7AE4339692A818F62:FG=1; BIDUPSID=1AF063C594A462A7AE4339692A818F62; PSTM=1520495586; BDUSS=lpWVhLbS1oLVZxVHF3bTNZcVRqeTJZSkxycFNvRWk0dlZzMWVXajd4eXFqT1ZhQVFBQUFBJCQAAAAAAAAAAAEAAABMke9xMjAwve-1xLzlsf3PwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAKr~vVqq~71aTj; pgv_pvi=9453675520; _ga=GA1.2.1950443734.1523936528; _gid=GA1.2.1733833974.1523936528; BKMASKSID=65b65a22f0743031d59872f6417d694e; H_PS_PSSID=1460_21096_20929; amisid=s%3AkLTQSkwY9ov5HaNeW-u8mjhPw2gqbQwN.NgKxnjOQMMl%2BhqDQXGA4340CxoZoPfdy4sBCI8aCjuw; Hm_lvt_1f80f2c9dbe21dc3af239cf9eee90f1f=1523693585,1523953237; Hm_lpvt_1f80f2c9dbe21dc3af239cf9eee90f1f=1523957351\n" +
                "Host: 10.99.202.243:8001\n\n" +
                "Connection: close").getBytes();
        buffer.put(bytes);
        buffer.flip();
        parseMethod(buffer);
        parsePath(buffer);
        parseVersion(buffer);
        parseHeader(buffer);
    }
}
