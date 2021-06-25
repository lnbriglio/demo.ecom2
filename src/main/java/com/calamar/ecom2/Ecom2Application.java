package com.calamar.ecom2;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.TopologyDescription;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.ProcessorSupplier;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.StreamsBuilderFactoryBeanCustomizer;
import org.springframework.context.annotation.Bean;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

@SpringBootApplication
public class Ecom2Application {

    public static void main(String[] args) {
        SpringApplication.run(Ecom2Application.class, args);
    }

    // Branch a source event into multiple streams (topics)
    @Bean
    public Function<KStream<String, Order>, KStream<String, Order>[]> orderPriceSplitter() {
        return orders -> orders
                .branch((key, order) -> order.price > 500,
                        (key, order) -> order.price <= 500);
    }

    @Bean
    public Function<KStream<String, Order>, KStream<String, Order>> notifyCheapOrders() {
        return orders -> orders
                .peek((key, order) -> System.out.println(String.format(">) Cheap Order -> %s went for %s", order.id, order.price)));
    }

    @Bean
    public Function<KStream<String, Order>, KStream<String, Order>> notifyExpensiveOrders() {
        return orders -> orders
                .peek((key, order) -> System.out.println(String.format(">) Expensive Order -> %s went for %s", order.id, order.price)));
    }

    // HANDLE the same topic events through different functions
    @Bean
    public Function<KStream<String, Order>, KStream<String, Order>> pendingOrders() {
        return orders -> orders
                .filter((key, order) -> order.status.equalsIgnoreCase("Pending"))
                .peek((key, order) -> System.out.println(String.format("A) Order -> %s is pending", order.id)));
    }

    @Bean
    public Function<KStream<String, Order>, KStream<String, Order>> completedOrders() {
        return orders -> orders
                .filter((key, order) -> order.status.equalsIgnoreCase("Completed"))
                .peek((key, order) -> System.out.println(String.format("B) Order -> %s is completed", order.id)));
    }

    // JOIN TABLES
    @Bean
    public BiFunction<KTable<String, Product>, KTable<String, Vendor>, KStream<String, String>> enrichProducts() {
        return (products, vendors) -> products
                // Join products with vendors, taking the KEY from the vendor event and choosing a matching property from the product
                .join(vendors, product -> product.vendorId,
                        // In case there is a match we will have a product and vendor
                        (product, vendor) -> {
                            // At this point we simple 'enrich' the product entity and return it to continue with our processing
                            product.vendorName = vendor.name;
                            return product;
                        }
                )
                .toStream()
                .peek((key, product) ->
                        System.out.println(
                                String.format("<<<GOT THIS PRODUCT %s WITH PRICE %s VENDOR IS %s", product.id, product.price, product.vendorName)))
                // This would be the output value for our stream
                .mapValues(product -> "Test");
    }

    @Bean
    public StreamsBuilderFactoryBeanCustomizer customizer() {
        return fb -> {
            try {
                final StreamsBuilder streamsBuilder = fb.getObject();
                if (streamsBuilder != null) {
                    streamsBuilder.addGlobalStore(
                            Stores.keyValueStoreBuilder(Stores.persistentKeyValueStore("CUSTOMER_STORE"), Serdes.Long(), Serdes.String()),
                            "CUSTOMER_TOPIC",
                            Consumed.with(Serdes.Long(), Serdes.String()),
                            () -> new GlobalStoreUpdater<>("CUSTOMER_STORE"));
                }
            } catch (Exception e) {

            }
        };
    }

    @Bean
    public Function<KStream<String, String>, KStream<String, String>> p3() {
        return x -> {
            x.process(() -> new Processor<String, String>() {
                @Override
                public void init(ProcessorContext processorContext) {


                }

                @Override
                public void process(String s, String s2) {

                }

                @Override
                public void close() {

                }
            });

            return x;
        };

    }

    @Bean
    public BiFunction<KStream<String, Product>, GlobalKTable<String, Vendor>, KStream<String, String>> enrichProducts2() {
        return (products, vendors) -> products
                // Join products with vendors, taking the KEY from the vendor event and choosing a matching property from the product
                .join(vendors, (k, product) -> product.vendorId,
                        // In case there is a match we will have a product and vendor
                        (product, vendor) -> {
                            // At this point we simple 'enrich' the product entity and return it to continue with our processing
                            product.vendorName = vendor.name;
                            return product;
                        }
                )
//                .toStream()
                .peek((key, product) ->
                        System.out.println(
                                String.format("<<<GOT THIS PRODUCT %s WITH PRICE %s VENDOR IS %s", product.id, product.price, product.vendorName)))
                // This would be the output value for our stream
                .mapValues(product -> "Test");
    }
}

