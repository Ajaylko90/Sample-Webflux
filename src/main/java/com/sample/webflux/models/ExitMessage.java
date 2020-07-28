package com.sample.webflux.models;

import com.sample.webflux.models.db.PersonDetailOutput;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class ExitMessage {

    private String message;
    private String key1Id;
    private String age;
    private List<String> diseases;
    private String statusCode;
    private String errorMessage;

}

