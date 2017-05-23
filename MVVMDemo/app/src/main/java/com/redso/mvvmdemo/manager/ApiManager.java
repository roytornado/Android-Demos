package com.redso.mvvmdemo.manager;

import android.os.Handler;
import android.os.Looper;

import com.redso.mvvmdemo.data.Post;

import java.util.ArrayList;

// This is just mocked-up API Client to simulate the async calls for network operations.
public class ApiManager {

  static public ApiManager sInstance = new ApiManager();

  public interface GetPostsCallback {
    void onResponse(ArrayList<Post> posts);
  }

  public void getPosts(final GetPostsCallback callback) {
    new Thread() {
      @Override
      public void run() {
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        // Return results in UI Thread
        new Handler(Looper.getMainLooper()).post(new Runnable() {
          @Override
          public void run() {
            ArrayList<Post> posts = new ArrayList<Post>();
            addDummyPosts(posts);
            callback.onResponse(posts);
          }
        });
      }
    }.start();
  }

  public interface GetPostCallback {
    void onResponse(Post post);
  }

  public void getPost(final String postId, final GetPostCallback callback) {
    new Thread() {
      @Override
      public void run() {
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        // Return results in UI Thread
        new Handler(Looper.getMainLooper()).post(new Runnable() {
          @Override
          public void run() {
            callback.onResponse(createDummyPost(Integer.parseInt(postId)));
          }
        });
      }
    }.start();
  }

  public interface CreatePostCallback {
    void onResponse();
  }

  public void createPost(final CreatePostCallback callback) {
    new Thread() {
      @Override
      public void run() {
        try {
          Thread.sleep(2000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        // Return results in UI Thread
        new Handler(Looper.getMainLooper()).post(new Runnable() {
          @Override
          public void run() {
            callback.onResponse();
          }
        });
      }
    }.start();
  }


  private void addDummyPosts(ArrayList<Post> posts) {
    for (int i = 1; i <= 20; i++) {
      posts.add(createDummyPost(i));
    }
  }

  private static Post createDummyPost(int position) {
    String dummyDesc = "Red Soldier Limited: Expert in building iPhone apps, Android apps, Mobile Web, scalable backend on Cloud Platforms, and integrating them all together.";
    String dummyImageUrl1 = "https://lh3.googleusercontent.com/2e9FAnwgd9P0BvMr-lIFyJcwPA8jm3WiX_sP44pTx17wdwzN4jc3BmQW72bl-BPkM--4=w300-rw";
    String dummyImageUrl2 = "https://lh3.googleusercontent.com/_87UoYWY1uqcEJxesTUVPJ-gmangKQHJgKo5hVl49GOeI3SMGMChrWtzvmIsm0dRFuKN=w300-rw";
    return new Post(String.valueOf(position), "Post " + position, position + " " + dummyDesc, position % 2 == 0 ? dummyImageUrl1 : dummyImageUrl2);
  }
}
