package com.github.brunotorrao.duff.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BeerSuggestion {
    private String beerStyle;
    private Playlist playlist;
}
