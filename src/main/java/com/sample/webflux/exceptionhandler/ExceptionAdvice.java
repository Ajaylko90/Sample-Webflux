package com.sample.webflux.exceptionhandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(MessageProcessorException.class)
    public ResponseEntity<String> messageProcessException(MessageProcessorException mpe) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mpe.getMessage());
    }

    @ExceptionHandler(MessageDecryptException.class)
    public ResponseEntity<String> messageDecryptException(MessageDecryptException mde) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mde.getMessage());
    }

    @ExceptionHandler(DaoException.class)
    public ResponseEntity<String> daoException(DaoException daoe) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(daoe.getMessage());
    }
}
