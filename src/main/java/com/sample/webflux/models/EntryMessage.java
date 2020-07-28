package com.sample.webflux.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@NoArgsConstructor
@Data
public class EntryMessage implements Serializable {

    private String message;

    private String key1Id;
}
