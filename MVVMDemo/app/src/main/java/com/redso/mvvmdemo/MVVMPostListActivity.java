package com.redso.mvvmdemo;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.redso.mvvmdemo.adapter.PostsRecyclerViewAdapter;
import com.redso.mvvmdemo.base.BaseActivity;
import com.redso.mvvmdemo.data.Post;

import java.util.List;

public class MVVMPostListActivity extends BaseActivity {

  private RecyclerView mRecyclerView;
  private MVVMPostListViewModel viewModel;

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
    viewModel = ViewModelProviders.of(this).get(MVVMPostListViewModel.class);
    viewModel.getPosts().observe(this, this::displayPosts);
    viewModel.getStatus().observe(this, status -> {
      if (status.isLoadingList) {
        showLoading();
      } else {
        hideLoading();
      }
    });
  }

  private void displayPosts(List<Post> posts) {
    mRecyclerView.setAdapter(new PostsRecyclerViewAdapter(posts));
  }
}
