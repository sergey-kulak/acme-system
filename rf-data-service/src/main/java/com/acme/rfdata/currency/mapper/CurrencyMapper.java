package com.acme.rfdata.currency.mapper;

import com.acme.rfdata.country.Country;
import com.acme.rfdata.currency.Currency;
import com.acme.rfdata.currency.dto.CurrencyDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CurrencyMapper {
    CurrencyDto toDto(Currency s);
}
