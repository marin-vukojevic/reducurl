package com.decodetamination.reducurl;

import com.datastax.oss.driver.api.core.ConsistencyLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.cassandra.core.EntityWriteResult;
import org.springframework.data.cassandra.core.InsertOptions;
import org.springframework.data.cassandra.core.ReactiveCassandraOperations;
import org.springframework.data.cassandra.core.cql.QueryOptions;
import org.springframework.data.cassandra.core.query.Criteria;
import org.springframework.data.cassandra.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class ReducedUrlRepository {

    private final ReactiveCassandraOperations cassandraOperations;

    @Value("${reducurl.url-ttl-in-days}")
    private long ttlInDays;

    public Mono<Boolean> exists(String id) {
        return cassandraOperations.select(
                        Query.query(Criteria.where("id").is(id))
                                .queryOptions(QueryOptions.builder()
                                        .consistencyLevel(ConsistencyLevel.ONE)
                                        .build()),
                        ReducedUrl.class)
                .hasElements();
    }

    public Mono<ReducedUrl> insert(ReducedUrl reducedUrl) {
        return cassandraOperations.insert(
                        reducedUrl,
                        InsertOptions.builder().consistencyLevel(ConsistencyLevel.ALL).ttl(Duration.ofDays(ttlInDays)).build())
                .map(EntityWriteResult::getEntity);
    }

    public Mono<ReducedUrl> findById(String id) {
        return cassandraOperations.select(
                Query.query(Criteria.where("id").is(id))
                        .queryOptions(QueryOptions.builder()
                                .consistencyLevel(ConsistencyLevel.ONE)
                                .build()),
                ReducedUrl.class)
                .next();
    }
}
