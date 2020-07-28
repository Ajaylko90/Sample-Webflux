package com.sample.webflux.services.decryption;

import com.sample.webflux.exceptionhandler.MessageDecryptException;
import com.sample.webflux.models.DecryptionObject;
import com.sample.webflux.models.EntryMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class DecryptEntryMessageImpl extends DecryptMessageCore implements DecryptEntryMessage<DecryptionObject> {

    @Override
    public Mono<DecryptionObject> getDecryptedObject(EntryMessage em) throws MessageDecryptException {
        try {
            String DecryptedJson = decryptData(em.getMessage(), em.getKey1Id());
            DecryptionObject decryptionObject = new DecryptionObject();
            decryptionObject.setPassword("xyz");
            decryptionObject.setUsername(DecryptedJson);
            decryptionObject.setId(Integer.parseInt(em.getKey1Id()));
            return Mono.just(decryptionObject);
        } catch (NumberFormatException nfe) {
            log.error("Exception decrypting data: ", nfe);
            throw new MessageDecryptException("Exception decrypting data: ");
        }
    }
}
