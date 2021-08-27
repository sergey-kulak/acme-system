package com.acme.rfdatasrv.lang;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("languages")
public class Lang {
    @Id
    private String code;
    private String name;
    private String nativeName;
    private boolean active;
}
