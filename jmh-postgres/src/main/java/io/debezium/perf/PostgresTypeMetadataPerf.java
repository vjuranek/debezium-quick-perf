package io.debezium.performance.connector.postgres;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import io.debezium.connector.postgresql.PostgresStreamingChangeEventSource;
import io.debezium.connector.postgresql.PostgresType;
import io.debezium.connector.postgresql.connection.AbstractReplicationMessageColumn;
import io.debezium.connector.postgresql.connection.ReplicationMessage;

/**
 *
 * @author vjuranek
 */
public class PostgresTypeMetadataPerf {
    @State(Scope.Thread)
    public static class ColumnState {

        public ReplicationMessage.Column column;

        @Setup(Level.Invocation)
        public void doSetup() {
            column = testColumn();
        }

        private ReplicationMessage.Column testColumn() {
            String columnName = "test";
            PostgresType columnType = PostgresType.UNKNOWN;
            String typeWithModifiers = "character varying(255)";
            boolean optional = true;
            return new AbstractReplicationMessageColumn(columnName, columnType, typeWithModifiers, optional) {
                @Override
                public Object getValue(PostgresStreamingChangeEventSource.PgConnectionSupplier connection, boolean includeUnknownDatatypes) {
                    return null;
                }
            };
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    @Fork(value = 1)
    @Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 10, time = 2, timeUnit = TimeUnit.SECONDS)
    public void columnMetadata(Blackhole bh, ColumnState state) {
        bh.consume(state.column.getTypeMetadata());
    }
}
