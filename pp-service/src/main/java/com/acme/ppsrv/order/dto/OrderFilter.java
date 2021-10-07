package com.acme.ppsrv.order.dto;

import com.acme.ppsrv.order.OrderStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class OrderFilter {
    @NotNull
    private UUID companyId;
    @NotNull
    private UUID publicPointId;
    private String number;
    private OrderStatus status;
    private BigDecimal fromTotalPrice;
    private BigDecimal toTotalPrice;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fromCreatedDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate toCreatedDate;
    private UUID dishId;

    @Tolerate
    public OrderFilter() {
    }
}
