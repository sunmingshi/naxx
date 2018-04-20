package com.megacreep.naxx.controller;

import com.google.gson.JsonObject;
import com.megacreep.naxx.api.Controller;
import com.megacreep.naxx.api.Request;
import com.megacreep.naxx.http.HttpRequest;

@Controller("/controller")
public class HelloWorldController {

    public String hello(HttpRequest req) {
        return "hello";
    }

    @Request("/helloworld")
    public JsonObject hellojson(HttpRequest req) {
        JsonObject json = new JsonObject();
        json.addProperty("msg", "hello");
        return json;
    }
}
