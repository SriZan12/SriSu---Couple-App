package CategoryFragment.Entertainment.Movie;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.srisu.R;
import com.example.srisu.databinding.CommentLayoutBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class MovieCommentAdapter extends FirebaseRecyclerAdapter<MovieCommentModel, MovieCommentAdapter.CommentViewHolder> {

    private static final String TAG = "This";
    Context context;
    String Initial_Commenter;
    FirebaseAuth firebaseAuth;
    String Key;

    public MovieCommentAdapter(@NonNull FirebaseRecyclerOptions<MovieCommentModel> options, Context context, String key) {
        super(options);
        this.context = context;
        this.Key = key;
    }


    @Override
    protected void onBindViewHolder(@NonNull CommentViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull MovieCommentModel model) {

        holder.commentLayoutBinding.comment.setText(model.getComment());

        firebaseAuth = FirebaseAuth.getInstance();
        Initial_Commenter = firebaseAuth.getUid(); //--> Initial commenter is the user who has commented on the movie.


        FirebaseDatabase.getInstance().getReference().child("Users").child(model.getCommenter_Uid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        holder.commentLayoutBinding.commentProfileName.setText(
                                snapshot.child("fullName").getValue(String.class) // Getting the name of the commenter
                        );

                        String ImageUrl = snapshot.child("profileImage").getValue(String.class);

                        Glide.with(context)
                                .load(ImageUrl)
                                .placeholder(R.drawable.placeholder)
                                .into(holder.commentLayoutBinding.commentProfile); // Fetching his image in the ImageView
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        holder.commentLayoutBinding.cardViewComment.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if (Initial_Commenter.equals(model.getCommenter_Uid())) { //--> Checking if the user has commented that particular comment or not.
                    Dialog dialog = new Dialog(context);
                    dialog.getWindow().setContentView(R.layout.update_comment);
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.show();

                    EditText comment = dialog.findViewById(R.id.UpdateCommentText);
                    ImageView UpdateComment = dialog.findViewById(R.id.UpdateMakeComment);

                    comment.setText(model.getComment());

                    UpdateComment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String UpdatedComment = comment.getText().toString();

                            HashMap<String, Object> commentHash = new HashMap<>();
                            commentHash.put("comment", UpdatedComment);

                            FirebaseDatabase.getInstance().getReference()
                                    .child("Movies")
                                    .child(Key)
                                    .child("Comments")
                                    .child(getRef(position).getKey()) //--> Putting the updated comment
                                    .updateChildren(commentHash).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            dialog.dismiss();
                                        }
                                    });
                        }
                    });


                }

                return false;
            }
        });


    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        CommentLayoutBinding commentLayoutBinding = CommentLayoutBinding.inflate(layoutInflater, parent, false);
        return new CommentViewHolder(commentLayoutBinding);
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {

        CommentLayoutBinding commentLayoutBinding;

        public CommentViewHolder(CommentLayoutBinding commentLayoutBinding) {
            super(commentLayoutBinding.getRoot());

            this.commentLayoutBinding = commentLayoutBinding;
        }
    }
}
