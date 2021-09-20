package com.acme.menusrv.common.json;

import com.acme.menusrv.common.Constants;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.time.LocalTime;

@JsonComponent
public class LocalTimeDeserializer extends JsonDeserializer<LocalTime> {

    @Override
    public LocalTime deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        return LocalTime.parse(p.getValueAsString(), Constants.TIME_FORMAT);
    }
}
