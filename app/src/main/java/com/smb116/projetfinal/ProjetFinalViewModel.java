package com.smb116.projetfinal;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ProjetFinalViewModel extends ViewModel {

    private Boolean firstOnCreate = true;
    private SerializedObject serializedObject = new SerializedObject();
    private Context context;

    private Integer bufferSize = 1024;
    private Integer sampleRate = 8000;
    private Boolean spectrumView = false;
    private Boolean followPeaks = true;
    private float maxSpectrumValue = 40;
    private short maxGraphValue = 5000;

    public Integer getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(Integer bufferSize) {
        this.bufferSize = bufferSize;
    }

    public Integer getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(Integer sampleRate) {
        this.sampleRate = sampleRate;
    }

    public Boolean getSpectrumView() {
        return spectrumView;
    }

    public void setSpectrumView(Boolean spectrumView) {
        this.spectrumView = spectrumView;
    }

    public Boolean getFollowPeaks() {
        return followPeaks;
    }

    public void setFollowPeaks(Boolean followPeaks) {
        this.followPeaks = followPeaks;
    }

    public float getMaxSpectrumValue() {
        return maxSpectrumValue;
    }

    public void setMaxSpectrumValue(float maxSpectrumValue) {
        this.maxSpectrumValue = maxSpectrumValue;
    }

    public short getMaxGraphValue() {
        return maxGraphValue;
    }

    public void setMaxGraphValue(short maxGraphValue) {
        this.maxGraphValue = maxGraphValue;
    }

    public void initialize(Context context){
        this.context = context;
        if (firstOnCreate){
            try {
                load();
                this.firstOnCreate = false;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveSerializedObject(){
        serializedObject.bufferSize = bufferSize;
        serializedObject.sampleRate = sampleRate;
        serializedObject.spectrumView = spectrumView;
        serializedObject.followPeaks = followPeaks;
        serializedObject.maxSpectrumValue = maxSpectrumValue;
        serializedObject.maxGraphValue = maxGraphValue;
        save();
    }

    private void save(){
        try {
            FileOutputStream fos = context.openFileOutput("SerializedObject", Context.MODE_PRIVATE);
            ObjectOutputStream os = null;
            os = new ObjectOutputStream(fos);
            os.writeObject(serializedObject);
            os.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void load() throws IOException, ClassNotFoundException {
        FileInputStream fis = context.openFileInput("SerializedObject");
        ObjectInputStream is = new ObjectInputStream(fis);
        SerializedObject deSerializedObject = (SerializedObject) is.readObject();

        if (deSerializedObject != null){
            bufferSize = deSerializedObject.bufferSize;
            sampleRate = deSerializedObject.sampleRate;
            spectrumView = deSerializedObject.spectrumView;
            followPeaks = deSerializedObject.followPeaks;
            maxSpectrumValue = deSerializedObject.maxSpectrumValue;
            maxGraphValue = deSerializedObject.maxGraphValue;
        }

        is.close();
        fis.close();
    }

    public class SerializedObject implements Serializable{
        public int bufferSize;
        public int sampleRate;
        public Boolean spectrumView;
        public Boolean followPeaks;
        public float maxSpectrumValue;
        public short maxGraphValue;
    }
}
