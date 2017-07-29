package com.kelique.popmoviespart2.apinject;

import java.util.List;
/**
 * Created by kelique on 7/21/2017.
 */
public class TrailersResponse {
    List<TrailerModel> results;

    public List<TrailerModel> getResults() {
        return results;
    }

    public void setResults(List<TrailerModel> results) {
        this.results = results;
    }
}
