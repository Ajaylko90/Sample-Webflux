package com.sample.webflux.models;


import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class DecryptionObject {
    String username;
    String password;
    Integer id;
}
