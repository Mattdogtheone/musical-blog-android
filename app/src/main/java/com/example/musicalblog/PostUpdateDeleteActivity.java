package com.example.musicalblog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

public class PostUpdateDeleteActivity extends AppCompatActivity {

    FirebaseAuth auth;
    ImageView postImage;
    TextView postTitle, postDesc;
    String image, title, desc;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_update_delete);

        auth = FirebaseAuth.getInstance();
        image = getIntent().getStringExtra("image");
        title = getIntent().getStringExtra("title");
        desc = getIntent().getStringExtra("desc");
        id = getIntent().getStringExtra("id");

        postImage = findViewById(R.id.blog_image_update);
        postTitle = findViewById(R.id.blog_title_update);
        postDesc = findViewById(R.id.blog_desc_update);

        Glide.with(this).load(image).into(postImage);
        postTitle.setText(title);
        postDesc.setText(desc);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.blog_post_update_delete_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.logout) {
            auth.signOut();
            startActivity(new Intent(PostUpdateDeleteActivity.this, MainActivity.class));
        }
        if (item.getItemId() == R.id.edit_post) {
            updatePost(id);
            startActivity(new Intent(PostUpdateDeleteActivity.this, BlogActivity.class));
        }
        if (item.getItemId() == R.id.delete_post) {
            deletePost(id);
            startActivity(new Intent(PostUpdateDeleteActivity.this, BlogActivity.class));
        }
        if (item.getItemId() == R.id.back_button){
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private void updatePost(String id) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(id);
        Map<String, Object> post = new HashMap<>();
        post.put("pTitle", postTitle.getText().toString());
        post.put("pDescription", postDesc.getText().toString());
        ref.updateChildren(post);
    }

    private void deletePost(String id) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(id);
        ref.removeValue();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(PostUpdateDeleteActivity.this, BlogActivity.class));
    }
}