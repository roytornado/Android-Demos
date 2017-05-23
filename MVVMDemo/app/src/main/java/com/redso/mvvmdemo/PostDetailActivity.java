package com.redso.mvvmdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.redso.mvvmdemo.base.BaseActivity;
import com.redso.mvvmdemo.data.Post;
import com.redso.mvvmdemo.manager.ApiManager;

public class PostDetailActivity extends BaseActivity {

  public static final String ARG_POST_ID = "post_id";
  private Post mPost;
  private CollapsingToolbarLayout mAppBarLayout;
  private TextView mDescView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_post_detail);
    Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
    mAppBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
    setSupportActionBar(toolbar);
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
    }

    mDescView = (TextView) findViewById(R.id.item_detail);

    String postId = getIntent().getStringExtra(ARG_POST_ID);
    loadPost(postId);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == android.R.id.home) {
      navigateUpTo(new Intent(this, PostListActivity.class));
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private void loadPost(String postId) {
    showLoading();
    ApiManager.sInstance.getPost(postId, new ApiManager.GetPostCallback() {
      @Override
      public void onResponse(Post post) {
        hideLoading();
        displayPost(post);
      }
    });
  }

  private void displayPost(Post post) {
    mPost = post;
    mAppBarLayout.setTitle(mPost.title);
    mDescView.setText(mPost.desc);
  }
}
