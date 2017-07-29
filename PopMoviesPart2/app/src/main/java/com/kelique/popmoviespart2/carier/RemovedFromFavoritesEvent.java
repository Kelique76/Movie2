package com.kelique.popmoviespart2.carier;

import com.kelique.popmoviespart2.apinject.MovieModel;
/**
 * Created by kelique on 7/21/2017.
 */
public class RemovedFromFavoritesEvent {
    MovieModel movie;

    public RemovedFromFavoritesEvent(MovieModel movie) {
        this.movie = movie;
    }

    public MovieModel getMovie() {
        return movie;
    }
}
