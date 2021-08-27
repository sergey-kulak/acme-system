package com.acme.rfdatasrv.currency;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("currencies")
public class Currency {
    @Id
    private String code;
    private String name;
    private String symbol;
    private boolean active;
}
