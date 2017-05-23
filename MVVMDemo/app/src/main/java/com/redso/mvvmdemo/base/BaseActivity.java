package com.redso.mvvmdemo.base;


import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {
  ProgressDialog progressDialog;

  protected void showLoading() {
    if (progressDialog == null) {
      progressDialog = new ProgressDialog(this);
      progressDialog.show();
    }
  }

  protected void hideLoading() {
    if (progressDialog != null) {
      progressDialog.dismiss();
    }
  }
}
