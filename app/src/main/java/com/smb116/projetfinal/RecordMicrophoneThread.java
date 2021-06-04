package com.smb116.projetfinal;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;

import ca.uol.aig.fftpack.RealDoubleFFT;

public class RecordMicrophoneThread extends Thread {
    private static final String TAG = "RecordMicrophoneThread";
    private static final int MINIMUM_SIZE = 1024;

    private int bufferSize;
    private int sampleRate;

    private final MainActivity mainActivity;
    private AudioRecord recorder;
    private Boolean isRecording = false;
    private RealDoubleFFT transformer;
    private Boolean spectrumView;
    private Boolean goThrough = true;

    public RecordMicrophoneThread(MainActivity mainActivity){
        this.mainActivity = mainActivity;
        bufferSize = mainActivity.projetFinalViewModel.getBufferSize();
        sampleRate = mainActivity.projetFinalViewModel.getSampleRate();
        spectrumView = mainActivity.projetFinalViewModel.getSpectrumView();

        transformer = new RealDoubleFFT(bufferSize);
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                sampleRate,
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

                short[] sData = new short[bufferSize];
                int bufferReadResult = recorder.read(sData, 0, bufferSize);

                if (spectrumView){
                    double[] dData = new double[bufferSize];
                    for (int i = 0; i < bufferSize && i < bufferReadResult; i++) {
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
        mainActivity.projetFinalViewModel.setSpectrumView(spectrumView);
    }

    public Boolean getIsRecording(){
        return isRecording;
    }

    public void stopRecorder(){
        interrupt();
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
