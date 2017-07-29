package com.example;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;

public class MainGen {
    public static void main(String[] args)  throws Exception {

        Schema schema = new Schema(1000, "com.kelique.popmoviespart2.dbhelper");


        Entity trailer = schema.addEntity("Trailer");
        trailer.addIdProperty();
        trailer.addStringProperty("key");
        Property trailerName = trailer.addStringProperty("name").notNull().getProperty();
        trailer.addContentProvider();
        Property movie_id = trailer.addLongProperty("movie_id").notNull().getProperty();


        Entity movie = schema.addEntity("Movie");
        movie.addIdProperty();
        movie.addStringProperty("overview");
        movie.addStringProperty("poster_path");
        movie.addStringProperty("title");
        movie.addStringProperty("voteAvarage");
        movie.addStringProperty("releaseDate");
        movie.addIntProperty("runtime");
        movie.addStringProperty("cachedPosterPath");

        movie.addContentProvider();



        ToMany movieToTrailers= movie.addToMany(trailer, movie_id);
        movieToTrailers.setName("trailers");
        movieToTrailers.orderAsc(trailerName);


        new DaoGenerator().generateAll(schema, "../app/src/main/java");

    }
}

