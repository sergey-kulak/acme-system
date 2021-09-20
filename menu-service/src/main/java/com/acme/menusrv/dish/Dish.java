package com.acme.menusrv.dish;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Document("dish")
public class Dish {
    @Id
    private UUID id;
    private UUID companyId;
    private UUID publicPointId;
    private String name;
    private String description;
    private String composition;
    private List<String> tags;
    private String primaryImage;
    private List<String> images;
    private BigDecimal price;
    private boolean deleted;
}
