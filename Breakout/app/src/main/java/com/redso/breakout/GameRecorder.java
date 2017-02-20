/*
 * Copyright 2013 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.redso.breakout;

import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Trace;
import android.util.Log;

import java.io.File;
import java.nio.ByteBuffer;


/**
 * Record video of a game in progress.
 * <p>
 * Not generally thread-safe.  The recorder should be set up by the Activity before the
 * Render thread is started, then updates should be made from the GLSurfaceView render thread.
 */
public class GameRecorder {
  private static final String TAG = BreakoutActivity.TAG;

  // parameters for the encoder
  private static final String MIME_TYPE = "video/avc";    // H.264 Advanced Video Coding
  private static final int FRAME_RATE = 30;               // 30fps
  private static final int IFRAME_INTERVAL = 10;          // 10 seconds between I-frames
  private static final int VIDEO_WIDTH = 1280;            // 720p
  private static final int VIDEO_HEIGHT = 720;
  private static final int BIT_RATE = 6000000;            // 6Mbps

  private static final Object sSyncObj = new Object();
  private static volatile GameRecorder sInstance;

  private File mOutputFile;
  private MediaCodec mEncoder;
  private InputSurface mInputSurface;
  private MediaCodec.BufferInfo mBufferInfo;
  private MediaMuxer mMuxer;
  private int mTrackIndex;
  private boolean mMuxerStarted;

  private int mViewportWidth, mViewportHeight;
  private int mViewportXoff, mViewportYoff;
  private final float mProjectionMatrix[] = new float[16];


  /**
   * singleton
   */
  private GameRecorder() {
  }

  /**
   * Retrieves the singleton, creating the instance if necessary.
   */
  public static GameRecorder getInstance() {
    if (sInstance == null) {
      synchronized (sSyncObj) {
        if (sInstance == null) {
          sInstance = new GameRecorder();
        }
      }
    }
    return sInstance;
  }

  /**
   * Creates a new encoder, and prepares the output file.
   * <p>
   * We can't set up the InputSurface yet, because we want the EGL contexts to share stuff,
   * and the primary context may not have been configured yet.
   */
  public void prepareEncoder(Context context) {
    MediaCodec encoder;
    MediaMuxer muxer;

    if (mEncoder != null || mInputSurface != null) {
      throw new RuntimeException("prepareEncoder called twice?");
    }

    mOutputFile = new File(context.getFilesDir(), "video.mp4");
    Log.d(TAG, "Video recording to file " + mOutputFile);
    mBufferInfo = new MediaCodec.BufferInfo();

    try {
      // Create and configure the MediaFormat.
      MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE,
          VIDEO_WIDTH, VIDEO_HEIGHT);
      format.setInteger(MediaFormat.KEY_COLOR_FORMAT,
          MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
      format.setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE);
      format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);
      format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);

      // Create a MediaCodec encoder, and configure it with our format.
      encoder = MediaCodec.createEncoderByType(MIME_TYPE);
      encoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);

      // Create a MediaMuxer.  We can't add the video track and start() the muxer here,
      // because our MediaFormat doesn't have the Magic Goodies.  These can only be
      // obtained from the encoder after it has started processing data.
      muxer = new MediaMuxer(mOutputFile.getAbsolutePath(),
          MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
      mMuxerStarted = false;

      mEncoder = encoder;
      mMuxer = muxer;
    } catch (Exception ex) {
      Log.w(TAG, "Something failed during recorder init: " + ex);
      releaseEncoder();
    }

    configureViewport();
  }

  /**
   * Finishes setup.  Call with the primary EGL context set.
   */
  public void firstTimeSetup() {
    if (!isRecording() || mInputSurface != null) {
      // not recording, or already initialized
      return;
    }

    InputSurface inputSurface;

    try {
      inputSurface = new InputSurface(mEncoder.createInputSurface());
      mEncoder.start();

      mInputSurface = inputSurface;
    } catch (Exception ex) {
      Log.w(TAG, "Something failed during recorder init: " + ex);
      releaseEncoder();
    }
  }

  /**
   * Releases encoder resources.  May be called after partial / failed initialization.
   */
  private void releaseEncoder() {
    Log.d(TAG, "releasing encoder objects");
    if (mEncoder != null) {
      mEncoder.stop();
      mEncoder.release();
      mEncoder = null;
    }
    if (mInputSurface != null) {
      mInputSurface.release();
      mInputSurface = null;
    }
    if (mMuxer != null) {
      mMuxer.stop();
      mMuxer.release();
      mMuxer = null;
    }
  }

  /**
   * Returns true if a recording is in progress.
   */
  public boolean isRecording() {
    return mEncoder != null;
  }

  /**
   * Configures our viewport projection matrix.
   * <p>
   * We always render at the surface's resolution, which matches the video encoder resolution.
   * The resolution and orientation of the device itself are irrelevant -- we're not recording
   * what's on screen, but rather what would be on screen if the device resolution matched our
   * video parameters.
   */
  private void configureViewport() {
    float arenaRatio = GameState.ARENA_HEIGHT / GameState.ARENA_WIDTH;
    int x, y, viewWidth, viewHeight;

    if (VIDEO_HEIGHT > (int) (VIDEO_WIDTH * arenaRatio)) {
      // limited by narrow width; restrict height
      viewWidth = VIDEO_WIDTH;
      viewHeight = (int) (VIDEO_WIDTH * arenaRatio);
    } else {
      // limited by short height; restrict width
      viewHeight = VIDEO_HEIGHT;
      viewWidth = (int) (VIDEO_HEIGHT / arenaRatio);
    }
    x = (VIDEO_WIDTH - viewWidth) / 2;
    y = (VIDEO_HEIGHT - viewHeight) / 2;

    Log.d(TAG, "configureViewport w=" + VIDEO_WIDTH + " h=" + VIDEO_HEIGHT);
    Log.d(TAG, " --> x=" + x + " y=" + y + " gw=" + viewWidth + " gh=" + viewHeight);

    mViewportXoff = x;
    mViewportYoff = y;
    mViewportWidth = viewWidth;
    mViewportHeight = viewHeight;

    Matrix.orthoM(mProjectionMatrix, 0, 0, GameState.ARENA_WIDTH,
        0, GameState.ARENA_HEIGHT, -1, 1);
  }

  /**
   * Returns the projection matrix.
   *
   * @param dest a float[16]
   */
  public void getProjectionMatrix(float[] dest) {
    System.arraycopy(dest, 0, mProjectionMatrix, 0, mProjectionMatrix.length);
  }

  /**
   * Sets the viewport for video.
   */
  public void setViewport() {
    GLES20.glViewport(mViewportXoff, mViewportYoff, mViewportWidth, mViewportHeight);
  }

  /**
   * Configures EGL to output to our InputSurface.
   */
  public void makeCurrent() {
    mInputSurface.makeCurrent();
  }

  /**
   * Sends the current frame to the recorder.  Before doing so, we drain any pending output.
   */
  public void swapBuffers() {
    if (!isRecording()) {
      return;
    }
    drainEncoder(false);
    mInputSurface.setPresentationTime(System.nanoTime());
    mInputSurface.swapBuffers();
  }

  /**
   * Extracts all pending data from the encoder.
   * <p>
   * If endOfStream is not set, this returns when there is no more data to drain.  If it
   * is set, we send EOS to the encoder, and then iterate until we see EOS on the output.
   */
  private void drainEncoder(boolean endOfStream) {
    if (!isRecording()) {
      return;
    }

    Trace.beginSection("drainEncoder");

//        if (endOfStream) {
//            if (VERBOSE) Log.d(TAG, "sending EOS to encoder");
//            mEncoder.signalEndOfInputStream();
//        }

    //Log.d(TAG, "GameRecorder drainEncoder " + endOfStream);
    ByteBuffer[] encoderOutputBuffers = mEncoder.getOutputBuffers();

    while (true) {
      int encoderStatus = mEncoder.dequeueOutputBuffer(mBufferInfo, 0);
      if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
        // no output available yet
        break;      // out of while
      } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
        // not expected for an encoder
        encoderOutputBuffers = mEncoder.getOutputBuffers();
      } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
        // should happen before receiving buffers, and should only happen once
        if (mMuxerStarted) {
          throw new RuntimeException("format changed twice");
        }
        MediaFormat newFormat = mEncoder.getOutputFormat();
        Log.d(TAG, "encoder output format changed: " + newFormat);
        mTrackIndex = mMuxer.addTrack(newFormat);
        mMuxer.start();
        mMuxerStarted = true;
      } else {
        ByteBuffer encodedData = encoderOutputBuffers[encoderStatus];
        if (encodedData == null) {
          throw new RuntimeException("encoderOutputBuffer " + encoderStatus + " was null");
        }

        if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
          // the codec config data was pulled out and fed to the muxer when we got
          // the INFO_OUTPUT_FORMAT_CHANGED status.  Ignore it.
          Log.d(TAG, "ignoring BUFFER_FLAG_CODEC_CONFIG");
          mBufferInfo.size = 0;
        }

        if (mBufferInfo.size != 0) {
          if (!mMuxerStarted) {
            throw new RuntimeException("muxer hasn't started");
          }

          // Adjust the ByteBuffer values to match BufferInfo.  (not needed?)
          encodedData.position(mBufferInfo.offset);
          encodedData.limit(mBufferInfo.offset + mBufferInfo.size);

          mMuxer.writeSampleData(mTrackIndex, encodedData, mBufferInfo);
          Log.d(TAG, "wrote " + mBufferInfo.size + " bytes");
        }

        mEncoder.releaseOutputBuffer(encoderStatus, false);
        if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
          if (!endOfStream) {
            Log.w(TAG, "reached end of stream unexpectedly");
          } else {
            Log.d(TAG, "end of stream reached");
          }
          break;      // out of while
        }
      }
    }

    Trace.endSection();
  }

  /**
   * Handles activity pauses (could be leaving the game, could be switching back to the
   * main activity).  Stop recording, shut the codec down.
   */
  public void gamePaused() {
    Log.d(TAG, "GameRecorder gamePaused");

    drainEncoder(true);
    releaseEncoder();
  }
}
