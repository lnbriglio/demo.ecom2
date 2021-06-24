package com.calamar.ecom2;

import com.calamar.ecom2.serializers.DmsSerde;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.Stores;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.function.BiFunction;
import java.util.function.Function;

@SpringBootApplication
public class Ecom2Application {

    public static void main(String[] args) {
        SpringApplication.run(Ecom2Application.class, args);
    }

    // Branch a source event into multiple streams (topics)
//    @Bean
//    public Function<KStream<String, Order>, KStream<String, Order>[]> orderPriceSplitter() {
//        return orders -> orders
//                .branch((key, order) -> order.price > 500,
//                        (key, order) -> order.price <= 500);
//    }
//
//    @Bean
//    public Function<KStream<String, Order>, KStream<String, Order>> notifyCheapOrders() {
//        return orders -> orders
//                .peek((key, order) -> System.out.println(String.format(">) Cheap Order -> %s went for %s", order.id, order.price)));
//    }
//
//    @Bean
//    public Function<KStream<String, Order>, KStream<String, Order>> notifyExpensiveOrders() {
//        return orders -> orders
//                .peek((key, order) -> System.out.println(String.format(">) Expensive Order -> %s went for %s", order.id, order.price)));
//    }
//
//    // HANDLE the same topic events through different functions
//    @Bean
//    public Function<KStream<String, Order>, KStream<String, Order>> pendingOrders() {
//        return orders -> orders
//                .filter((key, order) -> order.status.equalsIgnoreCase("Pending"))
//                .peek((key, order) -> System.out.println(String.format("A) Order -> %s is pending", order.id)));
//    }
//
//    @Bean
//    public Function<KStream<String, Order>, KStream<String, Order>> completedOrders() {
//        return orders -> orders
//                .filter((key, order) -> order.status.equalsIgnoreCase("Completed"))
//                .peek((key, order) -> System.out.println(String.format("B) Order -> %s is completed", order.id)));
//    }
//
//    // JOIN TABLES
//    @Bean
//    public BiFunction<KTable<String, Product>, KTable<String, Vendor>, KStream<String, String>> enrichProducts() {
//        return (products, vendors) -> products
//                // Join products with vendors, taking the KEY from the vendor event and choosing a matching property from the product
//                .join(vendors, product -> product.vendorId,
//                        // In case there is a match we will have a product and vendor
//                        (product, vendor) -> {
//                            // At this point we simple 'enrich' the product entity and return it to continue with our processing
//                            product.vendorName = vendor.name;
//                            return product;
//                        }
//                )
//                .toStream()
//                .peek((key, product) ->
//                        System.out.println(
//                                String.format("<<<GOT THIS PRODUCT %s WITH PRICE %s VENDOR IS %s", product.id, product.price, product.vendorName)))
//                // This would be the output value for our stream
//                .mapValues(product -> "Test");
//    }

    // Ktaibol
    @Bean
    public Function<KStream<String, DmsWrapper>, KStream<String, String>> sameSourceJoin() {
        return events -> {

            KTable<String, DmsWrapper> t1 = events
                    .filter((k, v) -> v.id < 50)
                    .toTable(Materialized.<String, DmsWrapper>as(Stores.persistentKeyValueStore("pepinillo"))
                            .withKeySerde(Serdes.String())
                            .withValueSerde(new DmsSerde()));

            KTable<String, DmsWrapper> t2 = events
                    .filter((k, v) -> v.id >= 50)
                    .toTable(Materialized.<String, DmsWrapper>as(Stores.persistentKeyValueStore("cuqui"))
                            .withKeySerde(Serdes.String())
                            .withValueSerde(new DmsSerde()));

            return t1.join(t2,
                    i -> String.valueOf(i.id),
                    (x, y) -> {
                        x.id += y.id;
                        ObjectMapper mapper = new ObjectMapper();
                        Customer cx = mapper.convertValue(x.data, Customer.class);
                        Customer cy = mapper.convertValue(y.data, Customer.class);
                        return cx.name + " " + cy.name;
                    },
                    Named.as("piruli")
            )
                    .toStream()
                    .peek((k, v) -> {
                        System.out.println(String.format("I was able to join this neng %s %s", k, v));
                    });
        };
    }
}

