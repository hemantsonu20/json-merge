package com.github.hemantsonu20.json;

/**
 * Exception to be thrown in case of any error occured while merging two json.
 *
 */
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
