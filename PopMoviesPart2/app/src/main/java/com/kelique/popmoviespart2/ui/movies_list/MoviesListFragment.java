package com.kelique.popmoviespart2.ui.movies_list;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.GridView;

import com.kelique.popmoviespart2.R;
import com.kelique.popmoviespart2.AppController;
import com.kelique.popmoviespart2.apinject.APIClient;
import com.kelique.popmoviespart2.apinject.MovieModel;
import com.kelique.popmoviespart2.dbhelper.MovieDao;
import com.kelique.popmoviespart2.dbhelper.MoviesContentProvider;
import com.kelique.popmoviespart2.carier.AddedToFavoritesEvent;
import com.kelique.popmoviespart2.carier.RemovedFromFavoritesEvent;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by kelique on 6/25/2017.
 */

public class MoviesListFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<List<MovieModel>>{
    public static final int POPULAR=1;
    public static final int TOP_RATED=2;
    public static final int FAVORITES=3;
    public static final String LIST_TYPE="list_type";
    private int listType = 0;
    MovieModel[] data;


    @Bind(R.id.grid)
    GridView gridView;


    private MoviesListAdapter moviAdapter;
    private View rootView;
    private Callback callback;


    public MoviesListFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);



    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int t =  prefs.getInt(LIST_TYPE, POPULAR);
        if (listType!=t) {
            getLoaderManager().initLoader(0, null, MoviesListFragment.this).forceLoad();

        }
//        // reload list if settings are changed
//        if (listType !=t){
//            getLoaderManager().initLoader(0, null, MoviesListFragment.this).forceLoad();
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        AppController.getEventBus().register(this);
        rootView = inflater.inflate(R.layout.movie_list_fragment, container, false);
        ButterKnife.bind(this,rootView);
        moviAdapter=new MoviesListAdapter(getActivity(),R.layout.movie_list_item);
        gridView.setAdapter(moviAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (callback != null) callback.movieSelected(moviAdapter.getItem(position));
            }
        });

        ViewTreeObserver vto = rootView.getViewTreeObserver();
        //wait for layout rendiring to obtain grid view width in pixels
        if (savedInstanceState!=null){
            data= (MovieModel[]) savedInstanceState.getParcelableArray("movies");
        }

        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                rootView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                int width = gridView.getMeasuredWidth()/ gridView.getNumColumns();
                moviAdapter.setImageWidth(width);
                moviAdapter.setImageHeight((int) (width * 1.5837));
                if (data==null){
                    getLoaderManager().initLoader(0, null, MoviesListFragment.this).forceLoad();
                }else{
                    moviAdapter.clear();
                    moviAdapter.addAll(data);
                    moviAdapter.notifyDataSetChanged();
                }


            }
        });


        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArray("movies", data);
    }

    @Override
    public Loader<List<MovieModel>> onCreateLoader(int id, Bundle args) {
        Loader<List<MovieModel>> loader=new MovieLoader(getActivity());
        return  loader;
    }

    @Override
    public void onLoadFinished(Loader<List<MovieModel>> loader, List<MovieModel> loadedData) {
//        if (loadedData.size()==0) {
//            Toast.makeText(getActivity(),getResources().getString(R.string.network_error), Toast.LENGTH_LONG).show();
//            return;
//        }
        moviAdapter.clear();
        moviAdapter.addAll(loadedData);
        this.data=new MovieModel[loadedData.size()];
        loadedData.toArray(this.data);
        moviAdapter.notifyDataSetChanged();
        gridView.setSelection(0);
        gridView.smoothScrollToPosition(0);
    }

    @Override
    public void onLoaderReset(Loader<List<MovieModel>> loader) {

    }

    public interface Callback {
        // TODO: Update argument type and name
        public void movieSelected(MovieModel movie);
    }
    public static class MovieLoader extends AsyncTaskLoader<List<MovieModel>> {

        public MovieLoader(Context context) {
            super(context);
        }

        @Override
        public List<MovieModel> loadInBackground() {
            try {

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                int listType = prefs.getInt(LIST_TYPE, POPULAR);
                switch (listType){
                    case TOP_RATED:
                        if (AppController.isConectedToInternet())   return APIClient.getInstance().getTopRated();
                    case POPULAR:
                        if (AppController.isConectedToInternet())  return APIClient.getInstance().getPopular();
                    case FAVORITES:
                        Cursor cursor=getContext().getContentResolver().query(MoviesContentProvider.CONTENT_URI, null, null, null, null);
                        ArrayList<MovieModel> list=new ArrayList<>();
                        while(cursor.moveToNext()){
                            MovieModel model=new MovieModel();
                            model.setId(cursor.getLong( cursor.getColumnIndex(MovieDao.Properties.Id.columnName)));
                            model.setTitle(cursor.getString(cursor.getColumnIndex(MovieDao.Properties.Title.columnName)));
                            model.setPosterPath(cursor.getString(cursor.getColumnIndex(MovieDao.Properties.Poster_path.columnName)));
                            model.setCachedPosterPath(cursor.getString(cursor.getColumnIndex(MovieDao.Properties.CachedPosterPath.columnName)));
                            model.setOverview(cursor.getString(cursor.getColumnIndex(MovieDao.Properties.Overview.columnName)));
                            model.setReleaseDate(cursor.getString(cursor.getColumnIndex(MovieDao.Properties.ReleaseDate.columnName)));
                            model.setRuntime(cursor.getInt(cursor.getColumnIndex(MovieDao.Properties.Runtime.columnName)));
                            model.setVoteAvarage(cursor.getString(cursor.getColumnIndex(MovieDao.Properties.VoteAvarage.columnName)));
                            list.add(model);
                        }
                        return  list;
                }
            }catch (Exception ex){

                return new ArrayList<MovieModel>();
            }
            return new ArrayList<MovieModel>();
        }

    }
    public void reload(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int t=prefs.getInt(LIST_TYPE,POPULAR);
        if (listType!=t){
            listType=t;
            getLoaderManager().initLoader(0, null, MoviesListFragment.this).forceLoad();

        }
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Subscribe
    public void onFavoriteAdded(AddedToFavoritesEvent event){
        if (listType==FAVORITES){
            moviAdapter.add(event.getMovie());
            moviAdapter.notifyDataSetChanged();
        }
    }
    @Subscribe
    public  void onFavoritesRemoved(RemovedFromFavoritesEvent event){

        moviAdapter.remove(event.getMovie());
        moviAdapter.notifyDataSetChanged();
    }
}
