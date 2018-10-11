package com.github.brunotorrao.duff.client;

import com.github.brunotorrao.duff.domain.Playlist;
import com.github.brunotorrao.duff.domain.Track;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

@Slf4j
@Component
public class SpotifyClient {
    
    private SpotifyApi spotifyApi;
    
    public SpotifyClient(SpotifyApi spotifyApi) {
        this.spotifyApi = spotifyApi;
    }
    
    public Mono<Playlist> getPlaylistByNameLike(String name) {
    
        try {
            var playLists = new PlaylistSimplified[0];
            playLists = spotifyApi.searchPlaylists(name)
                .limit(1)
                .offset(0)
                .build()
                .execute()
                .getItems();
    
            var playList = playLists[0];
            return Mono.just(new Playlist(playList.getName(), getTracks(playList)));
            
        } catch (IOException | SpotifyWebApiException e) {
            log.error("could not retrieve playlist");
            return Mono.empty();
        }
    }
    
    private List<Track> getTracks(PlaylistSimplified playList) throws IOException, SpotifyWebApiException {
        var spotifyTracks = spotifyApi.getPlaylistsTracks(playList.getOwner().getId(), playList.getId())
            .limit(100)
            .offset(0)
            .build()
            .execute()
            .getItems();
        
        return stream(spotifyTracks).map(t ->
            new Track(
                t.getTrack().getName(),
                t.getTrack().getArtists()[0].getName(),
                t.getTrack().getHref())
        ).collect(toList());
    }
}
