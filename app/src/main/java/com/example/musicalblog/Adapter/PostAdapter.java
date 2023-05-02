package com.example.musicalblog.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicalblog.Model.PostModel;
import com.example.musicalblog.R;

import java.util.List;


public class PostAdapter extends RecyclerView.Adapter<PostAdapter.Holder> {

    Context context;
    List<PostModel> postModels;

    public PostAdapter(Context context, List<PostModel> postModels) {
        this.context = context;
        this.postModels = postModels;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.blog_posts, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        String title = postModels.get(position).getpTitle();
        String desc = postModels.get(position).getpDescription();
        String img = postModels.get(position).getpImage();
        holder.blogTitle.setText(title);
        holder.blogDescription.setText(desc);
        Glide.with(context).load(img).into(holder.blogImage);
    }

    @Override
    public int getItemCount() {
        return postModels.size();
    }

    class Holder extends RecyclerView.ViewHolder{

        ImageView blogImage;
        TextView blogTitle, blogDescription;

        public Holder(@NonNull View itemView) {
            super(itemView);

            blogImage = itemView.findViewById(R.id.blog_image);
            blogTitle = itemView.findViewById(R.id.blog_title);
            blogDescription = itemView.findViewById(R.id.blog_desc);

        }
    }
}
