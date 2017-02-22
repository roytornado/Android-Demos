package com.redso.audioprocessing;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.effects.DelayEffect;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.io.android.AndroidAudioPlayer;
import be.tarsos.dsp.io.android.AndroidFFMPEGLocator;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.resample.RateTransposer;
import be.tarsos.dsp.writer.WriterProcessor;

public class MainActivity extends AppCompatActivity {

  public static String TAG = "AudioProcessingDemo";

  MicRecorder micRecorder;
  MediaPlayer mediaPlayer;
  AudioDispatcher dispatcher;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    //pitchTextView = (TextView) findViewById(R.id.pitchTextView);

    RawFileManager.instance.convertRawFiles(getApplicationContext());
    /*
    AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0);
    dispatcher.addAudioProcessor(new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, new PitchDetectionHandler() {
      @Override
      public void handlePitch(PitchDetectionResult pitchDetectionResult,
                              AudioEvent audioEvent) {
        final float pitchInHz = pitchDetectionResult.getPitch();
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            pitchTextView.setText("" + pitchInHz);
          }
        });

      }
    }));*/

    new AndroidFFMPEGLocator(this);

    /*
    AudioDispatcher dispatcher = AudioDispatcherFactory.fromPipe(RawFileManager.demo.getAbsolutePath(), 44100, 4096, 0);
    TarsosDSPAudioFormat format = dispatcher.getFormat();
    dispatcher.addAudioProcessor(new NoiseGenerator(0.2));
    dispatcher.addAudioProcessor(new RateTransposer(centToFactor(-800)));
    dispatcher.addAudioProcessor(new AndroidAudioPlayer(format));
    new Thread(dispatcher, "Audio Dispatcher").start();
    */
  }

  public void startMicRecording(View v) {
    micRecorder = new MicRecorder(RawFileManager.originalAudioFile);
    boolean success = micRecorder.start();
    if (!success) {
      Toast.makeText(getBaseContext(), "Unable to start mic recording", Toast.LENGTH_LONG).show();
    }
  }

  public void stopMicRecording(View v) {
    if (micRecorder != null) {
      micRecorder.stop();
      Toast.makeText(getBaseContext(), "Mic recording duration: " + micRecorder.getDurationInMilliseconds(), Toast.LENGTH_LONG).show();
      micRecorder = null;
    } else {
      Toast.makeText(getBaseContext(), "Mic recording not start yet", Toast.LENGTH_LONG).show();
    }
  }

  public void startMicRecordingPlayback(View v) {
    if (!RawFileManager.originalAudioFile.exists()) {
      Toast.makeText(getBaseContext(), "originalAudioFile not found", Toast.LENGTH_LONG).show();
      return;
    }
    mediaPlayer = new MediaPlayer();
    try {
      mediaPlayer.setDataSource(RawFileManager.originalAudioFile.getAbsolutePath());
      mediaPlayer.prepare();
      mediaPlayer.start();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void stopMicRecordingPlayback(View v) {
    if (mediaPlayer != null) {
      mediaPlayer.release();
      mediaPlayer = null;
    }
  }

  public void startMicRecordingPlaybackWithEffect(View v) {
    dispatcher = AudioDispatcherFactory.fromPipe(RawFileManager.originalAudioFile.getAbsolutePath(), 44100, 4096, 0);
    TarsosDSPAudioFormat format = dispatcher.getFormat();
    dispatcher.addAudioProcessor(new DelayEffect(0.2, 0.5, 44100));
    dispatcher.addAudioProcessor(new RateTransposer(centToFactor(800)));
    dispatcher.addAudioProcessor(new AndroidAudioPlayer(format));
    new Thread(dispatcher, "Audio Dispatcher").start();
  }

  public void stopMicRecordingPlaybackWithEffect(View v) {
    if (dispatcher != null) {
      dispatcher.stop();
      dispatcher = null;
    }
  }

  public void exportMicRecordingWithEffect(View v) {
    dispatcher = AudioDispatcherFactory.fromPipe(RawFileManager.originalAudioFile.getAbsolutePath(), 44100, 4096, 0);
    WriterProcessor writerProcessor = null;
    try {
      File file = RawFileManager.processedAudioFile;
      RandomAccessFile raf = new RandomAccessFile(file, "rw");
      writerProcessor = new WriterProcessor(dispatcher.getFormat(), raf);
      Log.d(TAG, "exportMicRecordingWithEffect " + file.getAbsolutePath());
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      return;
    }
    dispatcher.addAudioProcessor(new DelayEffect(0.2, 0.5, 44100));
    dispatcher.addAudioProcessor(new RateTransposer(centToFactor(800)));
    dispatcher.addAudioProcessor(writerProcessor);
    new Thread(dispatcher, "Audio Dispatcher").start();
  }

  public void playExportedMicRecordingWithEffect(View v) {
    if (!RawFileManager.processedAudioFile.exists()) {
      Toast.makeText(getBaseContext(), "processedAudioFile not found", Toast.LENGTH_LONG).show();
      return;
    }
    mediaPlayer = new MediaPlayer();
    try {
      mediaPlayer.setDataSource(RawFileManager.processedAudioFile.getAbsolutePath());
      mediaPlayer.prepare();
      mediaPlayer.start();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void exportMicRecordingM4A(View v) {
    PCMEncoder pcmEncoder = new PCMEncoder(128000, 44100, 1, RawFileManager.processedAudioFile.getAbsolutePath(), RawFileManager.outputAudioFile.getAbsolutePath(), audioEncodeHandler);
    pcmEncoder.start();
  }

  private Handler audioEncodeHandler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      super.handleMessage(msg);
      if (msg.what == PCMEncoder.EVENT_ENCODE_SUCCESS) {
        Toast.makeText(getBaseContext(), "encode success " + RawFileManager.outputAudioFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
      }
      if (msg.what == PCMEncoder.EVENT_ENCODE_FAILED) {
        Toast.makeText(getBaseContext(), "encode failed " + RawFileManager.outputAudioFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
      }
    }
  };

  public void playExportedMicRecordingM4A(View v) {
    if (!RawFileManager.outputAudioFile.exists()) {
      Toast.makeText(getBaseContext(), "outputAudioFile not found", Toast.LENGTH_LONG).show();
      return;
    }
    mediaPlayer = new MediaPlayer();
    try {
      mediaPlayer.setDataSource(RawFileManager.outputAudioFile.getAbsolutePath());
      mediaPlayer.prepare();
      mediaPlayer.start();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void mergeWithVideo(View v) {
    //VideoMakerManager videoMakerManager = new VideoMakerManager(RawFileManager.processedAudioFile, RawFileManager.inputVideo, RawFileManager.outputVideoFile, videoMakerHandler);
    //videoMakerManager.start();
  }

  public void playMerged(View v) {
  }

  private Handler videoMakerHandler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      super.handleMessage(msg);
    }
  };

  public static double centToFactor(double cents) {
    return 1 / Math.pow(Math.E, cents * Math.log(2) / 1200 / Math.log(Math.E));
  }

  public static double factorToCents(double factor) {
    return 1200 * Math.log(1 / factor) / Math.log(2);
  }
}
