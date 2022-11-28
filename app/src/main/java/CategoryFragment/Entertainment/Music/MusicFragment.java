package CategoryFragment.Entertainment.Music;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.srisu.R;
import com.example.srisu.databinding.FragmentMusicBinding;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import CategoryFragment.Entertainment.Movie.ClickListener.onMovieCommentListener;


public class MusicFragment extends Fragment {

    FragmentMusicBinding musicBinding;
    FirebaseAuth firebaseAuth;
    MusicAdapter musicAdapter;
    boolean bottomSheetDisplayed = false;
    MusicCommentAdapter commentAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        musicBinding = FragmentMusicBinding.inflate(inflater, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        musicBinding.musicRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL);
        musicBinding.musicRecycler.addItemDecoration(dividerItemDecoration);

        FirebaseRecyclerOptions<MusicModel> options =
                new FirebaseRecyclerOptions.Builder<MusicModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().
                                child("Music"), MusicModel.class)
                        .build(); // Getting all the musicList from Music node in Database.

        musicAdapter = new MusicAdapter(options, getContext(),onMovieCommentListener);
        musicBinding.musicRecycler.setAdapter(musicAdapter);

        musicBinding.addMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              addMusic();
            }
        });

        return musicBinding.getRoot();
    }

    private void addMusic(){
        Dialog dialog = new Dialog(getContext());
        dialog.getWindow().setContentView(R.layout.add_music);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show(); //Showing the add music dialog

        EditText musicName = dialog.findViewById(R.id.musicTitle);
        EditText singer = dialog.findViewById(R.id.singerName);
        Button recommend = dialog.findViewById(R.id.recommend);

        recommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String MusicTitle = musicName.getText().toString();
                String SingerName = singer.getText().toString();
                List<MusicCommentModel> commentModelList = new ArrayList<>();

                if (MusicTitle.isEmpty()) {
                    musicName.setError("Required");
                    musicName.setFocusable(true);

                } else if (SingerName.isEmpty()) {
                    singer.setError("Required");
                    singer.setFocusable(true);
                }

                String randomPushKey = FirebaseDatabase.getInstance().getReference().push().getKey();

                MusicModel musicModel = new MusicModel(MusicTitle, firebaseAuth.getUid(), SingerName, commentModelList, "0");

                assert randomPushKey != null;
                FirebaseDatabase.getInstance().getReference().child("Music").child(randomPushKey)
                        .setValue(musicModel);

                HashMap<String, Object> likesMap = new HashMap<>();
                likesMap.put("Likers", "");

                FirebaseDatabase.getInstance().getReference().child("Music").child(randomPushKey)
                        .child("Likes")
                        .setValue(likesMap);

                dialog.dismiss();

            }
        });
    }

    private final CategoryFragment.Entertainment.Movie.ClickListener.onMovieCommentListener onMovieCommentListener = new onMovieCommentListener() {
        @Override
        public void Onclick(String MovieTitle) {

            displayBottomSheet(MovieTitle);

            Log.d(TAG, "Onclick: " + MovieTitle);
        }
    };

    private void displayBottomSheet(String Key) { // This function will show all the comment list through a bottomView

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        View layout = LayoutInflater.from(getContext()).inflate(R.layout.comment_box, null);
        bottomSheetDialog.setContentView(layout);
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.show();

        bottomSheetDisplayed = true;


        Log.d(TAG, "displayBottomSheet: " + Key);

        ImageView imageView = layout.findViewById(R.id.MakeComment);
        EditText commentText = layout.findViewById(R.id.commentText);
        RecyclerView commentRecycler = layout.findViewById(R.id.cmtListRecycler);

        commentRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL);
        musicBinding.musicRecycler.addItemDecoration(dividerItemDecoration);

        FirebaseRecyclerOptions<MusicCommentModel> CommentOptions =
                new FirebaseRecyclerOptions.Builder<MusicCommentModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().
                                child("Music").child(Key).child("Comments"), MusicCommentModel.class)
                        .build();// Getting all the music Comment from the Music-Comments node.


        commentAdapter = new MusicCommentAdapter(CommentOptions, getContext(), Key);
        commentRecycler.setAdapter(commentAdapter);

        commentAdapter.startListening();


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String CommentText = commentText.getText().toString();

                MusicCommentModel commentModel = new MusicCommentModel(CommentText, firebaseAuth.getUid());

                FirebaseDatabase.getInstance().getReference().child("Music")
                        .child(Key)
                        .child("Comments")
                        .push()
                        .setValue(commentModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                commentText.setText("");
                            }
                        });
            }
        });
    }


    @SuppressLint("NotifyDataSetChanged")
    public void onStart() {
        super.onStart();
        musicAdapter.notifyDataSetChanged();
        musicAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        musicAdapter.stopListening();
    }
}