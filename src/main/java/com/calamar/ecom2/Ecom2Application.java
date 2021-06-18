package com.calamar.ecom2;

import org.apache.kafka.streams.kstream.Joined;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.function.BiFunction;

@SpringBootApplication
public class Ecom2Application {

    public static void main(String[] args) {
        SpringApplication.run(Ecom2Application.class, args);
    }

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
                        System.out.println(String.format("<<<GOT THIS PRODUCT %s WITH PRICE %s VENDOR IS %s", product.id, product.price, product.vendorName)))
                // This would be the output value for our stream
                .mapValues(product -> "Test");
    }
}

