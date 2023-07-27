package com.decodetamination.reducurl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.RedirectView;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class ReducedUrlController {

    private final ReducedUrlRepository reducedUrlRepository;
    private final ReducedUrlService reducedUrlService;

    @Value("${reducurl.base-url}")
    private String baseUrl;

    @PostMapping
    @ResponseBody
    public Mono<String> createReducedUrl(@RequestBody String url) {
        return reducedUrlService.createReducedUrl(url)
                .map(it -> baseUrl + it.id());
    }

    @GetMapping("{id}")
    public Mono<RedirectView> getReducedUrl(@PathVariable String id) {
        return reducedUrlService.getReducedUrl(id)
                .map(this::createRedirect);
    }

    private RedirectView createRedirect(ReducedUrl reducedUrl) {
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(reducedUrl.url());
        return redirectView;
    }
}
