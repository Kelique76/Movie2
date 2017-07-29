package com.kelique.popmoviespart2.ui.movie_details;


import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kelique.popmoviespart2.AppController;
import com.kelique.popmoviespart2.R;
import com.kelique.popmoviespart2.apinject.APIClient;
import com.kelique.popmoviespart2.apinject.MovieModel;
import com.kelique.popmoviespart2.apinject.TrailerModel;
import com.kelique.popmoviespart2.carier.AddedToFavoritesEvent;
import com.kelique.popmoviespart2.carier.RemovedFromFavoritesEvent;
import com.kelique.popmoviespart2.dbhelper.MovieDao;
import com.kelique.popmoviespart2.dbhelper.MoviesContentProvider;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<MovieModel> {


    private View rootView;
    @Bind(R.id.title)
    TextView titleView;

    @Bind(R.id.poster)
    ImageView posterImageView;
    private MovieModel movie;
    @Bind(R.id.year)
    TextView yearView;
    @Bind(R.id.rating)
    TextView ratingView;
    @Bind(R.id.overview)
    TextView overviewView;
    @Bind(R.id.runtime)
    TextView runtimeView;

    boolean detailsLoaded=false;
    @Bind(R.id.trailers)
    LinearLayout trailersContainer;
    @Bind(R.id.add_to_favorites)
    Button addToFavoritedButton;
    private boolean isFavorite;


    public DetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);

        rootView = inflater.inflate(R.layout.details_fragment, container, false);

        ButterKnife.bind(this, rootView);
        if (savedInstanceState == null) {
            movie = getArguments().getParcelable("movie");
        } else {
            movie = savedInstanceState.getParcelable("movie");
            detailsLoaded=true;

        }

        titleView.setText(movie.getTitle());
        overviewView.setText(movie.getOverview());
        if (movie.getVoteAvarage()!=null) ratingView.setText(movie.getVoteAvarage() + "/10");
        if (movie.getRuntime()!=0) runtimeView.setText(String.valueOf(movie.getRuntime()) + "min");
        if (movie.getReleaseDate()!=null) yearView.setText(movie.getReleaseYear());

        Picasso.with(getActivity()).load(movie.getCachedPosterPath()).placeholder(R.drawable.notification_template_icon_bg).into(posterImageView);

        Uri uri= ContentUris.withAppendedId(MoviesContentProvider.CONTENT_URI, movie.getId());

        Cursor cursor=getActivity().getContentResolver().query(uri, null, null, null, null);

        if (cursor.getCount()>0){
            isFavorite=true;
            addToFavoritedButton.setText(R.string.remove_from_favorites);
        }
        return rootView;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        inflater.inflate(R.menu.menu_details, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (movie.getTrailers()!=null && movie.getTrailers().size()>0) {
            TrailerModel trailer=movie.getTrailers().get(0);
            Intent share = new Intent(android.content.Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_SUBJECT,"Share:"+ trailer.getName());
            share.putExtra(Intent.EXTRA_TEXT, Uri.parse("http://www.youtube.com/watch?v=" + trailer.getKey()));
            startActivity(Intent.createChooser(share, getActivity().getString(R.string.share_trailer)));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onStart() {
        super.onStart();
        // load details from server
        if (!detailsLoaded && AppController.isConectedToInternet()) {
            getLoaderManager().initLoader(0, null, this).forceLoad();
        }
    }

    @Override
    public Loader<MovieModel> onCreateLoader(int id, Bundle args) {

        MovieLoader loader = new MovieLoader(getActivity());
        loader.setId(movie.getId());
        return loader;

    }

    @Override
    public void onLoadFinished(Loader<MovieModel> loader, MovieModel data) {
        if (data == null) {
            Toast.makeText(getActivity(), getResources().getString(R.string.network_error), Toast.LENGTH_LONG).show();
            return;
        }
        movie.setVoteAvarage(data.getVoteAvarage());
        movie.setReleaseDate(data.getReleaseDate());
        movie.setRuntime(data.getRuntime());
        movie.setTrailers(data.getTrailers());
        ratingView.setText(data.getVoteAvarage() + "/10");
        runtimeView.setText(String.valueOf(data.getRuntime()) + "min");
        yearView.setText(data.getReleaseYear());

        LayoutInflater li = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        if (movie.getTrailers()!=null){
            for (TrailerModel t:movie.getTrailers()) {
                View view=li.inflate(R.layout.trailer_item, null);
                TextView txt=ButterKnife.findById(view, R.id.name);
                txt.setText(t.getName());
                Button b=ButterKnife.findById(view, R.id.button);
                b.setTag(t);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TrailerModel trailer=(TrailerModel)v.getTag();
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v="+trailer.getKey())));

                    }
                });

                trailersContainer.addView(view);


            }


        }




    }

    @Override
    public void onLoaderReset(Loader<MovieModel> loader) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("movie", movie);
    }

    @OnClick(R.id.add_to_favorites)
    public void toogleFavorites(){
        if (!isFavorite) {
            ContentValues values = new ContentValues();
            values.put(MovieDao.Properties.Id.columnName, movie.getId());
            values.put(MovieDao.Properties.Title.columnName, movie.getTitle());
            values.put(MovieDao.Properties.CachedPosterPath.columnName, movie.getCachedPosterPath());
            values.put(MovieDao.Properties.Poster_path.columnName, movie.getPosterPath());
            values.put(MovieDao.Properties.Overview.columnName, movie.getOverview());
            values.put(MovieDao.Properties.ReleaseDate.columnName, movie.getReleaseDate());
            values.put(MovieDao.Properties.VoteAvarage.columnName, movie.getVoteAvarage());
            values.put(MovieDao.Properties.Runtime.columnName, movie.getRuntime());
            getActivity().getContentResolver().insert(MoviesContentProvider.CONTENT_URI, values);
            addToFavoritedButton.setText(R.string.remove_from_favorites);
            // Post event to event bus to notify movielist fragment
            AppController.getEventBus().post(new AddedToFavoritesEvent(movie));
            isFavorite=true;
        }else{
            Uri uri= ContentUris.withAppendedId(MoviesContentProvider.CONTENT_URI, movie.getId());
            getActivity().getContentResolver().delete(uri,null,null);
            addToFavoritedButton.setText(R.string.add_to_favorites);
            isFavorite=false;
            // Post event to event bus to notify movielist fragment
            AppController.getEventBus().post(new RemovedFromFavoritesEvent(movie));

        }

    }
    static class MovieLoader extends AsyncTaskLoader<MovieModel> {

        long id;

        public void setId(long id) {
            this.id = id;
        }

        public MovieLoader(Context context) {
            super(context);
        }

        @Override
        public MovieModel loadInBackground() {
            try {
                //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=cxLG2wtE7TM")));
                List<TrailerModel> trailers = APIClient.getInstance().getMovieTrailers(id);
                MovieModel m = APIClient.getInstance().getMovieDetails(id);
                m.setTrailers(trailers);
                return  m;
            } catch (Exception ex) {
                return null;
            }
        }

    }


}
