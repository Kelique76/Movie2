package com.kelique.popmoviespart2;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.kelique.popmoviespart2.dbhelper.DaoMaster;
import com.kelique.popmoviespart2.dbhelper.DaoSession;
import com.kelique.popmoviespart2.dbhelper.MoviesContentProvider;
import com.squareup.otto.Bus;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by kelique on 7/21/2017.
 */
public class AppController extends Application {

    static AppController instance;
    static DaoSession daoSession;
    static Bus bus;
    @Override
    public void onCreate() {
        super.onCreate();
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "dbhelper", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession=daoMaster.newSession();
        MoviesContentProvider.daoSession =daoSession;


//picasso tuning
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
        Picasso built = builder.build();
        Picasso.setSingletonInstance(built);

        instance = this;
        bus = new Bus();
    }

    public static boolean isConectedToInternet() {
        ConnectivityManager cm =
                (ConnectivityManager) instance.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }
    public static DaoSession getDaoSession(){
        return daoSession;
    }

    public static Bus getEventBus() {
        return bus;
    }
}
