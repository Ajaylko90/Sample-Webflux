package com.sample.webflux.services.restcaller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sample.webflux.models.ExitMessage;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.micrometer.core.annotation.Timed;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class CallApiImpl implements CallApi{

    @Value("${rest.hostname.port}")
    String hostnamePort;

    @Value("${rest.webclient.connect.timeout.ms:1000}")
    Integer connectTimeOut;

    @Value("${rest.webclient.read.timeout.ms:1000}")
    Integer readTimeOut;

    WebClient client ;

    @PostConstruct
    public void init(){
        client = createWebClientWithConnectAndReadTimeOuts(connectTimeOut,readTimeOut);
    }

    private WebClient createWebClientWithConnectAndReadTimeOuts(int connectTimeOut, long readTimeOut) {
        /*
           Webclient is built on top of HttpClient so TCP level connection settings have to be set on HttpClient
         */
        HttpClient httpClient = HttpClient.create()
                .tcpConfiguration(tcpClient -> {
                    tcpClient = tcpClient.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeOut);
                    tcpClient = tcpClient.doOnConnected(conn -> conn
                            .addHandlerLast(new ReadTimeoutHandler(readTimeOut, TimeUnit.MILLISECONDS)));
                    return tcpClient;
                });
        ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);
        return WebClient.builder().clientConnector(connector)
                .baseUrl(hostnamePort)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .filter(logRequest())
                .filter(logResponse())
                .build();
    }

    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            log.info("Response status code: {}", clientResponse.statusCode());
            return Mono.just(clientResponse);
        });
    }

    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("Request {} {}", clientRequest.method(), clientRequest.url());
            return Mono.just(clientRequest);
        });
    }


    @Override
    @Timed(
            value= "com.sample.webflux.services.restcaller.callPostApi",
            histogram =true,
            percentiles = {0.95,0.99},
            extraTags = {"version","1.0.0"}
    )
    @CircuitBreaker(name = "backendB", fallbackMethod = "handleBrokenCircuit")
    public Mono<?> callPostApi(String uri,String body) {
        Mono<String> employeeMono = client.post()
                .uri(uri)
                .body(Mono.just(body), String.class)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(error -> log.error("Error signal detected for uri :{},body:{}",uri,body, error));;
        return employeeMono;
    }

    public Mono<?> handleBrokenCircuit(String uri,String body,Exception ex) throws JsonProcessingException {
        ExitMessage em = new ExitMessage();
        em.setMessage("Remote service is not available");
        log.warn("Request execution being handled on fallback uri :{} body:{} ",uri,body);
        return Mono.just(new ObjectMapper().writeValueAsString(em));
    }


}
