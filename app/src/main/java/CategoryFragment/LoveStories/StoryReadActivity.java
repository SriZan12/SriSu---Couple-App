package CategoryFragment.LoveStories;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.srisu.databinding.ActivityStoryReadBinding;

public class StoryReadActivity extends AppCompatActivity {

    ActivityStoryReadBinding storyReadBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storyReadBinding = ActivityStoryReadBinding.inflate(getLayoutInflater());
        setContentView(storyReadBinding.getRoot());
    }
}