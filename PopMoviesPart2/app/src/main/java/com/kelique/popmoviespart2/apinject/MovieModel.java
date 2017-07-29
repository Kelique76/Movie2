package com.kelique.popmoviespart2.apinject;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by kelique on 6/25/2017.
 */
public class MovieModel implements Parcelable {

    public static final Parcelable.Creator<MovieModel> CREATOR = new Parcelable.Creator<MovieModel>() {

        public MovieModel createFromParcel(Parcel in) {

            return new MovieModel(in);
        }

        public MovieModel[] newArray(int size) {
            return new MovieModel[size];
        }
    };
    private String overview;
    private String title;
    @SerializedName("poster_path")
    private String posterPath;
    @SerializedName("vote_average")
    private String voteAvarage;
    @SerializedName("release_date")
    private String releaseDate="";
    private long id;
    private int runtime;
    private String cachedPosterPath;
    private List<TrailerModel> trailers;


    public MovieModel() {

    }



    // parcel construtor
    protected MovieModel(Parcel p) {
        id = p.readLong();
        overview = p.readString();
        posterPath = p.readString();
        title = p.readString();
        voteAvarage = p.readString();
        releaseDate=p.readString();
        runtime=p.readInt();
        cachedPosterPath=p.readString();

    }

    public List<TrailerModel> getTrailers() {
        return trailers;
    }

    public void setTrailers(List<TrailerModel> trailers) {
        this.trailers = trailers;
    }

    public String getCachedPosterPath() {
        return cachedPosterPath;
    }

    public void setCachedPosterPath(String cachedPosterPath) {
        this.cachedPosterPath = cachedPosterPath;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVoteAvarage() {
        return voteAvarage;
    }

    public void setVoteAvarage(String voteAvarage) {
        this.voteAvarage = voteAvarage;
    }

    public String getReleaseYear(){
        return releaseDate.substring(0,4);
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(overview);
        dest.writeString(posterPath);
        dest.writeString(title);
        dest.writeString(voteAvarage);
        dest.writeString(releaseDate);
        dest.writeInt(runtime);
        dest.writeString(cachedPosterPath);
    }
//then equal overode
    @Override
    public boolean equals(Object obj) {
        if (this==obj) return true;
        if (this==null) return false;
        if (this.getClass() !=obj.getClass()) return false;
        MovieModel movi = (MovieModel) obj;

        return movi.getId()==this.getId();
    }
}
