package com.mobileq.rusal.rusalapp.developer3456.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobileq.rusal.rusalapp.developer3456.R;
import com.mobileq.rusal.rusalapp.developer3456.model.Post;

import java.util.List;


public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> posts;
    Context context;
    public PostAdapter(Context context, List<Post> posts) {
        this.posts = posts;
        this.context = context;

    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        holder.onBind(posts.get(position));
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }


    public class PostViewHolder extends RecyclerView.ViewHolder {
           TextView textView;
           ImageView imagePost ;
           ImageView imageLike ;
        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            textView =itemView.findViewById(R.id.txt_PostDescription);
            imagePost =itemView.findViewById(R.id.img_postImage);
            imageLike =itemView.findViewById(R.id.img_like);



        }

        public void onBind(Post item) {

        textView.setText(item.postDec);
        imagePost.setImageResource(item.postImage);
        if (item.isLike){
            imageLike.setImageResource(R.drawable.heart_red);
        }else {
            imageLike.setImageResource(R.drawable.heart);

        }
//            Picasso.get()
//                    .load(item.photo1)
//                    .resize(50, 50)
//                    .centerCrop().error(R.drawable.ic_launcher_background)
//                    .into(imageView1);
//            Picasso.get()
//                    .load(item.photo2)
//                    .resize(50, 50)
//                    .centerCrop().error(R.drawable.ic_launcher_background)
//                    .into(imageView2);
        }
    }


}
