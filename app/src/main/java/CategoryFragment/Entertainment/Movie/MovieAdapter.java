package CategoryFragment.Entertainment.Movie;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.srisu.R;
import com.example.srisu.databinding.MovieLayoutBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

import CategoryFragment.Entertainment.Movie.ClickListener.onMovieCommentListener;

public class MovieAdapter extends FirebaseRecyclerAdapter<MovieModel, MovieAdapter.MovieViewHolder> {

    private static final String TAG = "This";
    Context context;
    String InitialAdder, Updater;
    onMovieCommentListener commentListener;
    boolean testClick = false;

    public MovieAdapter(@NonNull FirebaseRecyclerOptions<MovieModel> options, Context context, onMovieCommentListener onMovieCommentListener) {
        super(options);
        this.context = context;
        this.commentListener = onMovieCommentListener;
    }

    @Override
    protected void onBindViewHolder(@NonNull MovieViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull MovieModel model) {

        holder.movieLayoutBinding.movieName.setText(model.getMovieTitle());
        holder.movieLayoutBinding.category.setText(model.getCategory());
        holder.movieLayoutBinding.sourceMovie.setText(model.getMovieSource());

        InitialAdder = FirebaseAuth.getInstance().getUid(); //--> User's UID who recommended the movie

        FirebaseDatabase.getInstance().getReference().child("Movies")
                .child(Objects.requireNonNull(getRef(position).getKey()))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Updater = snapshot.child("uid").getValue(String.class);

                        FirebaseDatabase.getInstance().getReference().child("Users").child(Updater)
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        holder.movieLayoutBinding.adder.setText(
                                                snapshot.child("fullName").getValue(String.class)
                                        );

                                        Log.d(TAG, "onDataChange: " + Updater);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                        holder.movieLayoutBinding.cardView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {

                                if (InitialAdder.equals(Updater)) { //Checking for updating the movie Recommendation if the user had recommended it!
                                    Dialog dialog = new Dialog(context);
                                    dialog.getWindow().setContentView(R.layout.add_movie);
                                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    dialog.show();

                                    EditText movieTitle = dialog.findViewById(R.id.movieTitle);
                                    Button recommend = dialog.findViewById(R.id.recommend);
                                    CheckBox movie = dialog.findViewById(R.id.movies);
                                    CheckBox TvSeries = dialog.findViewById(R.id.TvSeries);

                                    movieTitle.setText(model.getMovieTitle());

                                    if (model.getCategory().equals("Movie")) {
                                        movie.setChecked(true);
                                    } else {
                                        TvSeries.setChecked(true);
                                    }

                                    recommend.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String MovieTitle = movieTitle.getText().toString();
                                            String category = null;

                                            if (!movie.isChecked() && !TvSeries.isChecked()) {
                                                movie.setError("Required");
                                                movie.setFocusable(true);

                                                movie.setError("Required");
                                                movie.setFocusable(true);

                                                return;
                                            } else if (movie.isChecked() && TvSeries.isChecked()) {
                                                Toast.makeText(context, "Choose Only on Category", Toast.LENGTH_SHORT).show();

                                                movie.setError("Required");
                                                movie.setFocusable(true);

                                                movie.setError("Required");
                                                movie.setFocusable(true);

                                                return;
                                            }

                                            if (movie.isChecked()) {
                                                category = "Movie";
                                            } else {
                                                category = "Series";
                                            }

                                            HashMap<String, Object> hashMap = new HashMap<>();
                                            hashMap.put("movieTitle", MovieTitle);
                                            hashMap.put("category", category);


                                            FirebaseDatabase.getInstance().getReference().child("Movies").child(Objects.requireNonNull(getRef(position).getKey()))
                                                    .updateChildren(hashMap);

                                            dialog.dismiss();
                                        }
                                    });
                                }

                                return false;
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        FirebaseDatabase.getInstance().getReference()
                .child("Movies").child(getRef(position).getKey())
                .child("Likes").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild(InitialAdder)) {
                            holder.movieLayoutBinding.like.setImageResource(R.drawable.likeee); // --> Enabling the like if the user had liked it previously.
                        } else {
                            holder.movieLayoutBinding.like.setImageResource(R.drawable.heart);
                        }

                        long LikeCount = snapshot.getChildrenCount() - 1;

                        if(LikeCount >= 1) {
                            String totalLikes = String.valueOf(LikeCount);
                            holder.movieLayoutBinding.likeCount.setText(totalLikes);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        holder.movieLayoutBinding.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String liker = FirebaseAuth.getInstance().getUid();
                testClick = true;

                FirebaseDatabase.getInstance().getReference() //--> Putting the user's Uid in like database to ensure that the user has liked!
                        .child("Movies").child(Objects.requireNonNull(getRef(position).getKey()))
                        .child("Likes").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                assert liker != null;
                                if (snapshot.hasChild(liker)) {
                                    if (testClick) {
                                        FirebaseDatabase.getInstance().getReference().child("Movies")
                                                .child(Objects.requireNonNull(getRef(position).getKey())).child("Likes")
                                                .child(liker).removeValue(); // if the user has previously liked it, it will remove his Uid from database.
                                    }
                                }else{
                                if (testClick) {
                                    FirebaseDatabase.getInstance().getReference().child("Movies")
                                            .child(Objects.requireNonNull(getRef(position).getKey())).child("Likes")
                                            .child(liker).
                                            push().
                                            setValue(true); //--> If the user hasn't like it it will put his UID in database
                                }
                                }
                                testClick = false;
                            }

                @Override
                public void onCancelled (@NonNull DatabaseError error){

                }
            });


        }
    });


        holder.movieLayoutBinding.cardView.setOnClickListener(new View.OnClickListener()

    {
        @Override
        public void onClick (View v){
        Intent intent = new Intent(context, MovieDetailActivity.class);
        intent.putExtra("Movie", model.getMovieTitle());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent); // To take to movie detail activity which will show all the details of movie through the Api.

    }
    });


        holder.movieLayoutBinding.comment.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick (View v){
        commentListener.Onclick(getRef(position).getKey());
    }
    });

}


    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        MovieLayoutBinding movieLayoutBinding = MovieLayoutBinding.inflate(layoutInflater, parent, false);
        return new MovieViewHolder(movieLayoutBinding);
    }

public static class MovieViewHolder extends RecyclerView.ViewHolder {

    MovieLayoutBinding movieLayoutBinding;

    public MovieViewHolder(MovieLayoutBinding movieLayoutBinding) {
        super(movieLayoutBinding.getRoot());

        this.movieLayoutBinding = movieLayoutBinding;
    }
}


}
