package com.sample.webflux.models.db;

import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
public class PersonDetailOutput {
    Integer ID;
    String name;
    Integer age;
    DiseaseInfo diseaseinfo;
}
