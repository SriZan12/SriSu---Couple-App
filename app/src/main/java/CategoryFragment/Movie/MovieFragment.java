package CategoryFragment.Movie;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Movie;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.srisu.R;
import com.example.srisu.databinding.FragmentMovieBinding;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class MovieFragment extends Fragment {

    FragmentMovieBinding movieBinding;
    MovieAdapter movieAdapter;
    FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        movieBinding =  FragmentMovieBinding.inflate(inflater, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        movieBinding.movieRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL);
        movieBinding.movieRecycler.addItemDecoration(dividerItemDecoration);

        FirebaseRecyclerOptions<MovieModel> options =
                new FirebaseRecyclerOptions.Builder<MovieModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().
                                child("Movies"), MovieModel.class)
                        .build();

        movieAdapter = new MovieAdapter(options,getContext());
        movieBinding.movieRecycler.setAdapter(movieAdapter);

        movieBinding.addMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dialog dialog = new Dialog(getContext());
                dialog.getWindow().setContentView(R.layout.add_movie);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();

                EditText movieTitle = dialog.findViewById(R.id.movieTitle);
                Button recommend = dialog.findViewById(R.id.recommend);
                CheckBox movie = dialog.findViewById(R.id.movies);
                CheckBox TvSeries = dialog.findViewById(R.id.TvSeries);

                recommend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String MovieTitle = movieTitle.getText().toString();
                        String category = null;

                        if(!movie.isChecked() && !TvSeries.isChecked()){
                            movie.setError("Required");
                            movie.setFocusable(true);

                            movie.setError("Required");
                            movie.setFocusable(true);

                            return;
                        }else if(movie.isChecked() && TvSeries.isChecked()){
                            Toast.makeText(getContext(), "Choose Only on Category", Toast.LENGTH_SHORT).show();

                            return;
                        }

                        if(movie.isChecked()){
                            category = "Movie";
                        }else{
                            category = "Series";
                        }

                        MovieModel movieModel = new MovieModel(MovieTitle, firebaseAuth.getUid(),category);

                        FirebaseDatabase.getInstance().getReference().child("Movies").push()
                                .setValue(movieModel);

                        dialog.dismiss();
                    }
                });
            }
        });

        return movieBinding.getRoot();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void onStart() {
        super.onStart();
        movieAdapter.notifyDataSetChanged();
        movieAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        movieAdapter.stopListening();
    }

}