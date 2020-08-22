package com.github.hemantsonu20.json;

public class JsonMergeException extends RuntimeException {

    public JsonMergeException() {
        super();
    }

    public JsonMergeException(String msg) {
        super(msg);
    }

    public JsonMergeException(String msg, Throwable th) {
        super(msg, th);
    }
}
