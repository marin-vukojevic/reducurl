package com.decodetamination.reducurl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ReducedUrlService {

    private final ReducedUrlRepository reducedUrlRepository;

    @ResponseBody
    public Mono<ReducedUrl> createReducedUrl(@RequestBody String originalUrl) {
        // not production ready implementation obviously with random generation and check
        // happening at the time when url is reduced but its good enough for demonstration purpose
        return Flux.<String>generate(it -> it.next(RandomStringUtils.randomAlphanumeric(8)))
                .filterWhen(id -> reducedUrlRepository.exists(id).map(it -> !it))
                .take(1)
                .single()
                .map(id -> new ReducedUrl(id, originalUrl))
                .flatMap(reducedUrlRepository::insert);
    }

    public Mono<ReducedUrl> getReducedUrl(@PathVariable String id) {
        return reducedUrlRepository.findById(id);
    }
}
