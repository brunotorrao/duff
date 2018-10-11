package com.github.brunotorrao.duff.config;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

import static com.wrapper.spotify.SpotifyApi.builder;

@Slf4j
@Configuration
@ConfigurationProperties(prefix = "spotify.auth")
public class SpotifyConfig {
    
    private String clientId = "45dbf33cbb904a1cb8f8606de1b444e4";
    private String clientSecret = "4f8b8efd316d4b5fb3348eb6e530d29d";
    
    @Bean
    SpotifyApi spotifyApi() {
        var spotifyApi = builder()
            .setClientId(clientId)
            .setClientSecret(clientSecret)
            .build();
        fillAccessToken(spotifyApi);
    
        return spotifyApi;
    }
    
    private void fillAccessToken(SpotifyApi spotifyApi) {
        try {
            spotifyApi.setAccessToken(spotifyApi.clientCredentials().build().execute().getAccessToken());
        } catch (IOException | SpotifyWebApiException e) {
            log.error("could not retrieve spotify authorization");
        }
    }
}
