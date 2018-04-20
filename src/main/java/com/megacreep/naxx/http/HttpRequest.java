package com.megacreep.naxx.http;


public class HttpRequest {

    public HttpRequest() {
        this.header = new Header();
        this.param = new Param();
        this.body = new Body();
    }

    private String method;
    private String uri;
    private String version;

    private Header header;
    private Param param;
    private Body body;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public void setHeader(String k, Object v) {
        if (this.header == null) {
            this.header = new Header();
        }
        this.header.put(k, v);
    }

    public Param getParam() {
        return param;
    }

    public void setParam(Param param) {
        this.param = param;
    }

    public void setParam(String k, Object v) {
        if (this.param == null) {
            this.param = new Param();
        }
        this.param.put(k, v);
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "method='" + method + '\'' +
                ", uri='" + uri + '\'' +
                ", version='" + version + '\'' +
                ", header=" + header +
                ", param=" + param +
                ", body=" + body +
                '}';
    }
}
