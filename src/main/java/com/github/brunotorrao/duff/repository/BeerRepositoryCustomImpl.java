package com.github.brunotorrao.duff.repository;

import com.github.brunotorrao.duff.domain.Beer;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators;
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators.Add;
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators.Divide;
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators.Subtract;
import reactor.core.publisher.Mono;

import static org.springframework.data.domain.Sort.by;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.aggregation.ArithmeticOperators.Abs.absoluteValueOf;

public class BeerRepositoryCustomImpl implements BeerRepositoryCustom {
    
    private ReactiveMongoTemplate mongoTemplate;
    
    public BeerRepositoryCustomImpl(ReactiveMongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
    
    @Override
    public Mono<Beer> findBestByTemperature(Integer temperature) {
    
        Aggregation aggregation = newAggregation(
            project("_id", "style", "idealTemperature")
                .and(Divide.valueOf(Add.valueOf("$idealTemperature.min").add("$idealTemperature.max")).divideBy(2)).as("avg"),
            project("_id", "style", "idealTemperature", "avg")
                .and(absoluteValueOf(Subtract.valueOf(temperature).subtract("$avg"))).as("diff"),
            sort(by("diff", "style")),
            limit(1L)
        );
        
        return mongoTemplate.aggregate(aggregation, "beers", Beer.class).elementAt(0);
    }
}
