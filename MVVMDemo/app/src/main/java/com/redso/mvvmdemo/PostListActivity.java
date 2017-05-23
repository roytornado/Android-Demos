package com.redso.mvvmdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.redso.mvvmdemo.adapter.PostsRecyclerViewAdapter;
import com.redso.mvvmdemo.base.BaseActivity;
import com.redso.mvvmdemo.data.Post;
import com.redso.mvvmdemo.manager.ApiManager;

import java.util.ArrayList;

public class PostListActivity extends BaseActivity {

  private RecyclerView mRecyclerView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_post_list);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    toolbar.setTitle(getTitle());
    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(getBaseContext(), PostCreateActivity.class);
        startActivity(intent);
      }
    });
    mRecyclerView = (RecyclerView) findViewById(R.id.item_list);
    loadPosts();
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (mRecyclerView.getAdapter() != null) {
      mRecyclerView.getAdapter().notifyDataSetChanged();
    }
  }

  private void loadPosts() {
    showLoading();
    ApiManager.sInstance.getPosts(new ApiManager.GetPostsCallback() {
      @Override
      public void onResponse(ArrayList<Post> posts) {
        hideLoading();
        displayPosts(posts);
      }
    });
  }

  private void displayPosts(ArrayList<Post> posts) {
    mRecyclerView.setAdapter(new PostsRecyclerViewAdapter(posts));
  }
}
