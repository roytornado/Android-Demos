package com.redso.audioprocessing;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class VideoMakerManager {
  public static String TAG = "VideoMakerManager";
  File inputAudioFile;
  File inputVideoFile;
  File outputFile;
  Handler handler;

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
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void convert() throws Exception {
    log("start convert");
    MediaMuxer muxer = new MediaMuxer(outputFile.getAbsolutePath(), MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);

    // Audio

    FileInputStream audioInputStream = new FileInputStream(inputAudioFile);
    MediaExtractor audioExtractor = new MediaExtractor();
    audioExtractor.setDataSource(inputAudioFile.getAbsolutePath());

    MediaFormat targetAudioFormat = null;
    int targetInputTrack = 0;
    int numTracks = audioExtractor.getTrackCount();
    for (int i = 0; i < numTracks; ++i) {
      MediaFormat format = audioExtractor.getTrackFormat(i);
      String mime = format.getString(MediaFormat.KEY_MIME);
      if (mime.contains("audio")) {
        log("audioExtractor track: " + mime);
        targetAudioFormat = format;
        targetInputTrack = i;
        audioExtractor.selectTrack(i);
      }
    }

    if (targetAudioFormat == null) {
      return;
    }

    MediaFormat audioCodecOutputFormat = MediaFormat.createAudioFormat("audio/mp4a-latm", 44100, 1);
    audioCodecOutputFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
    audioCodecOutputFormat.setInteger(MediaFormat.KEY_BIT_RATE, 128000);

    MediaCodec audioCodec = MediaCodec.createEncoderByType("audio/mp4a-latm");
    audioCodec.configure(audioCodecOutputFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);

    int targetOutputTrack = muxer.addTrack(audioCodecOutputFormat);
    muxer.start();
    audioCodec.start();

    encodeAudio(audioInputStream, audioCodec, muxer, targetOutputTrack);

    /*
    int frameCount = 0;
    MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();

    ByteBuffer[] audioCodecInputBuffers = audioCodec.getInputBuffers();
    ByteBuffer[] audioCodecOutputBuffers = audioCodec.getOutputBuffers();


    boolean hasMoreData = true;
    while (hasMoreData) {
      int currentAudioInputBufferId = audioCodec.dequeueInputBuffer(-1);
      ByteBuffer currentAudioInputBuffer = audioCodecInputBuffers[currentAudioInputBufferId];
      currentAudioInputBuffer.clear();
      audioExtractor.advance();
      log("readSampleData: " + currentAudioInputBufferId + ":" + currentAudioInputBuffer.limit());
      int bytesRead = audioExtractor.readSampleData(ByteBuffer.allocate(32000), 100);
      long presentationTimeUs = audioExtractor.getSampleTime();
      audioCodec.queueInputBuffer(currentAudioInputBufferId, 0, bytesRead, presentationTimeUs, 0);

      int currentAudioOutputBufferId = audioCodec.dequeueOutputBuffer(bufferInfo, -1);
      if (currentAudioOutputBufferId >= 0) {
        ByteBuffer encodedData = audioCodecOutputBuffers[currentAudioOutputBufferId];
        muxer.writeSampleData(targetOutputTrack, encodedData, bufferInfo);
        audioCodec.releaseOutputBuffer(currentAudioOutputBufferId, false);
      } else if (currentAudioOutputBufferId == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
        log("audioCodec: INFO_OUTPUT_BUFFERS_CHANGED");
        audioCodecOutputBuffers = audioCodec.getOutputBuffers();
      } else if (currentAudioOutputBufferId == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
        log("audioCodec: INFO_OUTPUT_FORMAT_CHANGED");
      }

      log("appended audio frame: " + frameCount);
      frameCount++;
      hasMoreData = audioExtractor.hasCacheReachedEndOfStream();
    }*/

    /*
    while (audioExtractor.readSampleData(inputBuffer, offset) >= 0) {
      bufferInfo.presentationTimeUs = audioExtractor.getSampleTime();
      muxer.writeSampleData(targetOutputTrack, inputBuffer, bufferInfo);
      log("appended audio frame: " + frameCount);
      audioExtractor.advance();
      frameCount++;
    }*/

    muxer.stop();
    muxer.release();
    log("finish convert");
  }

  private void encodeAudio(FileInputStream audioInputStream, MediaCodec audioCodec, MediaMuxer muxer, int targetOutputTrack) throws IOException {

    int frameCount = 0;
    MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
    ByteBuffer[] audioCodecInputBuffers = audioCodec.getInputBuffers();
    ByteBuffer[] audioCodecOutputBuffers = audioCodec.getOutputBuffers();

    boolean hasMoreData = true;
    while (hasMoreData) {
      int currentAudioInputBufferId = audioCodec.dequeueInputBuffer(-1);
      ByteBuffer currentAudioInputBuffer = audioCodecInputBuffers[currentAudioInputBufferId];
      currentAudioInputBuffer.clear();
      byte[] inputBuffer = new byte[currentAudioInputBuffer.limit()];
      int bytesRead = audioInputStream.read(inputBuffer, 0, inputBuffer.length);
      log("audioInputStream read: " + bytesRead);

      if (bytesRead > 0) {
        currentAudioInputBuffer.put(inputBuffer);
        long presentationTimeUs = System.nanoTime();
        audioCodec.queueInputBuffer(currentAudioInputBufferId, 0, bytesRead, presentationTimeUs, 0);

        int currentAudioOutputBufferId = audioCodec.dequeueOutputBuffer(bufferInfo, -1);
        if (currentAudioOutputBufferId >= 0) {
          ByteBuffer encodedData = audioCodecOutputBuffers[currentAudioOutputBufferId];
          muxer.writeSampleData(targetOutputTrack, encodedData, bufferInfo);
          audioCodec.releaseOutputBuffer(currentAudioOutputBufferId, false);
        } else if (currentAudioOutputBufferId == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
          log("audioCodec: INFO_OUTPUT_BUFFERS_CHANGED");
          audioCodecOutputBuffers = audioCodec.getOutputBuffers();
        } else if (currentAudioOutputBufferId == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
          log("audioCodec: INFO_OUTPUT_FORMAT_CHANGED " + audioCodec.getOutputFormat());
          targetOutputTrack = muxer.addTrack(audioCodec.getOutputFormat());
        }
        frameCount++;
        log("appended audio frame: " + frameCount);
      } else {
        hasMoreData = false;
      }
    }
  }


  private void log(String message) {
    Log.d(TAG, message);
  }
}
