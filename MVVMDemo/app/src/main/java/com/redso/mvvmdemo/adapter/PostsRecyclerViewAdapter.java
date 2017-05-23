package com.redso.mvvmdemo.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.redso.mvvmdemo.PostDetailActivity;
import com.redso.mvvmdemo.R;
import com.redso.mvvmdemo.data.Post;

import java.util.List;

public class PostsRecyclerViewAdapter extends RecyclerView.Adapter<PostsRecyclerViewAdapter.ViewHolder> {
  private final List<Post> mPosts;

  public PostsRecyclerViewAdapter(List<Post> posts) {
    mPosts = posts;
  }

  @Override
  public PostsRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_content, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(PostsRecyclerViewAdapter.ViewHolder holder, int position) {
    Post post = mPosts.get(position);
    holder.setContent(post);
  }

  @Override
  public int getItemCount() {
    return mPosts.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    public final View mView;
    public final ImageView mImageView;
    public final TextView mIdView;
    public final TextView mContentView;
    public Post mPost;

    public ViewHolder(View view) {
      super(view);
      mView = view;
      mIdView = (TextView) view.findViewById(R.id.id);
      mContentView = (TextView) view.findViewById(R.id.content);
      mImageView = (ImageView) view.findViewById(R.id.image);
    }

    public void setContent(Post post) {
      mPost = post;
      mIdView.setText(mPost.postId);
      mContentView.setText(mPost.title);
      Glide.with(mImageView).load(mPost.imageUrl).into(mImageView);
      mView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Intent intent = new Intent(v.getContext(), PostDetailActivity.class);
          intent.putExtra(PostDetailActivity.ARG_POST_ID, mPost.postId);
          v.getContext().startActivity(intent);
        }
      });
    }

  }
}
