package com.sample.webflux.services.process;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sample.webflux.exceptionhandler.DaoException;
import com.sample.webflux.exceptionhandler.MessageDecryptException;
import com.sample.webflux.exceptionhandler.MessageProcessorException;
import com.sample.webflux.models.DecryptionObject;
import com.sample.webflux.models.EntryMessage;
import com.sample.webflux.models.ExitMessage;
import com.sample.webflux.models.RestCallObject;
import com.sample.webflux.models.db.PersonDetailOutput;
import com.sample.webflux.services.database.CrudService;
import com.sample.webflux.services.database.CrudServiceImpl;
import com.sample.webflux.services.decryption.DecryptEntryMessage;
import com.sample.webflux.services.restcaller.CallApi;
import com.sample.webflux.services.restcaller.CallApiImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Service
@Slf4j
public class EntryMessageProcessor {


    @Value("${rest.endpoint.uri}")
    String uri;


    CallApi callApiImpl;
    DecryptEntryMessage decryptEntryMessage;
    CrudService crudServiceImpl;

    @Autowired
    public EntryMessageProcessor(CallApiImpl callApiImpl, DecryptEntryMessage decryptEntryMessage, CrudServiceImpl crudServiceImpl) {
        this.callApiImpl = callApiImpl;
        this.decryptEntryMessage = decryptEntryMessage;
        this.crudServiceImpl = crudServiceImpl;
    }


    public Mono<ExitMessage> process(EntryMessage entryMsg) throws MessageDecryptException {
        Mono<DecryptionObject> decryptionObjectMono = null;
        Mono<ExitMessage> exitMessage = null;
        try {
            decryptionObjectMono = decryptEntryMessage.getDecryptedObject(entryMsg);
            exitMessage = decryptionObjectMono.flatMap(
                    decryptionObject -> {
                        try {
                            Mono<PersonDetailOutput> pdo = crudServiceImpl.getDetailsByID(decryptionObject.getId());
                            Mono<String> existMsg = (Mono<String>) callApiImpl.callPostApi(uri, formRestObjectInput(entryMsg, decryptionObject, pdo));
                            return formOutput(existMsg);
                        } catch (IOException e) {
                            log.error("Exception retrieving data from DB", e);
                        } catch (MessageProcessorException e) {
                            log.error("Unable to parse message");
                        }
                        return Mono.empty();
                    });
        } catch (Exception e) {
            log.error("Exception retrieving data from DB" + e);
            throw new MessageDecryptException("Exception retrieving data from DB");
        }
        return exitMessage;
    }

    private String formRestObjectInput(EntryMessage entryMsg, DecryptionObject decryptionObject, Mono<PersonDetailOutput> pdo) throws JsonProcessingException {
        RestCallObject rco = new RestCallObject();
        rco.setKey1Id(entryMsg.getKey1Id());
        rco.setMessage(entryMsg.getMessage());
        rco.setPassword(decryptionObject.getPassword());
        rco.setUsername(decryptionObject.getUsername());
        rco.setPersonDetailOutput(pdo.block());
        return new ObjectMapper().writeValueAsString(rco);
    }

    private Mono<ExitMessage> formOutput(Mono<String> restOutputString) throws MessageProcessorException {
        try {
            return restOutputString.flatMap(
                    output -> {
                        try {
                            return Mono.just(new ObjectMapper().readValue(output, ExitMessage.class));
                        } catch (JsonProcessingException e) {
                            try {
                                log.error("Unable to parse: {}", output, e);
                                throw new MessageProcessorException("Unable to parse: " + output);
                            } catch (MessageProcessorException messageProcessorException) {
                                messageProcessorException.printStackTrace();
                            }
                        }
                        return Mono.empty();
                    }
            );
        } catch (Exception e) {
            log.error("Unable to parse message");
            throw new MessageProcessorException("Unable to parse");
        }
    }
}