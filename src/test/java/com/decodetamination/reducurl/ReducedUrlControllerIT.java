package com.decodetamination.reducurl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class ReducedUrlControllerIT {

    private static final String TEST_URL = "http://www.test.com";

    @Container
    @ServiceConnection
    static CassandraContainer<?> cassandraContainer = new CassandraContainer<>("cassandra:4.1.2");

    @Autowired
    ReducedUrlController underTest;

    @Value("${reducurl.base-url}")
    String baseUrl;

    WebClient webClient;

    @BeforeEach
    public void init() {
        webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    @Test
    void createAndGetReducedUrl() {
        Mono<ResponseEntity<Void>> createAndGet =
                webClient.post()
                        .bodyValue(TEST_URL)
                        .retrieve()
                        .bodyToMono(String.class)
                        .flatMap(it -> webClient.get()
                                .uri(URI.create(it))
                                .retrieve()
                                .toBodilessEntity());

        StepVerifier.create(createAndGet)
                .assertNext(it -> assertThat(it).satisfies(
                        response -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SEE_OTHER),
                        response -> assertThat(response.getHeaders().get(HttpHeaders.LOCATION)).contains(TEST_URL)))
                .expectComplete()
                .verify();
    }
}
