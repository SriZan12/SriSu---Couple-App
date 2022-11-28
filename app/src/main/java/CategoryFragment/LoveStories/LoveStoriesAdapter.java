package CategoryFragment.LoveStories;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.srisu.R;
import com.example.srisu.databinding.StoriesLayoutBinding;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoveStoriesAdapter extends FirebaseRecyclerAdapter<StoriesModel, LoveStoriesAdapter.AllStoriesViewHolder> {

    Context context;
    onStoriesCommentListener storiesCommentListener;
    boolean testClick = false;
    String InitialAdder,Updater;
    FirebaseAuth firebaseAuth;

    public LoveStoriesAdapter(@NonNull FirebaseRecyclerOptions<StoriesModel> options, Context thisContext,onStoriesCommentListener onStoriesCommentListener) {
        super(options);
        context = thisContext;
        this.storiesCommentListener = onStoriesCommentListener;
    }

    @Override
    protected void onBindViewHolder(@NonNull AllStoriesViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull StoriesModel model) {

        firebaseAuth = FirebaseAuth.getInstance();
        InitialAdder = firebaseAuth.getUid();

        holder.storiesLayoutBinding.BookTitle.setText(model.getBookName());
        holder.storiesLayoutBinding.writer.setText(model.getAuthor());

        Glide.with(context)
                .load(model.getPhotoUrl())
                .placeholder(R.drawable.placeholder)
                .into(holder.storiesLayoutBinding.BookImage);

        holder.storiesLayoutBinding.mainLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, model.getBookName(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context,ReadStoryActivity.class);
                intent.putExtra("PDFUrl",model.getPDFUrl());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                holder.storiesLayoutBinding.mainLinear.getContext().startActivity(intent);
            }
        });

//        To Show the Likes

        FirebaseDatabase.getInstance().getReference()
                .child("LoveStories").child(getRef(position).getKey())
                .child("Likes").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild(InitialAdder)) {
                            holder.storiesLayoutBinding.like.setImageResource(R.drawable.likeee);
                        } else {
                            holder.storiesLayoutBinding.like.setImageResource(R.drawable.heart);
                        }

                        long LikeCount = snapshot.getChildrenCount() - 1;

                        if(LikeCount >= 1) {
                            String totalLikes = String.valueOf(LikeCount);
                            holder.storiesLayoutBinding.likeCount.setText(totalLikes);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        holder.storiesLayoutBinding.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesCommentListener.Onclick(getRef(position).getKey());
            }
        });

//        To store the user's UID who has liked the Stories
        holder.storiesLayoutBinding.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String liker = FirebaseAuth.getInstance().getUid();
                testClick = true;

                FirebaseDatabase.getInstance().getReference()
                        .child("LoveStories").child(Objects.requireNonNull(getRef(position).getKey()))
                        .child("Likes").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                assert liker != null;
                                if (snapshot.hasChild(liker)) {
                                    if (testClick) {
                                        FirebaseDatabase.getInstance().getReference().child("LoveStories")
                                                .child(Objects.requireNonNull(getRef(position).getKey())).child("Likes")
                                                .child(liker).removeValue();
                                    }
                                }else{
                                    if (testClick) {
                                        FirebaseDatabase.getInstance().getReference().child("LoveStories")
                                                .child(Objects.requireNonNull(getRef(position).getKey())).child("Likes")
                                                .child(liker).
                                                push().
                                                setValue(true);
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
    public AllStoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        StoriesLayoutBinding storiesLayoutBinding = StoriesLayoutBinding.inflate(layoutInflater);
        return new AllStoriesViewHolder(storiesLayoutBinding);
    }

    public static class AllStoriesViewHolder extends RecyclerView.ViewHolder{

        StoriesLayoutBinding storiesLayoutBinding;

        public AllStoriesViewHolder(StoriesLayoutBinding binding) {
            super(binding.getRoot());
            this.storiesLayoutBinding = binding;

        }
    }
}
