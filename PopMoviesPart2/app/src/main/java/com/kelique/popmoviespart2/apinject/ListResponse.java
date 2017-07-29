package com.kelique.popmoviespart2.apinject;

import java.util.List;

/**
 * Created by kelique on 6/25/2017.
 */
public class ListResponse {
    List<MovieModel> results;
    int page;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List<MovieModel> getResults() {
        return results;
    }

    public void setResults(List<MovieModel> results) {
        this.results = results;
    }
}
