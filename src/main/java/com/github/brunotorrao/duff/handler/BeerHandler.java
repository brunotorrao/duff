package com.github.brunotorrao.duff.handler;

import com.github.brunotorrao.duff.client.SpotifyClient;
import com.github.brunotorrao.duff.domain.Beer;
import com.github.brunotorrao.duff.domain.BeerSuggestion;
import com.github.brunotorrao.duff.repository.BeerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static java.net.URI.create;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.web.reactive.function.BodyInserters.fromObject;
import static org.springframework.web.reactive.function.server.ServerResponse.badRequest;
import static org.springframework.web.reactive.function.server.ServerResponse.created;
import static org.springframework.web.reactive.function.server.ServerResponse.noContent;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;
import static org.springframework.web.reactive.function.server.ServerResponse.status;

@Component
@Slf4j
public class BeerHandler {
    
    private BeerRepository repository;
    private SpotifyClient spotifyClient;
    
    public BeerHandler(BeerRepository repository, SpotifyClient spotifyClient){
        this.repository = repository;
        this.spotifyClient = spotifyClient;
    }
    
    public Mono<ServerResponse> findAll() {
        return ok().body(repository.findAll(), Beer.class);
    }
    
    public Mono<ServerResponse> findById(ServerRequest request) {
        return ok().body(repository.findById(request.pathVariable("id")), Beer.class);
    }
    
    public Mono<ServerResponse> save(ServerRequest request) {
        return request.bodyToMono(Beer.class)
            .doOnNext(beer1 -> beer1.setId(null))
            .flatMap(repository::save)
            .flatMap(beer -> created(create("/beers/"+beer.getId())).body(fromObject(beer)))
            .onErrorResume(DuplicateKeyException.class, error -> status(CONFLICT).body(fromObject("Beer already exists!")));
    }
    
    public Mono<ServerResponse> update(ServerRequest request) {
        return request
            .bodyToMono(Beer.class)
            .doOnNext(beer -> beer.setId(request.pathVariable("id")))
            .flatMap(repository::save)
            .flatMap(beer -> ok().body(fromObject(beer)));
    }
    
    public Mono<ServerResponse> remove(ServerRequest request) {
        return repository.deleteById(request.pathVariable("id"))
            .then(noContent().build());
    }
    
    public Mono<ServerResponse> findBestByTemperature(ServerRequest request) {
        Optional<String> opTemperature = request.queryParam("temperature");
        if (opTemperature.isPresent()) {
            return Mono.just(opTemperature.get())
                .map(Integer::parseInt)
                .flatMap(temperature -> repository.findBestByTemperature(temperature))
                .map(Beer::getStyle)
                .flatMap(this::getSuggestion)
                .flatMap(beer -> ok().body(fromObject(beer)));
        } else {
            return badRequest().body(fromObject("Temperature is required!"));
        }
    }
    
    private Mono<BeerSuggestion> getSuggestion(String style) {
        return spotifyClient.getPlaylistByNameLike(style)
            .map(playlist -> new BeerSuggestion(style, playlist));
    }
}
