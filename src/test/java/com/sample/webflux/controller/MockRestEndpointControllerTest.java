package com.sample.webflux.controller;

import com.sample.webflux.models.ExitMessage;
import com.sample.webflux.models.RestCallObject;
import com.sample.webflux.models.db.DiseaseInfo;
import com.sample.webflux.models.db.PersonDetailOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class MockRestEndpointControllerTest {

    WebTestClient webTestClient;
    MockRestEndpointController controller;

    @BeforeEach
    void setUp() {
        controller = new MockRestEndpointController();
        webTestClient = WebTestClient.bindToController(controller).build();
    }

    @Test
    void postTemperature() {

        DiseaseInfo info = new DiseaseInfo();
        info.setDiseases(Arrays.asList("Hypertension","Diabetes"));

        PersonDetailOutput detailOutput = new PersonDetailOutput();
        detailOutput.setAge(23);
        detailOutput.setID(23);
        detailOutput.setName("hella");
        detailOutput.setDiseaseinfo(info);

        RestCallObject restCallObject = new RestCallObject();
        restCallObject.setKey1Id("3");
        restCallObject.setMessage("message");
        restCallObject.setUsername("abc");
        restCallObject.setPassword("d2wd");
        restCallObject.setPersonDetailOutput(detailOutput);


        Mono<RestCallObject> mono = Mono.just(restCallObject);

        webTestClient.post()
                .uri("/history")
                .body(mono, RestCallObject.class)
                .exchange()
                .expectStatus()
                .isOk();
    }
}