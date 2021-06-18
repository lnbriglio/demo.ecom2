# Spring Cloud Streams - Join KTables using functional style

### Prerequisites

- `Java 16` This is not a requirement for the solution, this just happen to be what I had during the setup
- `Docker` Required to run local instances of kafka provided in `support/docker-compose.yml`

### Key elements

`application.yml` the chosen binding style leverages configuration from this file, in here we define the input and
output topics and which function should take care of handling that (among other things)

Ref: https://docs.spring.io/spring-cloud-stream-binder-kafka/docs/3.0.10.RELEASE/reference/html/spring-cloud-stream-binder-kafka.html#_multiple_input_bindings

### Overall flow and expectation of this code

The provided function will listen to 2 topics (`products` and `vendors`) it's preferrable to have those topics created
before lunching the spring application.

Then we would want to send events to those topics with the intention of obtaining enriched products (products that
include the vendor name as an output).

Another requirement would be that if we send products before the vendors, those products wouldn't be enriched since no
vendor data is available, but after we produce the vendor events, previously processed products should be re-evaluated
for enrichment, and assuming the join condition matches we would then obtain enriched product events.

### Sample data:

Should send individual objects and use the key property as the EVENT KEY and serialize the value as a JSON string for
the EVENT VALUE

```javascript
products = [
    {'key': 1, 'value': {'id': 1234, 'price': 200, 'vendorId': 1}},
    {'key': 555, 'value': {'id': 555, 'price': 600, 'vendorId': 1}},
    {'key': 2, 'value': {'id': 111, 'price': 9, 'vendorId': 2}},
    {'key': 222, 'value': {'id': 222, 'price': 1000, 'vendorId': 2}}
]
```

```javascript
vendors = [
    {'key': 1, 'value': {'id': 1, 'name': 'MegaCorp'}},
    {'key': 2, 'value': {'id': 2, 'name': 'DualProductions'}}
]
```

https://www.ru-rocker.com/2020/08/11/how-to-join-one-to-many-and-many-to-one-relationship-with-kafka-streams/

https://docs.spring.io/spring-cloud-stream-binder-kafka/docs/3.0.10.RELEASE/reference/html/spring-cloud-stream-binder-kafka.html#_multiple_input_bindings

https://www.vinsguru.com/kafka-stream-with-spring-boot/