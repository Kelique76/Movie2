package com.kelique.popmoviespart2.apinject;

import java.util.List;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by kelique on 6/25/2017.
 */


public class APIClient implements RequestInterceptor {
    public static  final String API_KEY="a58b5acd0ae2a5f99dbd52615cea98fa";
    public static  final String KEY_PARAM="api_key";
    public static final String BASE_URL="https://api.themoviedb.org/3";
    private static APIMethods api;
    private static APIClient instance;
    private APIClient() {

    }
    public static  APIClient getInstance(){
        if (instance==null){
            instance=new APIClient();
        }
        return instance;
    }
    private APIMethods getApi(){
        if (api==null){
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setRequestInterceptor(this)
                    .setEndpoint(APIClient.BASE_URL)
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .build();

            api=restAdapter.create(APIMethods.class);
        }
        return api;
    }
    @Override
    public void intercept(RequestFacade request) {
        request.addQueryParam(APIClient.KEY_PARAM,APIClient.API_KEY);
    }
    public List<MovieModel> getPopular(){
        ListResponse result=getApi().getPopular();
        return  result.getResults();
    }
    public List<MovieModel> getTopRated(){
        ListResponse result=getApi().getTopRated();
        return  result.getResults();
    }

    public MovieModel getMovieDetails(long id) {
        MovieModel m=getApi().getMovieDetails(id);
        return m;
    }
    public List<TrailerModel> getMovieTrailers (long id){
        return getApi().getMovieTrailers(id).getResults();
    }

    private   interface APIMethods{
        @GET("/movie/popular")
        ListResponse getPopular();
        @GET("/movie/top_rated")
        ListResponse getTopRated();
        @GET("/movie/{id}")
        MovieModel getMovieDetails(@Path("id") long id);
        @GET("/movie/{id}/videos")
        TrailersResponse getMovieTrailers(@Path("id") long id);
    }
}
