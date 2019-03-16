package com.example.bhaumik.spendsmart.helper;

public class EventDbFormat {
    String message;

    public EventDbFormat(){

    }
    public EventDbFormat(String message) {
        this.message = message;
    }

    public String getMessage() {

        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
