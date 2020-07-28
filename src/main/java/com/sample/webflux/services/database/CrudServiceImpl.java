package com.sample.webflux.services.database;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sample.webflux.dao.DBInteraction;
import com.sample.webflux.dao.PersonDetailDBInteractionRepository;
import com.sample.webflux.models.db.DiseaseInfo;
import com.sample.webflux.models.db.PersonDetailAccess;
import com.sample.webflux.models.db.PersonDetailOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

@Service
public class CrudServiceImpl implements CrudService{

    DBInteraction pdRepo;

    @Autowired
    public CrudServiceImpl(PersonDetailDBInteractionRepository pdRepo){
        this.pdRepo = pdRepo;
    }


    @Override
    public Mono<PersonDetailOutput> getDetailsByID(Integer ID) throws IOException {
        PersonDetailOutput diseaseInfo = new PersonDetailOutput();
        List<PersonDetailAccess> pda = pdRepo.getbyIDUsingStoredProcedure(ID);
        return buildDiseaseInfo(pda);
    }

    private Mono<PersonDetailOutput> buildDiseaseInfo( List<PersonDetailAccess> opda) throws IOException {
        PersonDetailOutput dInfo = new PersonDetailOutput();
        if(!opda.isEmpty()) {
            PersonDetailAccess pda = opda.get(0);
            dInfo.setAge(pda.getAge());
            dInfo.setID(pda.getID());
            dInfo.setName(pda.getName());
            dInfo.setDiseaseinfo(new ObjectMapper().readValue(pda.getDiseaseinfo(), DiseaseInfo.class));
            return Mono.just(dInfo);
        }
        return Mono.empty();
    }
}
