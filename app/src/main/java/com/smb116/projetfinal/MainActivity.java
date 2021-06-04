package com.smb116.projetfinal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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

    private WeakReference<RecordMicrophoneThread> weakRecordMicrophoneThread;
    private LineChart chart ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkForPermission();
        this.chart = findViewById(R.id.chart);
    }

    public void changeView(View view){
        weakRecordMicrophoneThread.get().changeViewFlag();
    }

    @Override
    protected void onResume() {
        super.onResume();
        weakRecordMicrophoneThread = new WeakReference<>(new RecordMicrophoneThread(this));
        weakRecordMicrophoneThread.get().start();
    }

    @Override
    protected void onPause() {
        weakRecordMicrophoneThread.get().interrupt();
        super.onPause();
    }

    public void checkForPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_CODE);
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

        for (int i =0; i < dData.length; i++) {
            entries.add(new Entry(i, (float)Math.abs(dData[i]*4)));
        }
        LineDataSet dataSet = new LineDataSet(entries, getResources().getString(R.string.spectrum));
        buildChart(dataSet);
    }

    public void drawGraph(short[] sData){
        final short[] dataObjects =  sData;
        List<Entry> entries = new ArrayList<Entry>();
        for (int i =0; i< dataObjects.length; i++) {
            entries.add(new Entry(i, dataObjects[i]));
        }
        LineDataSet dataSet = new LineDataSet(entries, getResources().getString(R.string.waveform));
        buildChart(dataSet);
    }

    public void buildChart(LineDataSet dataSet){
        dataSet.setColor(getResources().getColor(R.color.black));
        dataSet.setValueTextColor(getResources().getColor(R.color.purple_200)); // styling, ...
        dataSet.setDrawCircleHole(false);
        dataSet.setDrawCircles(false);
        LineData lineData = new LineData(dataSet);
        chart.clear();
        chart.setData(lineData);
        chart.invalidate(); // refresh
        weakRecordMicrophoneThread.get().goThough();
    }

}