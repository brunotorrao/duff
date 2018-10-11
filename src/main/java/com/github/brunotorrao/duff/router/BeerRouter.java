package com.github.brunotorrao.duff.router;

import com.github.brunotorrao.duff.handler.BeerHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;

import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class BeerRouter {
    
    @Bean
    public RouterFunction routerFunction(BeerHandler beerHandler) {
        return
            nest(
                path("/beers"),
                route(GET(""), request -> beerHandler.findAll())
                    .andRoute(POST("/"), beerHandler::save)
                    .andRoute(PUT("/{id}"), beerHandler::update)
                    .andRoute(GET("/{id}"), beerHandler::findById)
                    .andRoute(DELETE("/{id}"), beerHandler::remove)
                    .andRoute(GET("/temperatures/suggest"), beerHandler::findBestByTemperature)
                    
            );
    }
}
