package com.redso.mvvmdemo;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.redso.mvvmdemo.data.Post;
import com.redso.mvvmdemo.data.ViewModelStatus;
import com.redso.mvvmdemo.manager.ApiManager;

import java.util.ArrayList;
import java.util.List;

public class MVVMPostListViewModel extends ViewModel {

  private MutableLiveData<List<Post>> postsData;
  private MutableLiveData<ViewModelStatus> status = new MutableLiveData<ViewModelStatus>();
  private ViewModelStatus statusData = new ViewModelStatus();

  public LiveData<ViewModelStatus> getStatus() {
    return status;
  }

  public LiveData<List<Post>> getPosts() {
    if (postsData == null) {
      postsData = new MutableLiveData<List<Post>>();
      loadPosts();
    }

    return postsData;
  }

  private void loadPosts() {
    statusData.isLoadingList = true;
    status.setValue(statusData);
    ApiManager.sInstance.getPosts(new ApiManager.GetPostsCallback() {
      @Override
      public void onResponse(ArrayList<Post> posts) {
        postsData.setValue(posts);
        statusData.isLoadingList = false;
        status.setValue(statusData);
      }
    });
  }
}
