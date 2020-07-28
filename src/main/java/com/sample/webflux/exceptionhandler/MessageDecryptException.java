package com.sample.webflux.exceptionhandler;

public class MessageDecryptException extends Exception{
    public MessageDecryptException(String message) {
        super(message);
    }
}
