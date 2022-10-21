package CategoryFragment.Facts;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.srisu.R;
import com.example.srisu.databinding.FragmentFactsBinding;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import CategoryFragment.Facts.MainFactModel;
import retrofit2.Call;
import retrofit2.Callback;

public class FactsFragment extends Fragment {

   FragmentFactsBinding factsBinding;
   FactsApiInstance factsApiInstance;
   String ApiKey = "7wBw062CJFYJEND2ZkE0vA==brI2eJZMFtXs8VqS";


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        factsBinding =  FragmentFactsBinding.inflate(inflater, container, false);

//        showFacts();
        ShowFacts();
        return factsBinding.getRoot();
    }

    private void showFacts(){
       String Url = "https://api.api-ninjas.com/v1/facts?limit=10" + "7wBw062CJFYJEND2ZkE0vA==brI2eJZMFtXs8VqS";

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, Url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
//                        textView.setText("Response: " + response.toString());
                        Toast.makeText(getContext(), "Responded", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Toast.makeText(getContext(), "Not Responded!", Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(jsonObjectRequest);

    }

    private void ShowFacts(){
        factsApiInstance = FactsRetrofitInstance.getFactsRetrofit().create(FactsApiInstance.class);

        factsApiInstance.allFacts(10).enqueue(new Callback<MainFactModel>() {
            @Override
            public void onResponse(Call<MainFactModel> call, retrofit2.Response<MainFactModel> response) {
                if(response.isSuccessful()){
                    Toast.makeText(getContext(), "Hello", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getContext(),"Not Hello",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MainFactModel> call, Throwable t) {

            }
        });
    }


}