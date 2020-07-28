package com.sample.webflux.exceptionhandler;

public class DaoException extends Exception {
    public DaoException(String message) {
        super(message);
    }
}
