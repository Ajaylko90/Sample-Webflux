package com.sample.webflux.exceptionhandler;

public class MessageProcessorException extends Exception{
    public MessageProcessorException(String message) {
        super(message);
    }
}
