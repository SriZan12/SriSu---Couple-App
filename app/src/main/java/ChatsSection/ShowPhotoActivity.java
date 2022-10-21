package ChatsSection;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.srisu.R;
import com.example.srisu.databinding.ActivityShowPhotoBinding;

public class ShowPhotoActivity extends AppCompatActivity {

    ActivityShowPhotoBinding showPhotoBinding;
    String photoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showPhotoBinding = ActivityShowPhotoBinding.inflate(getLayoutInflater());
        setContentView(showPhotoBinding.getRoot());

        hideStatusBar();

        getSupportActionBar().hide();

        photoUrl = getIntent().getStringExtra("photo");
        Glide.with(this)
                .load(Uri.parse(photoUrl))
                .placeholder(R.drawable.placeholder)
                .into(showPhotoBinding.myZoomImageView);

        showPhotoBinding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadImage();
            }
        });
    }

    private void hideStatusBar() {
        View decorView = getWindow().getDecorView();
// Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

    }

    private void downloadImage(){
        DownloadManager downloadManager;

        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        Uri uri = Uri.parse(photoUrl);

        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle("Downloaded")
                .setMimeType("image/jpeg")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES,"SriSu");

        downloadManager.enqueue(request);
        Toast.makeText(ShowPhotoActivity.this,"Download Completed",Toast.LENGTH_SHORT).show();

    }
}