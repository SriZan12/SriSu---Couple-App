package CategoryFragment.Bored;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.srisu.R;
import com.example.srisu.databinding.FragmentBoredBinding;

import org.json.JSONException;
import org.json.JSONObject;

public class BoredFragment extends Fragment {

    FragmentBoredBinding boredBinding;
    String Category;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        boredBinding =  FragmentBoredBinding.inflate(inflater, container, false);

        boredBinding.random.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Category = "Random";
                showDialogForTask("random");

            }
        });

        boredBinding.education.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Category = "Education";
                showDialogForTask("education");
            }
        });

        boredBinding.recreation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Category = "Recreation";
                showDialogForTask("recreational");
            }
        });

        boredBinding.social.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Category = "Social";
                showDialogForTask("social");

            }
        });

        boredBinding.busy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Category = "Busy";
                showDialogForTask("busywork");

            }
        });

        boredBinding.charity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Category = "Charity";
                showDialogForTask("charity");

            }
        });

        boredBinding.music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Category = "Music";
                showDialogForTask("music");

            }
        });

        boredBinding.cooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Category = "Cooking";
                showDialogForTask("cooking");

            }
        });

        boredBinding.diy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Category = "Diy";
                showDialogForTask("diy");

            }
        });

        boredBinding.relax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Category = "Relax";
                showDialogForTask("relaxation");

            }
        });

        return boredBinding.getRoot();
    }

    private void showDialogForTask(String type){

        final Dialog dialog = new Dialog(getContext());
        dialog.getWindow().setContentView(R.layout.bored_task_layout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();

        Button generateTask = dialog.findViewById(R.id.generate);
        TextView taskView = dialog.findViewById(R.id.taskView);

        generateTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (Category){
                    case "Random":
                        showRandomTask(taskView);
                        break;

                    case "Recreation":
                    case "Education":
                    case "Cooking":
                    case "Social":
                    case "Diy":
                    case "Music":
                    case "Busy":
                    case "Charity":
                    case "Relax":
                        showTypeTask(taskView,type);
                        break;
                }

            }
        });
    }

    private void showRandomTask(TextView taskView) { //--> Calling the Api for Random Task.
         String url = "https://www.boredapi.com/api/activity";

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        String task = null;
                        try {

                            task = response.getString("activity");
                            taskView.setText(task);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG, "onResponse: random: " + task );

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    private void showTypeTask(TextView taskView,String type){ //--> Calling the Api as per the user's Request

        String url = "https://www.boredapi.com/api/activity?" + "type=" + type;

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        String task = null;

                        try {
                            task = response.getString("activity");
                            taskView.setText(task);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//

                        Log.d(TAG, "onResponse: random: " + task );

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

        requestQueue.add(jsonObjectRequest);
    }
}