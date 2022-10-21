package CategoryFragment.LoveStories;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.srisu.R;
import com.example.srisu.databinding.FragmentStoriesBinding;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Locale;
import java.util.Objects;


public class StoriesFragment extends Fragment {

    FragmentStoriesBinding storiesBinding;
    FirebaseAuth firebaseAuth;
    Dialog dialog;
    EditText BookName, Author;
    Button Upload;
    private static final int STORAGE_PERMISSION_CODE = 11;
    private static final int PDF = 101;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    Uri PDF_File;
    LoveStoriesAdapter loveStoriesAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        storiesBinding = FragmentStoriesBinding.inflate(inflater, container, false);
        firebaseAuth = FirebaseAuth.getInstance();

        storiesBinding.recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireContext(),
                DividerItemDecoration.VERTICAL);
        storiesBinding.recycler.addItemDecoration(dividerItemDecoration);

        FirebaseRecyclerOptions<StoriesModel> options = new FirebaseRecyclerOptions.Builder<StoriesModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference().
                        child("LoveStories"), StoriesModel.class)
                .build();

        loveStoriesAdapter = new LoveStoriesAdapter(options, getContext());
        storiesBinding.recycler.setAdapter(loveStoriesAdapter);

        dialog = new Dialog(getContext());
        dialog.getWindow().setContentView(R.layout.addstories_layout);
        BookName = dialog.findViewById(R.id.BookName);
        Author = dialog.findViewById(R.id.author);
        Upload = dialog.findViewById(R.id.uploadPDF);

        FirebaseDatabase.getInstance().getReference().child("Name_Id")
                .child(Objects.requireNonNull(firebaseAuth.getUid())).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String IsAdmin = snapshot.child("Admin").getValue(String.class);

                        assert IsAdmin != null;

                        if (IsAdmin.equals("Yes")) {
                            storiesBinding.addPdf.setVisibility(View.VISIBLE);
                        } else {
                            storiesBinding.addPdf.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        storiesBinding.addPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();

                Upload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckPermission();
                    }
                });

            }
        });


        return storiesBinding.getRoot();
    }

    private void CheckPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            pickPDF();
        } else {
            RequestPermission();
        }
    }

    private void RequestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(getContext())
                    .setMessage("Browse the PDF")
                    .setTitle("Uploader")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) getContext(),
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions((Activity) getContext(), new
                    String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickPDF();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void pickPDF() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(Intent.createChooser(intent, "Open with"), PDF);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PDF && resultCode == RESULT_OK) {
            assert data != null;
            PDF_File = data.getData();
            uploadPDFToFirebase(PDF_File);
        }
    }

    private void uploadPDFToFirebase(Uri File) {

        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("SriSu");
        progressDialog.show();
        String BookTitle = BookName.getText().toString();

        storageReference = FirebaseStorage.getInstance().getReference().child("LoveStoriesPdf")
                        .child(firebaseAuth.getCurrentUser() + BookTitle);

        storageReference.putFile(File)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String BooksName = BookName.getText().toString();
                                String author = Author.getText().toString();
                                String LowercaseBookName = BookName.getText().toString().toLowerCase(Locale.ROOT);

                                StoriesModel storiesModel = new StoriesModel("",BooksName, author, LowercaseBookName, uri.toString());

                                databaseReference = FirebaseDatabase.getInstance().getReference().child("LoveStories");
                                databaseReference.child(BooksName).setValue(storiesModel);

                                BookName.setText("");
                                Author.setText("");
                                dialog.dismiss();

                                progressDialog.dismiss();
                            }

                        });
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        float percent = (100 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        progressDialog.setMessage("Uploading : " + (int) percent + "%");
                    }
                });
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onStart() {
        super.onStart();
        loveStoriesAdapter.notifyDataSetChanged();
        loveStoriesAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        loveStoriesAdapter.stopListening();
    }


}