package com.github.brunotorrao.duff.repository;

import com.github.brunotorrao.duff.domain.Beer;
import reactor.core.publisher.Mono;

public interface BeerRepositoryCustom {
    
    Mono<Beer> findBestByTemperature(Integer temperature);
}
