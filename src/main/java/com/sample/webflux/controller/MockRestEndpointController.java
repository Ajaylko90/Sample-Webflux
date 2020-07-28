package com.sample.webflux.controller;

import com.sample.webflux.models.ExitMessage;
import com.sample.webflux.models.RestCallObject;
import io.micrometer.core.annotation.Timed;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class MockRestEndpointController {

    private int temperature;
    private String weather;


    @PostMapping("/history")
    @Timed(
            value= "com.sample.webflux.controller.postTemperature",
            histogram =true,
            percentiles = {0.95,0.99},
            extraTags = {"version","1.0.0"}
    )
    public Mono<ExitMessage> postTemperature(@RequestBody RestCallObject restCObj){
        ExitMessage exitMessage = new ExitMessage();
        if(restCObj.getKey1Id().equals("40")){
            throw new RuntimeException();
        }
        exitMessage.setKey1Id(restCObj.getKey1Id());
        if(restCObj!=null && restCObj.getPersonDetailOutput()!=null) {
            exitMessage.setAge(restCObj.getPersonDetailOutput().getAge().toString());
            exitMessage.setDiseases(restCObj.getPersonDetailOutput().getDiseaseinfo().getDiseases());
            exitMessage.setMessage("Not well");
        }
        else {
            exitMessage.setMessage("No Data Available");
        }

        return  Mono.just(exitMessage);
    }

   /*

    @GetMapping("/temperature")
    Remove getMapping and use route registration to use this
    public Mono<ServerResponse> temperature(ServerRequest req) {
        Stream<Integer> stream = Stream.iterate(0, i -> i + 1);
        Flux<Temperature> mapFlux = Flux.fromStream(stream).zipWith(Flux.interval(Duration.ofSeconds(1)))
                .map(i -> {
                    Temperature templarature = new Temperature();
                    templarature.setTemperature(temperature);
                    templarature.setWeather(weather);
                    return templarature;
                });

        return ok().contentType(MediaType.APPLICATION_STREAM_JSON).body(mapFlux,
                Temperature.class);
    }


    */
}
