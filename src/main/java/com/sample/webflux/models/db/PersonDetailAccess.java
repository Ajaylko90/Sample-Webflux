package com.sample.webflux.models.db;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PersonDetailAccess {

    Integer ID;
    String name;
    Integer age;
    String diseaseinfo;

}
