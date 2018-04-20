package com.megacreep.naxx.controller;

import com.megacreep.naxx.api.Controller;
import com.megacreep.naxx.api.Request;
import com.megacreep.naxx.http.HttpRequest;
import java.util.HashMap;

@Controller("/controller")
public class HelloWorldController {

    public String hello(HttpRequest req) {
        return "hello";
    }

    @Request("/helloworld")
    public HashMap<String, Object> hellojson(HttpRequest req) {
        HashMap<String, Object> json = new HashMap<String, Object>();
        json.put("msg", "hello");
        return json;
    }
}
