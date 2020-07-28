package com.sample.webflux.controller;

import com.sample.webflux.models.EntryMessage;
import com.sample.webflux.models.ExitMessage;
import com.sample.webflux.services.process.EntryMessageProcessor;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.logging.Level;

@RestController
@Slf4j
public class ReactiveInfoFromUIController {

    EntryMessageProcessor entryMessageProcessor;

    @Autowired
    public ReactiveInfoFromUIController(EntryMessageProcessor entryMessageProcessor){
        this.entryMessageProcessor = entryMessageProcessor;
    }

    @PostMapping("/v1/sendInfo")
    @Timed(
            value= "com.sample.webflux.controller.ReactiveInfoFromUIController.postInfo",
            histogram =true,
            percentiles = {0.95,0.99},
            extraTags = {"version","1.0.0"}
    )
    public Mono<ExitMessage> postInfo(@RequestBody EntryMessage entryMsg){
        try {
            Mono<ExitMessage> exitMessage = entryMessageProcessor.process(entryMsg).log("ReactiveInfoFromUIController.postInfo exitMessage log for:{} "+entryMsg, Level.INFO,null);
            return exitMessage;
        } catch (Exception e) {
            return Mono.error(e);
        }
    }
}
