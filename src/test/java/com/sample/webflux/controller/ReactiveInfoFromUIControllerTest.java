package com.sample.webflux.controller;

import com.sample.webflux.models.EntryMessage;
import com.sample.webflux.models.ExitMessage;
import com.sample.webflux.services.process.EntryMessageProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;

class ReactiveInfoFromUIControllerTest {

    WebTestClient webTestClient;
    EntryMessageProcessor entryMessageProcessor;
    ReactiveInfoFromUIController controller;

    @BeforeEach
    void setUp() {
        entryMessageProcessor = Mockito.mock(EntryMessageProcessor.class);
        controller = new ReactiveInfoFromUIController(entryMessageProcessor);
        webTestClient = WebTestClient.bindToController(controller).build();
    }

    @Test
    void postInfo() {
        EntryMessage entryMsg = new EntryMessage();
        entryMsg.setKey1Id("40");
        entryMsg.setMessage("Stable");

        Mono<EntryMessage> mono = Mono.just(entryMsg);

        webTestClient.post()
                .uri("/v1/sendInfo")
                .body(mono, EntryMessage.class)
                .exchange()
                .expectStatus()
                .isOk();

        webTestClient.post()
                .uri("/v1/sendInfo")
                .body(mono, EntryMessage.class)
                .exchange()
                .expectBody(ExitMessage.class);
    }
}