package com.acme.rfdata.country;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("countries")
public class Country {
    @Id
    private String code;
    private String name;
    private boolean active;
}
