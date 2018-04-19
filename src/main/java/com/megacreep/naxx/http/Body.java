package com.megacreep.naxx.http;

public class Body {

    public Body() {
        text = "";
    }

    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "Body{" +
                "text='" + text + '\'' +
                '}';
    }
}
