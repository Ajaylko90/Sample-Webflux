package com.sample.webflux.services.database;

import com.sample.webflux.models.db.PersonDetailOutput;
import reactor.core.publisher.Mono;

import java.io.IOException;

public interface CrudService {
    public Mono<PersonDetailOutput> getDetailsByID(Integer ID) throws IOException;

}
