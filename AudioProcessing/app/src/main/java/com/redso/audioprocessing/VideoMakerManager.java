package com.redso.audioprocessing;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.nio.ByteBuffer;

public class VideoMakerManager {
  public static final int EVENT_ENCODE_SUCCESS = 0;
  public static final int EVENT_ENCODE_FAILED = 1;

  public static String TAG = "VideoMakerManager";
  public static final boolean debugEnabled = true;

  private File inputAudioFile;
  private File inputVideoFile;
  private File outputFile;
  private Handler handler;

  public VideoMakerManager(File inputAudioFile, File inputVideoFile, File outputFile, Handler handler) {
    this.inputAudioFile = inputAudioFile;
    this.inputVideoFile = inputVideoFile;
    this.outputFile = outputFile;
    this.handler = handler;
  }

  public void start() {
    new Thread() {
      @Override
      public void run() {
        startInternal();
      }
    }.start();
  }

  private void startInternal() {
    try {
      convert();
      handler.sendEmptyMessage(EVENT_ENCODE_SUCCESS);
    } catch (Exception e) {
      logError("Can't convert", e);
      handler.sendEmptyMessage(EVENT_ENCODE_FAILED);
    }
    handler = null;
  }

  private void convert() throws Exception {
    logDebug("start convert");
    MediaMuxer muxer = new MediaMuxer(outputFile.getAbsolutePath(), MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);

    // Audio Setup
    MediaExtractor audioExtractor = new MediaExtractor();
    audioExtractor.setDataSource(inputAudioFile.getAbsolutePath());

    MediaFormat targetAudioFormat = null;
    for (int i = 0; i < audioExtractor.getTrackCount(); ++i) {
      MediaFormat format = audioExtractor.getTrackFormat(i);
      String mime = format.getString(MediaFormat.KEY_MIME);
      logDebug("audioExtractor track: " + mime);
      if (mime.contains("audio")) {
        targetAudioFormat = format;
        audioExtractor.selectTrack(i);
      }
    }

    // Video Setup
    MediaExtractor videoExtractor = new MediaExtractor();
    videoExtractor.setDataSource(inputVideoFile.getAbsolutePath());

    MediaFormat targetVideoFormat = null;
    for (int i = 0; i < videoExtractor.getTrackCount(); ++i) {
      MediaFormat format = videoExtractor.getTrackFormat(i);
      String mime = format.getString(MediaFormat.KEY_MIME);
      logDebug("videoExtractor track: " + mime);
      if (mime.contains("video")) {
        targetVideoFormat = format;
        videoExtractor.selectTrack(i);
      }
    }


    if (targetAudioFormat == null || targetVideoFormat == null) {
      new Exception("Expect one audio track and one video track");
      return;
    }

    int targetAudioOutputTrack = muxer.addTrack(targetAudioFormat);
    int targetVideoOutputTrack = muxer.addTrack(targetVideoFormat);
    muxer.start();


    ByteBuffer inputBuffer = ByteBuffer.allocate(256 * 1024);
    MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();

    int frameCount = 0;
    boolean audioMuxDone = false;
    long presentationTimeUs = -1;
    while (!audioMuxDone) {
      inputBuffer.clear();
      int bytesRead = audioExtractor.readSampleData(inputBuffer, 0);
      if (bytesRead < 0) {
        audioMuxDone = true;
      } else {
        if (presentationTimeUs == -1) presentationTimeUs = audioExtractor.getSampleTime();
        bufferInfo.presentationTimeUs = audioExtractor.getSampleTime() - presentationTimeUs;
        bufferInfo.flags = audioExtractor.getSampleFlags();
        bufferInfo.size = bytesRead;
        muxer.writeSampleData(targetAudioOutputTrack, inputBuffer, bufferInfo);
        logDebug("appended audio frame: " + targetAudioOutputTrack + ":" + frameCount + ":" + bufferInfo.presentationTimeUs);
        audioExtractor.advance();
        frameCount++;
      }
    }

    frameCount = 0;
    boolean videoMuxDone = false;
    presentationTimeUs = -1;
    while (!videoMuxDone) {
      inputBuffer.clear();
      int bytesRead = videoExtractor.readSampleData(inputBuffer, 0);
      if (bytesRead < 0) {
        videoMuxDone = true;
      } else {
        if (presentationTimeUs == -1) presentationTimeUs = videoExtractor.getSampleTime();
        bufferInfo.presentationTimeUs = videoExtractor.getSampleTime() - presentationTimeUs;
        bufferInfo.flags = videoExtractor.getSampleFlags();
        bufferInfo.size = bytesRead;
        muxer.writeSampleData(targetVideoOutputTrack, inputBuffer, bufferInfo);
        logDebug("appended video frame: " + targetVideoOutputTrack + ":" + frameCount + ":" + bufferInfo.presentationTimeUs);
        videoExtractor.advance();
        frameCount++;
      }
    }

    audioExtractor.release();
    videoExtractor.release();
    muxer.stop();
    muxer.release();
    logDebug("finish convert");
  }


  private void logDebug(String message) {
    if (debugEnabled) Log.d(TAG, message);
  }

  private void logError(String message, Throwable throwable) {
    Log.e(TAG, message, throwable);
  }
}
