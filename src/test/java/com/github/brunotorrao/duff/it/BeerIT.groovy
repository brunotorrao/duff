package com.github.brunotorrao.duff.it

import com.github.brunotorrao.duff.Application
import com.github.brunotorrao.duff.domain.Beer
import com.github.brunotorrao.duff.domain.Temperature
import com.github.brunotorrao.duff.repository.BeerRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootContextLoader
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.test.StepVerifier
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import static org.springframework.web.reactive.function.BodyInserters.fromObject

@SpringBootTest(classes = Application, webEnvironment = RANDOM_PORT)
@ContextConfiguration(loader = SpringBootContextLoader, classes = Application)
@ActiveProfiles("test")
@Stepwise
class BeerIT extends Specification {

    @Autowired
    private WebTestClient webTestClient

    @Autowired
    private BeerRepository repository

    @Shared
    private Beer beer

    def 'should create Beer'() {
        given:
            beer = new Beer(style: 'IPA', idealTemperature: new Temperature(-2, 2))
        when:
            def exchange = webTestClient.post()
                .uri("/beers")
                .body(fromObject(beer))
                .exchange()
        then:
            exchange.expectStatus().isCreated()
            exchange.returnResult(Beer).responseBody.doOnNext {
                beer = it
            }.blockFirst()
            StepVerifier.create(repository.findById(beer.id))
                .expectNext(beer)
                .verifyComplete()
    }

    def 'should update Beer'() {
        when:
            def exchange = webTestClient.put()
                .uri("/beers/$beer.id")
                .body(fromObject(beer.with { idealTemperature.min = -3; it }))
                .exchange()
        then:
            exchange
                .expectStatus().isOk()
                .expectBody().jsonPath("\$.idealTemperature.min").isEqualTo(-3)
    }

    def 'should find all'() {
        when:
            WebTestClient.ResponseSpec exchange = webTestClient.get()
                .uri("/beers")
                .exchange()
        then:
            exchange.expectBody()
                .jsonPath("\$[0].style").isEqualTo("IPA")
                .jsonPath("\$[0].idealTemperature.min").isEqualTo(-3)
                .jsonPath("\$[0].idealTemperature.max").isEqualTo(2)
    }

    def 'should find by id'() {
        when:
            WebTestClient.ResponseSpec exchange = webTestClient.get()
                .uri("/beers/$beer.id")
                .exchange()
        then:
            exchange.expectBody()
                .jsonPath("\$.style").isEqualTo("IPA")
                .jsonPath("\$.idealTemperature.min").isEqualTo(-3)
                .jsonPath("\$.idealTemperature.max").isEqualTo(2)
    }

    def 'should best beer by temperature and provide a playlist'() {
        when:
            WebTestClient.ResponseSpec exchange = webTestClient.get()
                .uri("/beers/temperatures/suggest?temperature=0")
                .exchange()
        then:
            exchange.expectBody()
                .jsonPath("\$.beerStyle").isEqualTo("IPA")
                .jsonPath("\$.playlist.name").isEqualTo("IPA Rock - Wood and Beer")
    }

    def 'should remove beer by id'() {
        when:
            WebTestClient.ResponseSpec exchange = webTestClient.delete()
                .uri("/beers/$beer.id")
                .exchange()
        then:
            exchange
                .expectStatus().isNoContent()
            !repository.findById(beer.id).blockOptional().isPresent()

    }
}
