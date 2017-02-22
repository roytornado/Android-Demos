package com.redso.audioprocessing;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class RawFileManager {
  public static String TAG = "RawFileManager";
  public static RawFileManager instance = new RawFileManager();
  public static File demo = null;
  public static File inputVideo = null;
  public static File originalAudioFile = null;
  public static File processedAudioFile = null;
  public static File outputVideoFile = null;
  public static File outputAudioFile = null;

  public void convertRawFiles(Context context) {
    File dir = new File(context.getFilesDir(), "raw");
    if (!dir.exists()) {
      Log.d(TAG, "create dir: " + dir.getAbsolutePath());
      dir.mkdirs();
    }

    demo = convertRawFile(context, R.raw.demo, "demo.mp3", dir);
    inputVideo = convertRawFile(context, R.raw.test_video, "test_video.mp4", dir);
    originalAudioFile = new File(context.getExternalCacheDir(), "originalAudio.m4a");
    processedAudioFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "processedAudio.wav");
    outputVideoFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "outputVideo.m4a");
    outputAudioFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "outputAudio.m4a");
  }

  public File convertRawFile(Context context, int fileId, String name, File folder) {
    InputStream inputStream = context.getResources().openRawResource(fileId);
    File file = createFileFromInputStream(inputStream, name, folder);
    Log.d(TAG, "createFileFromInputStream: " + file.getAbsolutePath());
    return file;
  }

  private File createFileFromInputStream(InputStream inputStream, String name, File folder) {

    try {
      File f = new File(folder, name);
      if (f.exists()) {
        return f;
      }
      OutputStream outputStream = new FileOutputStream(f);
      byte buffer[] = new byte[1024];
      int length = 0;

      while ((length = inputStream.read(buffer)) > 0) {
        outputStream.write(buffer, 0, length);
      }

      outputStream.close();
      inputStream.close();

      return f;
    } catch (IOException e) {
      //Logging exception
      e.printStackTrace();
    }

    return null;
  }
}
