package com.smb116.projetfinal;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import ca.uol.aig.fftpack.RealDoubleFFT;

public class RecordMicrophoneThread extends Thread {
    private static final String TAG = "RecordMicrophoneThread";
    private static final int BUFFER_SIZE = 1024;
    private static final int MINIMUM_SIZE = 1024;
    private static final int SAMPLE_RATE = 8000;

    private final MainActivity mainActivity;
    private AudioRecord recorder;
    private Boolean isRecording = false;
    private RealDoubleFFT transformer;
    private Boolean spectrumView = false;
    private Boolean goThrough = true;

    public RecordMicrophoneThread(MainActivity mainActivity){
        this.mainActivity = mainActivity;
        transformer = new RealDoubleFFT(BUFFER_SIZE);
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                MINIMUM_SIZE);
    }

    @Override
    public void run() {
        super.run();
        recorder.startRecording();

        while (isRecording){
            if (goThrough){
                goThrough = false;

                short[] sData = new short[BUFFER_SIZE];
                int bufferReadResult = recorder.read(sData, 0, BUFFER_SIZE);

                if (spectrumView){
                    double[] dData = new double[BUFFER_SIZE];
                    for (int i = 0; i < BUFFER_SIZE && i < bufferReadResult; i++) {
                        dData[i] = (double) sData[i] / 32768.0;
                    }
                    transformer.ft(dData);
                    showSpectrumOnMainUI(dData);
                }else {
                    showWaveFormOnMainUI(sData);
                }
            }
        }

    }

    @Override
    public synchronized void start() {
        isRecording = true;
        super.start();
    }

    @Override
    public void interrupt() {
        isRecording = false;
        recorder.stop();
        super.interrupt();
    }

    public void changeViewFlag(){
        spectrumView = !spectrumView;
        Log.i(TAG, String.valueOf(spectrumView));
    }

    public void goThough(){
        goThrough = true;
    }

    public void showSpectrumOnMainUI(double[] dData){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mainActivity.drawSpectrum(dData);
            }
        });
    }

    public void showWaveFormOnMainUI(short[] sData){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mainActivity.drawGraph(sData);
            }
        });
    }
}
