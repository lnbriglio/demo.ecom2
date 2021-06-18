package com.calamar.ecom2;

import org.apache.kafka.streams.kstream.Joined;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

@SpringBootApplication
public class Ecom2Application {

    public static void main(String[] args) {
        SpringApplication.run(Ecom2Application.class, args);
    }

//	@Bean
//	public Function<KStream<Object, WorkOrder>, KStream<Object, WorkOrder>> johnyConsumer(){
//		return stream -> stream
//				.peek((key, workOrder) ->
//						System.out.println("Processing order: " + workOrder.id))
//				.mapValues((key, workOrder) -> {
//					WorkOrder modified = new WorkOrder();
//					modified.id = workOrder.id;
//					modified.status = "Processed";
//
//					return modified;
//				});
//	}
//
//	@Bean
//	public Function<KStream<Object, WorkOrder>, KStream<Object, String>> notifyProcessed(){
//		return stream -> stream
//				.peek((key, workOrder) ->
//						System.out.println("Notifying on order: " + workOrder + " Your order has been processed"))
//				.mapValues(workOrder -> "Te procesamo la orden viteh " + workOrder.id)
//				;
//	}

//    @Bean
//    public BiFunction<KTable<String, Product>, KTable<String, Vendor>, KStream<String, String>> enrichProducts() {
//        return (products, vendors) -> products
//                .leftJoin(vendors,
//                        (product, vendor) -> {
//                            product.vendorName = vendor != null ? vendor.name : null;
//                            return product;
//                        }
//                )
//                .toStream()
//                .peek((key, product) ->
//                        System.out.println(String.format("<<<GOT THIS PRODUCT %s WITH PRICE %s VENDOR IS %s", product.id, product.price, product.vendorName)))
//                .mapValues(product -> "Test")
//                ;
//    }

    @Bean
    public BiFunction<KTable<String, Product>, KTable<String, Vendor>, KStream<String, String>> enrichProducts() {
        return (products, vendors) -> products
                .join(vendors, p -> p.vendorId,
                        (p, v) -> {
                            p.vendorName = v.name;
                            return p;
                        }
                )
                .toStream()
                .peek((key, product) ->
                        System.out.println(String.format("<<<GOT THIS PRODUCT %s WITH PRICE %s VENDOR IS %s", product.id, product.price, product.vendorName)))
                .mapValues(product -> "Test")
                ;
    }
}

