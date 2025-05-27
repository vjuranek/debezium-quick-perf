package io.debezium.perf;

import io.debezium.embedded.Connect;
import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import org.apache.kafka.connect.source.SourceRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DebeziumEngniePostgres {
    private static final Logger LOGGER = LoggerFactory.getLogger(DebeziumEngniePostgres.class);

    public static void main(String[] args) {
        final Properties props = new Properties();
        props.setProperty("name", "engine");
        props.setProperty("connector.class", "io.debezium.connector.postgresql.PostgresConnector");
        props.setProperty("plugin.name", "pgoutput");
        props.setProperty("database.hostname", "127.0.0.1");
        props.setProperty("database.port", "5432");
        props.setProperty("database.user", "postgres");
        props.setProperty("database.password", "postgres");
        props.setProperty("database.dbname", "postgres");
        props.setProperty("topic.prefix", "perf");
        props.setProperty("table.include.list", "public.pgbench_.*");
        props.setProperty("snapshot.mode", "no_data");
        props.setProperty("offset.storage", "org.apache.kafka.connect.storage.FileOffsetBackingStore");
        props.setProperty("offset.storage.file.filename", "./data/offsets.dat");
        props.setProperty("offset.flush.interval.ms", "60000");
        props.setProperty("schema.history.internal", "io.debezium.storage.file.history.FileSchemaHistory");
        props.setProperty("schema.history.internal.file.filename", "./data/schemahistory.dat");

        try (DebeziumEngine<ChangeEvent<SourceRecord, SourceRecord>> engine = DebeziumEngine.create(Connect.class)
                .using(props)
                .notifying(record -> {
                }).build()
        ) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(engine);
            System.out.println("Debezium started");
            Thread.sleep(600000);
        }
        catch (IOException|InterruptedException e) {
            System.out.println("Failed with " + e);
        }
        System.out.println("Debezium stopped");
    }
}
