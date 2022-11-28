package CategoryFragment.Entertainment.Music;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.srisu.R;
import com.example.srisu.databinding.MusicLayoutBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

import CategoryFragment.Entertainment.Movie.ClickListener.onMovieCommentListener;
import CategoryFragment.Entertainment.Movie.MovieModel;


public class MusicAdapter extends FirebaseRecyclerAdapter<MusicModel,MusicAdapter.MusicViewHolder> {

    Context context;
    String InitialAdder,Updater;
    FirebaseAuth firebaseAuth;
    onMovieCommentListener commentListener;
    boolean testClick = false;


    public MusicAdapter(@NonNull FirebaseRecyclerOptions<MusicModel> options, Context context, onMovieCommentListener onMovieCommentListener) {
        super(options);
        this.context = context;
        this.commentListener = onMovieCommentListener;
    }

    @Override
    protected void onBindViewHolder(@NonNull MusicViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull MusicModel model) {
        holder.musicLayoutBinding.musicName.setText(model.getMusicTitle());
        holder.musicLayoutBinding.SingerName.setText(model.getSinger());

        firebaseAuth = FirebaseAuth.getInstance();
        InitialAdder = firebaseAuth.getUid();  // --> User's UID who recommended the movie

        FirebaseDatabase.getInstance().getReference().child("Music")
                .child(Objects.requireNonNull(getRef(position).getKey()))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Updater = snapshot.child("uid").getValue(String.class);

                        FirebaseDatabase.getInstance().getReference().child("Users").child(Updater)
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        holder.musicLayoutBinding.adder.setText(
                                                snapshot.child("fullName").getValue(String.class)
                                        );

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                        holder.musicLayoutBinding.cardView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {

                                if(InitialAdder.equals(Updater)){ // Checking if the user has recommended the music for updating.
                                    Dialog dialog = new Dialog(context);
                                    dialog.getWindow().setContentView(R.layout.add_music);
                                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    dialog.show();

                                    EditText musicTitle = dialog.findViewById(R.id.musicTitle);
                                    EditText singer = dialog.findViewById(R.id.singerName);
                                    Button recommend = dialog.findViewById(R.id.recommend);

                                    musicTitle.setText(model.getMusicTitle());
                                    singer.setText(model.getSinger());


                                    recommend.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String MusicTitle = musicTitle.getText().toString();
                                            String singerName = singer.getText().toString();

                                            HashMap<String, Object> hashMap = new HashMap<>();
                                           hashMap.put("musicTitle",MusicTitle);
                                           hashMap.put("singer",singerName);


                                            FirebaseDatabase.getInstance().getReference().child("Music").child(Objects.requireNonNull(getRef(position).getKey()))
                                                    .updateChildren(hashMap); // Updating the Music and it's properties

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

        holder.musicLayoutBinding.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentListener.Onclick(getRef(position).getKey()); // Showing the comment section
            }
        });

        FirebaseDatabase.getInstance().getReference()
                .child("Music").child(getRef(position).getKey())
                .child("Likes").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild(InitialAdder)) {
                            holder.musicLayoutBinding.like.setImageResource(R.drawable.likeee); // Enabling the like if the user has liked it!
                        } else {
                            holder.musicLayoutBinding.like.setImageResource(R.drawable.heart);
                        }

                        long LikeCount = snapshot.getChildrenCount() - 1; // Calculating the like Count

                        if(LikeCount >= 1) {
                            String totalLikes = String.valueOf(LikeCount);
                            holder.musicLayoutBinding.likeCount.setText(totalLikes);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        holder.musicLayoutBinding.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String liker = FirebaseAuth.getInstance().getUid();
                testClick = true;

                FirebaseDatabase.getInstance().getReference()
                        .child("Music").child(Objects.requireNonNull(getRef(position).getKey()))
                        .child("Likes").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                assert liker != null;
                                if (snapshot.hasChild(liker)) {
                                    if (testClick) {
                                        FirebaseDatabase.getInstance().getReference().child("Music")
                                                .child(Objects.requireNonNull(getRef(position).getKey())).child("Likes")
                                                .child(liker).removeValue(); // if the user has already liked the music it will remove his UID from database and
                                        // unlikes the music
                                    }
                                }else{
                                    if (testClick) {
                                        FirebaseDatabase.getInstance().getReference().child("Music")
                                                .child(Objects.requireNonNull(getRef(position).getKey())).child("Likes")
                                                .child(liker).
                                                push().
                                                setValue(true);// Adding user UID in database and enabling the database
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




    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        MusicLayoutBinding musicLayoutBinding = MusicLayoutBinding.inflate(layoutInflater,parent,false);
        return new MusicViewHolder(musicLayoutBinding);
    }

    public static class MusicViewHolder extends RecyclerView.ViewHolder{

        MusicLayoutBinding musicLayoutBinding;

        public MusicViewHolder(MusicLayoutBinding musicLayoutBinding) {
            super(musicLayoutBinding.getRoot());

            this.musicLayoutBinding = musicLayoutBinding;
        }
    }

}
