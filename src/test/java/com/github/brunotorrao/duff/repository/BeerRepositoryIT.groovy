package com.github.brunotorrao.duff.repository

import com.github.brunotorrao.duff.Application
import com.github.brunotorrao.duff.domain.Beer
import com.github.brunotorrao.duff.domain.Temperature
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootContextLoader
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import reactor.core.publisher.Flux
import spock.lang.Specification
import spock.lang.Unroll

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(classes = Application, webEnvironment = RANDOM_PORT)
@ContextConfiguration(loader = SpringBootContextLoader, classes = Application)
@ActiveProfiles("test")
class BeerRepositoryIT extends Specification{

    @Autowired
    private BeerRepository repository

    void setup() {
        def fluxSave = Flux.fromIterable([
            new Beer(style: "beer1", idealTemperature: new Temperature(-2, 2)),
            new Beer(style: "beer2", idealTemperature: new Temperature(-3, 5)),
            new Beer(style: "beer3", idealTemperature: new Temperature(1, 5)),
            new Beer(style: "beer4", idealTemperature: new Temperature(2, 6))
        ])
        .flatMap { repository.save(it) }
        .collectList()

        repository.deleteAll()
            .then(fluxSave)
            .block()

    }

    @Unroll
    def 'should return #style when request suggestion for temperature #temperature'() {
        expect:
            repository.findBestByTemperature(temperature).block().style == style
        where:
            temperature                     |   style
            0                               | "beer1"
            2                               | "beer2"
            3                               | "beer3"
            4                               | "beer4"
    }
}
