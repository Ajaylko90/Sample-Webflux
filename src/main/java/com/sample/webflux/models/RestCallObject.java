package com.sample.webflux.models;

import com.sample.webflux.models.db.PersonDetailOutput;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class RestCallObject {
    private String message;
    private String key1Id;
    String username;
    String password;
    PersonDetailOutput personDetailOutput;
}
