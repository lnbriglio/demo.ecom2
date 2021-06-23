package com.calamar.ecom2;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class SerializerTests {


    @Test
    public void should_serialize() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.CUSTOM);
        mapper.registerModule(new Jdk8Module());


        Contract c = new Contract();
        c.setId(15);
        c.text = Optional.of("hey");
        c.subtext = Optional.ofNullable(null);
        c.footer = null;
        Person p = new Person();
        p.id = Optional.of(10);
        p.name = Optional.of("John Rambo");

        c.person = Optional.of(p);

        String serialized = mapper.writeValueAsString(c);

        assertThat(serialized).isEqualTo("{\"id\":15,\"text\":\"hey\",\"subtext\":null,\"person\":{\"id\":10,\"name\":\"John Rambo\"}}");
    }

    @Test
    public void should_deserialize() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module());

        String serializedContract = "{\"id\":15,\"text\":\"hey\",\"subtext\":null,\"person\":{\"id\":10,\"name\":\"John Rambo\"}}";

        Contract contract = mapper.readValue(serializedContract, Contract.class);


        assertThat(contract.getId()).isEqualTo(Optional.of(15));
        assertThat(contract.footer).isNull();
        assertThat(contract.text).isEqualTo(Optional.of("hey"));
        assertThat(contract.subtext).isEqualTo(Optional.ofNullable(null));
        assertThat(contract.person.get().id).isEqualTo(Optional.of(10));
        assertThat(contract.person.get().name).isEqualTo(Optional.of("John Rambo"));
    }

    @Test
    public void should_deserialize_dms_wrapper() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module());

        // I want to read this json with the F_1234 fieldname as 'name'
        String serializedContract = "{\"metadata\":\"Version final\",\"data\":{ \"id\":100, \"F_1234\":\"John Rambo\"}}";

        DmsWrapper dmsEvent = mapper.readValue(serializedContract, DmsWrapper.class);

        DistributionCenter data = mapper.convertValue(dmsEvent.data, DistributionCenter.class);

        assertThat(dmsEvent.metadata).isEqualTo("Version final");
        assertThat(data.id).isEqualTo(Optional.of(100));
        assertThat(data.getName()).isEqualTo(Optional.of("John Rambo"));

        // Afterwards if I serialized this again, I want to keep the 'name' property name
        String serializedData = mapper.writeValueAsString(data);
        assertThat(serializedData).isEqualTo("{\"id\":100,\"name\":\"John Rambo\"}");
    }
}
