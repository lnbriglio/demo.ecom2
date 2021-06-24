package com.calamar.ecom2.serializers;

import com.calamar.ecom2.DmsWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.boot.autoconfigure.security.SecurityProperties;

import java.util.Map;

public class LeanSerde implements Serializer<DmsWrapper>, Deserializer<DmsWrapper> {

    @Override
    public void configure(Map configs, boolean isKey) {
        Serializer.super.configure(configs, isKey);
    }

    @Override
    public byte[] serialize(String arg0, DmsWrapper arg1) {
        byte[] retVal = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            retVal = objectMapper.writeValueAsString(arg1).getBytes();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retVal;
    }

    @Override
    public byte[] serialize(String topic, Headers headers, DmsWrapper data) {
        return Serializer.super.serialize(topic, headers, data);
    }

    @Override
    public void close() {
        Serializer.super.close();
    }

    @Override
    public DmsWrapper deserialize(String arg0, byte[] arg1) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(arg1, DmsWrapper.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public DmsWrapper deserialize(String topic, Headers headers, byte[] data) {
        return Deserializer.super.deserialize(topic, headers, data);
    }
}
