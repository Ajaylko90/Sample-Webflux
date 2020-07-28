package com.sample.webflux.services.restcaller;

import com.sample.webflux.models.RestCallObject;
import reactor.core.publisher.Mono;

public interface CallApi {
    Mono<?> callPostApi(String uri,String temp);
}
