package com.github.brunotorrao.duff;

import com.github.brunotorrao.duff.domain.Beer;
import com.github.brunotorrao.duff.domain.Temperature;
import com.github.brunotorrao.duff.repository.BeerRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.util.List;

import static java.math.BigDecimal.valueOf;

@SpringBootApplication
public class Application {
    
    public Application(BeerRepository repository) {
        this.repository = repository;
    }
    
    public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	private final BeerRepository repository;

    @PostConstruct
    public void initializeDataBase() {
	    
	    var beers = List.of(
            new Beer("Weissbier", new Temperature(valueOf(-1), valueOf(3))),
            new Beer("Pilsens",	new Temperature(valueOf(-2), valueOf(4))),
            new Beer("Weizenbier",	new Temperature(valueOf(-4), valueOf(6))),
            new Beer("Red ale",	new Temperature(valueOf(-5), valueOf(5))),
            new Beer("India pale ale",	new Temperature(valueOf(-6), valueOf(7))),
            new Beer("IPA",	new Temperature(valueOf(-7), valueOf(10))),
            new Beer("Dunkel", new Temperature(valueOf(-8), valueOf(2))),
            new Beer("Imperial Stouts",	new Temperature(valueOf(-10), valueOf(13))),
            new Beer("Brown ale",	new Temperature(valueOf(0), valueOf(14)))
        );
	    
	    var saveFlux = Flux.fromIterable(beers)
            .flatMap(repository::save);
	    
	    repository
            .deleteAll()
            .thenMany(saveFlux)
            .doOnNext(System.out::println)
            .subscribe();
    }
}
