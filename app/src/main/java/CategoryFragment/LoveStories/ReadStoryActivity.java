package CategoryFragment.LoveStories;


import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;


import com.example.srisu.databinding.ActivityStoryReadBinding;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ReadStoryActivity extends AppCompatActivity {


    ActivityStoryReadBinding readStoryBinding;
    String mURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        readStoryBinding = ActivityStoryReadBinding.inflate(getLayoutInflater());
        setContentView(readStoryBinding.getRoot());

        mURL = getIntent().getStringExtra("PDFUrl");

        if(mURL != null) {
            new RetrievePDFStream().execute(mURL);
        }
    }

   @SuppressLint("StaticFieldLeak")
   private class RetrievePDFStream extends AsyncTask<String,Void, InputStream> { // This method will show the story in PDF view
        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream = null;

            try {

                URL urlx = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) urlx.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());

                }
            } catch (IOException e) {
                return null;
            }
            return inputStream;

        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            readStoryBinding.pdfView.fromStream(inputStream).
                    load();
        }
    }
}