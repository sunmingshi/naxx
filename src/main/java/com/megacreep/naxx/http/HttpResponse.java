package com.megacreep.naxx.http;

public class HttpResponse {

    private Header header;

    private Body body;

    public HttpResponse() {
        header = new Header();
        body = new Body();
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
                "header=" + header +
                ", body=" + body +
                '}';
    }
}
