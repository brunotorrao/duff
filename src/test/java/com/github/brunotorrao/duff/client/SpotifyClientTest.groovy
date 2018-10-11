package com.github.brunotorrao.duff.client

import com.github.brunotorrao.duff.Application
import com.github.brunotorrao.duff.domain.Playlist
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootContextLoader
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(classes = Application, webEnvironment = RANDOM_PORT)
@ContextConfiguration(loader = SpringBootContextLoader, classes = Application)
@ActiveProfiles("test")
class SpotifyClientTest extends Specification {

    @Autowired
    private SpotifyClient spotifyClient

    def 'should retrieve playlist given name'() {
        given:
            def name = "IPA"
        when:
            Mono<Playlist> playlist = spotifyClient.getPlaylistByNameLike(name)
        then:
            StepVerifier.create(playlist).assertNext {
                assert it.name == "IPA Rock - Wood and Beer"
                assert it.tracks.find { track ->
                    track.name == 'Highway to Hell' &&
                    track.artist == "AC/DC" &&
                    track.link == "https://api.spotify.com/v1/tracks/2zYzyRzz6pRmhPzyfMEC8s"
                }
            }
            .verifyComplete()
    }
}
