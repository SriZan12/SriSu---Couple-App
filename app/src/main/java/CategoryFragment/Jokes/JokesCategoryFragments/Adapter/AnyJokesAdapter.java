package CategoryFragment.Jokes.JokesCategoryFragments.Adapter;


import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.srisu.databinding.JokesCategoryBinding;

import java.util.List;

import CategoryFragment.Jokes.Model.JokesModel;

public class AnyJokesAdapter extends RecyclerView.Adapter<AnyJokesAdapter.AnyJokesViewHolder>{

    List<JokesModel> JokesList;
    Context context;
    String type;
    String FullJoke;
    ClickListener clickListener;

    public AnyJokesAdapter(Context context, List<JokesModel> anyJokes, ClickListener clkListener) {
        this.context = context;
        this.JokesList = anyJokes;
        this.clickListener = clkListener;
    }

    @NonNull
    @Override
    public AnyJokesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        JokesCategoryBinding jokesCategoryBinding = JokesCategoryBinding.inflate(layoutInflater);
        return new AnyJokesViewHolder(jokesCategoryBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull AnyJokesViewHolder holder, @SuppressLint("RecyclerView") int position) {

        type = JokesList.get(position).getType();

        if(type.equals("twopart")) {
            holder.jokesCategoryBinding.setup.setText(JokesList.get(position).getSetUp());
            holder.jokesCategoryBinding.delivery.setText(JokesList.get(position).getDelivery());

        }else{
            holder.jokesCategoryBinding.setup.setText(JokesList.get(position).getJokes());
            holder.jokesCategoryBinding.delivery.setVisibility(View.GONE);

        }



        holder.jokesCategoryBinding.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(type.equals("twopart")) {
                    FullJoke = JokesList.get(position).getSetUp()
                            +"\n" + JokesList.get(holder.getAbsoluteAdapterPosition()).getDelivery();
                }else{
                    FullJoke = JokesList.get(holder.getAbsoluteAdapterPosition()).getJokes();
                }
                clickListener.CopyJoke(FullJoke);
                Log.d(TAG, "onClick: "+ FullJoke);
            }
        });
    }

    @Override
    public int getItemCount() {
        return JokesList.size();
    }

    public static class AnyJokesViewHolder extends RecyclerView.ViewHolder {

        JokesCategoryBinding jokesCategoryBinding;

        public AnyJokesViewHolder(JokesCategoryBinding jokesCategoryBinding) {
            super(jokesCategoryBinding.getRoot());
            this.jokesCategoryBinding = jokesCategoryBinding;
        }
    }
}
