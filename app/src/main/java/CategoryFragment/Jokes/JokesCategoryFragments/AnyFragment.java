package CategoryFragment.Jokes.JokesCategoryFragments;

import static android.content.ContentValues.TAG;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.srisu.databinding.FragmentAnyBinding;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import CategoryFragment.Jokes.Api.JokesApiInstance;
import CategoryFragment.Jokes.Api.JokesRetrofitInstance;
import CategoryFragment.Jokes.JokesCategoryFragments.Adapter.AnyJokesAdapter;
import CategoryFragment.Jokes.JokesCategoryFragments.Adapter.ClickListener;
import CategoryFragment.Jokes.Model.JokesModel;
import CategoryFragment.Jokes.Model.MainModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AnyFragment extends Fragment {

    FragmentAnyBinding anyBinding;
    JokesApiInstance apiInstance;
    List<JokesModel> AnyJokes = new ArrayList<>();
    AnyJokesAdapter anyJokesAdapter;
    ClickListener clickListener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        anyBinding =  FragmentAnyBinding.inflate(inflater, container, false);

        showJokes();

        return anyBinding.getRoot();
    }

    private void showJokes() {
        apiInstance = JokesRetrofitInstance.getRetrofit().create(JokesApiInstance.class);

        apiInstance.getJokes("https://v2.jokeapi.dev/joke/Any?amount=10")
                .enqueue(new Callback<MainModel>() {
            @Override
            public void onResponse(@NonNull Call<MainModel> call, @NonNull Response<MainModel> response) {
                if(response.isSuccessful()){

                    assert response.body() != null;
                    AnyJokes = response.body().getJokesModelList();

                    Log.d(TAG, "onResponse: " +  AnyJokes.size());
                    Log.d(TAG, "onResponse: " + Arrays.toString(AnyJokes.toArray()));

                    clickListener = new ClickListener() {
                        @Override
                        public void CopyJoke(String text) {
                            ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("label", text);
                            clipboard.setPrimaryClip(clip);
                            Toast.makeText(getContext(), "Copied to Clipboard", Toast.LENGTH_SHORT).show();
                        }
                    };

                    anyBinding.anyRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
                    anyJokesAdapter = new AnyJokesAdapter(getContext(),AnyJokes,clickListener);
                    anyBinding.anyRecycler.setAdapter(anyJokesAdapter);

                }else{
                    Toast.makeText(getContext(), "Not Responded!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MainModel> call, Throwable t) {
                Toast.makeText(getContext(), "Turn on Internet!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}