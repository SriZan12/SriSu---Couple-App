package CategoryFragment.Memes.Adapter;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.srisu.R;
import com.example.srisu.databinding.MemesLayoutBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import CategoryFragment.Memes.Models.Memes;

public class MemeAdapter extends RecyclerView.Adapter<MemeAdapter.MemesViewHolder> {

    Context context1;
    List<Memes> MemesList;
    String ImageUrl;
    ClickListener memeClickListener;

    public MemeAdapter(Context context, List<Memes> AllMemes,ClickListener clickListener) {
        this.context1 = context;
        this.MemesList = AllMemes;
        this.memeClickListener = clickListener;

    }

    @NonNull
    @Override
    public MemesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        MemesLayoutBinding memesLayoutBinding = MemesLayoutBinding.inflate(layoutInflater);
        return new MemesViewHolder(memesLayoutBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MemesViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Glide.with(context1)
                .load(MemesList.get(position).getUrl())
                .placeholder(R.drawable.placeholder)
                .into(holder.memesLayoutBinding.memesImage);

        holder.memesLayoutBinding.memesTitle.setText(MemesList.get(position).getTitle());

        holder.memesLayoutBinding.shareInside.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageUrl = MemesList.get(position).getUrl();
                memeClickListener.Send(ImageUrl);
            }
        });

        holder.memesLayoutBinding.shareOutside.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageUrl = MemesList.get(position).getUrl();
                memeClickListener.Share(ImageUrl);
            }
        });

    }

    @Override
    public int getItemCount() {
        return MemesList.size();
    }

    public static class MemesViewHolder extends RecyclerView.ViewHolder {

        MemesLayoutBinding memesLayoutBinding;

        public MemesViewHolder(MemesLayoutBinding memeLayoutBinding) {
            super(memeLayoutBinding.getRoot());

            this.memesLayoutBinding = memeLayoutBinding;
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

            FirebaseDatabase.getInstance().getReference().child("Users")
                    .child(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String relationship = snapshot.child("relationship").getValue(String.class);
                            if(relationship.equals("Single")){
                                memeLayoutBinding.shareInside.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }
}
