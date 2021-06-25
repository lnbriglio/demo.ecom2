package com.calamar.ecom2;

import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.processor.api.Record;

// Processor that keeps the global store updated.
public class GlobalStoreUpdater<K, V, KIn, KOut> implements Processor<K, V, KIn, KOut> {

    private final String storeName;

    public GlobalStoreUpdater(final String storeName) {
        this.storeName = storeName;
    }

    private KeyValueStore<K, V> store;

    @Override
    public void init(
            final org.apache.kafka.streams.processor.api.ProcessorContext<KIn, KOut> processorContext) {
        store = processorContext.getStateStore(storeName);
    }

    @Override
    public void process(final Record<K, V> record) {
        // We are only supposed to put operation the keep the store updated.
        // We should not filter record or modify the key or value
        // Doing so would break fault-tolerance.
        // see https://issues.apache.org/jira/browse/KAFKA-7663
        store.put(record.key(), record.value());
    }

    @Override
    public void close() {
        // No-op
    }

}