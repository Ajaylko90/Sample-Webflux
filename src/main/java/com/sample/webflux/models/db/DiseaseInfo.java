package com.sample.webflux.models.db;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class DiseaseInfo {

    List<String> diseases;

}
