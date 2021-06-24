package com.calamar.ecom2.serializers;

import com.calamar.ecom2.DmsWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class DmsSerde implements Serde<DmsWrapper> {

    @Override
    public Serializer<DmsWrapper> serializer() {
        return new LeanSerde();
    }

    @Override
    public Deserializer<DmsWrapper> deserializer() {
        return new LeanSerde();
    }

}
