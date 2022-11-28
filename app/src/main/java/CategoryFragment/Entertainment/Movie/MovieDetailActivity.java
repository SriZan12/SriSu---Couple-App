package CategoryFragment.Entertainment.Movie;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.srisu.databinding.ActivityMovieDetailBinding;

import org.json.JSONException;
import org.json.JSONObject;

public class MovieDetailActivity extends AppCompatActivity {

    ActivityMovieDetailBinding movieDetailBinding;
    String MovieTitle,Apikey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        movieDetailBinding = ActivityMovieDetailBinding.inflate(getLayoutInflater());
        setContentView(movieDetailBinding.getRoot());

        hideStatusBar();

        MovieTitle = getIntent().getStringExtra("Movie");
        Apikey = "bec7efc6";

        Log.d(TAG, "onCreate: " + MovieTitle);

        ShowMovieDetails(MovieTitle);
    }

    private void hideStatusBar() { // --> This Function will hide the Status Bar of the phone
        View decorView = getWindow().getDecorView();
       // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

    }


    private void ShowMovieDetails(String movieTitle){ // Fetching all the movie details through Volley Library Api.

        String url = "https://www.omdbapi.com/?t="+movieTitle+"&plot=full&apiKey="+Apikey;

        RequestQueue requestQueue = Volley.newRequestQueue(MovieDetailActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(MovieDetailActivity.this, "Responded!", Toast.LENGTH_SHORT).show();
                        try {
                            String Title = response.getString("Title");
                            String Runtime = response.getString("Runtime");
                            String Genre = response.getString("Genre");
                            String Director = response.getString("Director");
                            String Writer = response.getString("Writer");
                            String Actors = response.getString("Actors");
                            String Type = response.getString("Type");
                            String ImDB = response.getString("imdbRating");
                            String Poster = response.getString("Poster");
                            String plot = response.getString("Plot");
                            String release = response.getString("Released");
                            String Country = response.getString("Country");

                            if(Type.equals("series")) {
                                String totalSeasons = response.getString("totalSeasons");
                                movieDetailBinding.box.setText("TotalSeasons: ");
                                movieDetailBinding.boxOffice.setText(totalSeasons);
                            }else{
                                String BoxOffice = response.getString("BoxOffice");
                                movieDetailBinding.box.setText("BoxOffice: ");
                                movieDetailBinding.boxOffice.setText(BoxOffice);
                            }

                            Log.d(TAG, "onResponse: " + Title);
                            Log.d(TAG, "onResponse: " + plot);

                            Log.d(TAG, "onResponse: " + Poster);

                            Glide.with(MovieDetailActivity.this)
                                    .load(Uri.parse(Poster))
                                    .into(movieDetailBinding.poster);

                            movieDetailBinding.movieTitle.setText(Title);
                            movieDetailBinding.IMBD.setText(ImDB);
                            movieDetailBinding.MovieDescription.setText(plot);
                            movieDetailBinding.genre.setText(Genre);
                            movieDetailBinding.runTime.setText(Runtime);
                            movieDetailBinding.director.setText(Director);
                            movieDetailBinding.writer.setText(Writer);
                            movieDetailBinding.release.setText(release);
                            movieDetailBinding.country.setText(Country);
                            movieDetailBinding.actors.setText(Actors);
                            movieDetailBinding.type.setText(Type);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(MovieDetailActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

        Log.d(TAG, "onResponse: " + url);

        requestQueue.add(jsonObjectRequest);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}