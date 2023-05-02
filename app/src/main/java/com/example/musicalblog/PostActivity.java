package com.example.musicalblog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;

public class PostActivity extends AppCompatActivity {

    EditText postTitle, postDescription;
    Button postButton;
    ImageView postImage;
    Uri imageUri = null;
    private static final int GALLERY_CODE = 100;
    private static final int CAMERA_CODE = 200;
    ProgressDialog progressDialog;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        permission();
        postTitle = findViewById(R.id.post_title);
        postDescription = findViewById(R.id.post_description);
        postButton = findViewById(R.id.post_upload);
        postImage = findViewById(R.id.post_image);
        progressDialog = new ProgressDialog(this);
        auth = FirebaseAuth.getInstance();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Új post");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);


        postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImageDialog();
            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = postTitle.getText().toString();
                String description = postDescription.getText().toString();

                if (TextUtils.isEmpty(title)) {
                    postTitle.setError("A cím kötelező!");
                } else if (TextUtils.isEmpty(description)) {
                    postDescription.setError("A leírás kötelező");
                } else {
                    uploadPost(title, description);
                }
            }
        });
    }

    private void uploadPost(String title, String desc) {
        final String currentTime = String.valueOf(System.currentTimeMillis());
        String filepath = "Post/" + "post_" + currentTime;
        progressDialog.setMessage("A post feltöltése folyamatban");
        progressDialog.show();

        if (postImage.getDrawable() != null) {
            Bitmap bitmap = ((BitmapDrawable) postImage.getDrawable()).getBitmap();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] data = byteArrayOutputStream.toByteArray();

            StorageReference ref = FirebaseStorage.getInstance().getReference().child(filepath);
            ref.putBytes(data).addOnSuccessListener(taskSnapshot -> {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful()) ;

                String downloadUri = uriTask.getResult().toString();

                if (uriTask.isSuccessful()) {
                    FirebaseUser user = auth.getCurrentUser();
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("uid", user.getUid());
                    hashMap.put("uEmail", user.getEmail());
                    hashMap.put("pId", currentTime);
                    hashMap.put("pTitle", title);
                    hashMap.put("pImage", downloadUri);
                    hashMap.put("pDescription", desc);
                    hashMap.put("pTime", currentTime);


                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
                    databaseReference.child(currentTime).setValue(hashMap).addOnSuccessListener(task -> {
                        progressDialog.dismiss();
                        Toast.makeText(PostActivity.this, "Sikeres feltöltés", Toast.LENGTH_SHORT).show();
                        postTitle.setText("");
                        postDescription.setText("");
                        postImage.setImageURI(null);
                        imageUri = null;

                        startActivity(new Intent(PostActivity.this, BlogActivity.class));

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(PostActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PostActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void pickImageDialog() {
        String[] options = {"Kamera", "Galléria"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Kérem válasszon képet");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    startCamera();
                } else {
                    pickFromGallery();
                }
            }
        });
        builder.create().show();
    }

    private void startCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Ideiglenes kép");
        contentValues.put(MediaStore.Images.Media.TITLE, "Ideiglenes leírás");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, CAMERA_CODE);

    }

    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_CODE);
    }


    private void permission() {
        Dexter.withActivity(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {/* ... */}

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
        }).check();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_CODE) {
                imageUri = data.getData();
                postImage.setImageURI(imageUri);
            }
            if (requestCode == CAMERA_CODE) {
                postImage.setImageURI(imageUri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}
