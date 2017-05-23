package com.redso.mvvmdemo.base;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
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

  protected void showAlertMessage(String message) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setMessage(message);
    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
      }
    });
    builder.show();
  }
}
