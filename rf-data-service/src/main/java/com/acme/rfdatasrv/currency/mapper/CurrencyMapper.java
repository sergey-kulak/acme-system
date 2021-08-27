package com.acme.rfdatasrv.currency.mapper;

import com.acme.rfdatasrv.currency.Currency;
import com.acme.rfdatasrv.currency.dto.CurrencyDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CurrencyMapper {
    CurrencyDto toDto(Currency s);
}
