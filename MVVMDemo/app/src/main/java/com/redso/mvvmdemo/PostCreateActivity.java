package com.redso.mvvmdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.redso.mvvmdemo.base.BaseActivity;
import com.redso.mvvmdemo.manager.ApiManager;

public class PostCreateActivity extends BaseActivity {

  private TextView postIdTextView;
  private TextInputLayout postTitleTextInput;
  private TextInputLayout postDescTextInput;
  private AppCompatCheckBox agreeCheckBox;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_post_create);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    toolbar.setTitle(getTitle());
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
    }

    postIdTextView = (TextView) findViewById(R.id.postIdTextView);
    postTitleTextInput = (TextInputLayout) findViewById(R.id.postTitleTextInput);
    postDescTextInput = (TextInputLayout) findViewById(R.id.postDescTextInput);
    agreeCheckBox = (AppCompatCheckBox) findViewById(R.id.agreeCheckBox);

    postTitleTextInput.getEditText().addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override
      public void afterTextChanged(Editable s) {
        if (s.length() > 0) {
          postIdTextView.setText("" + s.length());
        } else {
          postIdTextView.setText("");
        }
      }
    });
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == android.R.id.home) {
      onBackPressed();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  public void submit(View view) {
    postTitleTextInput.setError("");
    postDescTextInput.setError("");
    if (postTitleTextInput.getEditText().getText().length() == 0) {
      postTitleTextInput.setError("Please input title");
      return;
    }
    if (postDescTextInput.getEditText().getText().length() == 0) {
      postDescTextInput.setError("Please input title");
      return;
    }
    if (!agreeCheckBox.isChecked()) {
      showAlertMessage("Please agree T&C");
      return;
    }
    showLoading();
    // It's just mocked-up call. Nothing real here
    ApiManager.sInstance.createPost(new ApiManager.CreatePostCallback() {
      @Override
      public void onResponse() {
        hideLoading();
        onBackPressed();
      }
    });
  }
}
