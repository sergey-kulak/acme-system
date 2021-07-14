package com.acme.usersrv.common.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.data.domain.PageImpl;

import java.io.IOException;

@JsonComponent
public class PageImplSerializer extends JsonSerializer<PageImpl<?>> {

    @Override
    public void serialize(PageImpl value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("number", value.getNumber());
        gen.writeNumberField("totalElements", value.getTotalElements());
        gen.writeNumberField("totalPages", value.getTotalPages());
        if (value.getPageable().isPaged()) {
            gen.writeNumberField("size", value.getPageable().getPageSize());
        }
        gen.writeFieldName("content");
        provider.defaultSerializeValue(value.getContent(), gen);
        gen.writeEndObject();
    }
}
