package CategoryFragment.LoveStories;

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
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class LoveStoriesAdapter extends FirebaseRecyclerAdapter<StoriesModel, LoveStoriesAdapter.AllStoriesViewHolder> {

    Context context;

    public LoveStoriesAdapter(@NonNull FirebaseRecyclerOptions<StoriesModel> options, Context thisContext) {
        super(options);
        context = thisContext;
    }

    @Override
    protected void onBindViewHolder(@NonNull AllStoriesViewHolder holder, int position, @NonNull StoriesModel model) {

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
                Intent intent = new Intent(context,StoryReadActivity.class);
                intent.putExtra("PDFUrl",model.getPDFUrl());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                holder.storiesLayoutBinding.mainLinear.getContext().startActivity(intent);
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
