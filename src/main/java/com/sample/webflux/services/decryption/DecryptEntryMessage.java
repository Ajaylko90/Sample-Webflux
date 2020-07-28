package com.sample.webflux.services.decryption;

import com.sample.webflux.exceptionhandler.MessageDecryptException;
import com.sample.webflux.models.DecryptionObject;
import com.sample.webflux.models.EntryMessage;
import reactor.core.publisher.Mono;

public interface DecryptEntryMessage<K> {
    Mono<K> getDecryptedObject(EntryMessage em) throws MessageDecryptException;
}
