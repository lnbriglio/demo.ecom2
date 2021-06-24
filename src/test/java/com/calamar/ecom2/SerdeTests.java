package com.calamar.ecom2;

import com.calamar.ecom2.serializers.DmsSerde;
import com.calamar.ecom2.serializers.LeanSerde;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.KeyValueStore;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class SerdeTests {


    @Test
    public void should_serialize_and_deserialize() {
        DmsWrapper event = new DmsWrapper();
        event.id = 45;
        event.data = new LinkedHashMap();
        event.data.put("name", "John Rambo");

        event.metadata = "Meta meta";

        LeanSerde mySerde = new LeanSerde();

        byte[] serialized = mySerde.serialize("myTopic", event);

        DmsWrapper deserialized = mySerde.deserialize("myTopic", serialized);

        assertThat(deserialized.id).isEqualTo(45);
    }

    @Test
    public void should_create_state_store() {
        Materialized.<String, DmsWrapper, KeyValueStore<String, byte[]>>as("pepinillo")
                .withValueSerde(new DmsSerde());
    }
}
