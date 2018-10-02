package com.github.brunotorrao.duff.repository;

import com.github.brunotorrao.duff.domain.Beer;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface BeerRepository extends ReactiveMongoRepository<Beer, String> {

}
