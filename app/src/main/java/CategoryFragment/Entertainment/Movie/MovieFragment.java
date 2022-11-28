package CategoryFragment.Entertainment.Movie;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.srisu.R;
import com.example.srisu.databinding.FragmentMovieBinding;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import CategoryFragment.Entertainment.Movie.ClickListener.onMovieCommentListener;

public class MovieFragment extends Fragment {

    FragmentMovieBinding movieBinding;
    MovieAdapter movieAdapter;
    FirebaseAuth firebaseAuth;
    RelativeLayout relativeLayout;
    MovieCommentAdapter commentAdapter;
    boolean bottomSheetDisplayed = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        movieBinding =  FragmentMovieBinding.inflate(inflater, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

       View v =  inflater.inflate(R.layout.comment_box,null);
       relativeLayout = v.findViewById(R.id.RelativeComment);

        movieBinding.movieRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL);
        movieBinding.movieRecycler.addItemDecoration(dividerItemDecoration); // Adding separating line below everyList in RecyclerView

        FirebaseRecyclerOptions<MovieModel> options =
                new FirebaseRecyclerOptions.Builder<MovieModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().
                                child("Movies"), MovieModel.class)
                        .build(); // Fetching all the data from Movies node.


        movieAdapter = new MovieAdapter(options,getContext(),onMovieCommentListener);
        movieBinding.movieRecycler.setAdapter(movieAdapter);


        movieBinding.addMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMovie();
            }
        });

        return movieBinding.getRoot();
    }

    private void addMovie(){
        Dialog dialog = new Dialog(getContext());
        dialog.getWindow().setContentView(R.layout.add_movie);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();

        EditText movieTitle = dialog.findViewById(R.id.movieTitle);
        Button recommend = dialog.findViewById(R.id.recommend);
        CheckBox movie = dialog.findViewById(R.id.movies);
        CheckBox TvSeries = dialog.findViewById(R.id.TvSeries);
        CheckBox drama = dialog.findViewById(R.id.Drama);
        CheckBox anime = dialog.findViewById(R.id.Anime);
        EditText Source = dialog.findViewById(R.id.movieSource);

        recommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String MovieTitle = movieTitle.getText().toString();
                String category = null;
                String movieSource = Source.getText().toString();
                List<MovieCommentModel> commentModelList = new ArrayList<>();

                if(!movie.isChecked() && !TvSeries.isChecked() &&
                        !drama.isChecked() && !anime.isChecked() ){
                    movie.setError("Required");
                    movie.setFocusable(true);

                    movie.setError("Required");
                    movie.setFocusable(true);

                    return;
                }else if(movie.isChecked() && TvSeries.isChecked()
                        && anime.isChecked() && drama.isChecked()){

                    Toast.makeText(getContext(), "Choose Only on Category", Toast.LENGTH_SHORT).show();

                    return;
                }

                if(movie.isChecked()){
                    category = "Movie";
                }else if(TvSeries.isChecked()){
                    category = "Series";
                }else if(anime.isChecked()){
                    category = "Anime";
                }else{
                    category = "Drama";
                }

                String randomPushKey = FirebaseDatabase.getInstance().getReference().push().getKey();

                MovieModel movieModel = new MovieModel(MovieTitle, firebaseAuth.getUid(),category,commentModelList,"",movieSource);


                assert randomPushKey != null;
                FirebaseDatabase.getInstance().getReference().child("Movies").child(randomPushKey)
                        .setValue(movieModel)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                dialog.dismiss();
                            }
                        });

                HashMap<String,Object> likesMap = new HashMap<>();
                likesMap.put("Likers","");

                FirebaseDatabase.getInstance().getReference().child("Movies").child(randomPushKey)
                        .child("Likes")
                        .setValue(likesMap);


            }
        });
    }

    private final onMovieCommentListener onMovieCommentListener = new onMovieCommentListener() {
        @Override
        public void Onclick(String MovieTitle) {

            displayBottomSheet(MovieTitle);

            Log.d(TAG, "Onclick: " + MovieTitle);
        }
    };

    private void displayBottomSheet(String Key){ // it displays a bottom view that shows the all the comments

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        View layout = LayoutInflater.from(getContext()).inflate(R.layout.comment_box ,null);
        bottomSheetDialog.setContentView(layout);
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.show();

        bottomSheetDisplayed = true;


        Log.d(TAG, "displayBottomSheet: " + Key);

        ImageView imageView = layout.findViewById(R.id.MakeComment);
        EditText commentText = layout.findViewById(R.id.commentText);
        RecyclerView commentRecycler = layout.findViewById(R.id.cmtListRecycler);

        commentRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL);
        movieBinding.movieRecycler.addItemDecoration(dividerItemDecoration);

        FirebaseRecyclerOptions<MovieCommentModel> CommentOptions =
                new FirebaseRecyclerOptions.Builder<MovieCommentModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().
                                child("Movies").child(Key).child("Comments"), MovieCommentModel.class)
                        .build(); // Getting all the commentList from the Movies-Comments node.


        commentAdapter = new MovieCommentAdapter(CommentOptions,getContext(),Key);
        commentRecycler.setAdapter(commentAdapter);

        commentAdapter.startListening();


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String CommentText = commentText.getText().toString();

                MovieCommentModel commentModel = new MovieCommentModel(CommentText,firebaseAuth.getUid());

                FirebaseDatabase.getInstance().getReference().child("Movies")
                        .child(Key)
                        .child("Comments")
                        .push()
                        .setValue(commentModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                              commentText.setText("");
                            }
                        });
            }
        });


    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
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