package com.smb116.projetfinal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE = 1234;

    private float maxSpectrumValue;
    private short maxGraphValue;
    private WeakReference<RecordMicrophoneThread> weakRecordMicrophoneThread;
    public ProjetFinalViewModel projetFinalViewModel;
    private LineChart chart ;
    private Boolean followPeaks;
    private Boolean granted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkForPermission();
        this.chart = findViewById(R.id.chart);
        projetFinalViewModel = new ProjetFinalViewModelFactory(this).create(ProjetFinalViewModel.class);
        projetFinalViewModel.initialize(this);
        maxSpectrumValue = projetFinalViewModel.getMaxSpectrumValue();
        maxGraphValue = projetFinalViewModel.getMaxGraphValue();
    }

    @Override
    protected void onResume() {
        super.onResume();
       if (granted){
           ifIsGranted();
       }
    }

    @Override
    protected void onPause() {
        if (granted){
            projetFinalViewModel.setFollowPeaks(followPeaks);
            weakRecordMicrophoneThread.get().interrupt();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        projetFinalViewModel.saveSerializedObject();
        super.onDestroy();
    }

    private void ifIsGranted(){
        followPeaks = projetFinalViewModel.getFollowPeaks();
        weakRecordMicrophoneThread = new WeakReference<>(new RecordMicrophoneThread(this));
        weakRecordMicrophoneThread.get().start();
    }

    public void checkForPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_CODE);
        }else{
            granted = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("TAG", "permission OK by user");
                    granted = true;
                } else {
                    Log.d("TAG", "permission DENIED by user");
                    finish();
                }
                return;
            }
        }
    }

    public void drawSpectrum(double[] dData){
        List<Entry> entries = new ArrayList<Entry>();
        int i = 0;

        if (followPeaks){
            for (i =0; i< dData.length; i++) {
                entries.add(new Entry(i, (float)Math.abs(dData[i])));
            }
        }else{
            for (i =0; i< dData.length; i++) {
                entries.add(new Entry(i, (float)Math.abs(dData[i])));
            }
            entries.add(new Entry(i+1, maxSpectrumValue));
        }

        LineDataSet dataSet = new LineDataSet(entries, getResources().getString(R.string.spectrum));
        buildChart(dataSet);
    }

    public void drawGraph(short[] sData){
        List<Entry> entries = new ArrayList<Entry>();
        int i = 0;

        if (followPeaks){
            for (i =0; i< sData.length; i++) {
                entries.add(new Entry(i, sData[i]));
            }
        }else{
            for (i =0; i< sData.length; i++) {
                entries.add(new Entry(i, sData[i]));
            }
            entries.add(new Entry(i+1, maxGraphValue));
            entries.add(new Entry(i+2, -maxGraphValue));
        }

        LineDataSet dataSet = new LineDataSet(entries, getResources().getString(R.string.waveform));
        buildChart(dataSet);
    }

    public void buildChart(LineDataSet dataSet){
        dataSet.setColor(getResources().getColor(R.color.black));
        dataSet.setValueTextColor(getResources().getColor(R.color.purple_200));
        dataSet.setDrawCircleHole(false);
        dataSet.setDrawCircles(false);
        LineData lineData = new LineData(dataSet);
        chart.clear();
        chart.setData(lineData);
        chart.invalidate();
        weakRecordMicrophoneThread.get().goThough();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_change_view:
                weakRecordMicrophoneThread.get().changeViewFlag();
                return true;
            case R.id.menu_follow_peaks:
                followPeaks = !followPeaks;
                return true;
            case R.id.menu_play_pause:
                if (weakRecordMicrophoneThread.get().getIsRecording()){
                    weakRecordMicrophoneThread.get().stopRecorder();
                }else{
                    weakRecordMicrophoneThread = new WeakReference<>(new RecordMicrophoneThread(this));
                    weakRecordMicrophoneThread.get().start();
                }
                return true;
            case R.id.menu_setup:
                startActivity(new Intent(this, SetupActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}