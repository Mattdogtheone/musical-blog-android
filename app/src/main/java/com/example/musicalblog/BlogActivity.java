package com.example.musicalblog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.musicalblog.Adapter.PostAdapter;
import com.example.musicalblog.Model.PostModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BlogActivity extends AppCompatActivity {

    FirebaseAuth auth;
    RecyclerView recyclerView;
    List<PostModel> postModelList;
    PostAdapter postAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);
        auth = FirebaseAuth.getInstance();
        recyclerView = findViewById(R.id.recycleview);

        LinearLayoutManager linlay = new LinearLayoutManager(this);
        linlay.setStackFromEnd(true);
        linlay.setReverseLayout(true);

        recyclerView.setLayoutManager(linlay);

        postModelList = new ArrayList<>();

        loadPosts();

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Intent intent = new Intent(BlogActivity.this, PostUpdateDeleteActivity.class);
                        intent.putExtra("image", postModelList.get(position).getpImage());
                        intent.putExtra("title", postModelList.get(position).getpTitle());
                        intent.putExtra("desc", postModelList.get(position).getpDescription());
                        intent.putExtra("id", postModelList.get(position).getpId());

                        startActivity(intent);
                    }

                    @Override public void onLongItemClick(View view, int position) {
                    }
                })
        );

    }

    private void loadPosts() {
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Posts");
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postModelList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                     PostModel postModel = ds.getValue(PostModel.class);
                     postModelList.add(postModel);
                     postAdapter = new PostAdapter(BlogActivity.this, postModelList);
                     recyclerView.setAdapter(postAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BlogActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.blog_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.logout) {
            auth.signOut();
            startActivity(new Intent(BlogActivity.this, MainActivity.class));
        }
        if (item.getItemId() == R.id.new_post){
            startActivity(new Intent(BlogActivity.this, PostActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}