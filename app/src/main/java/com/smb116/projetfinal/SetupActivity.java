package com.smb116.projetfinal;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SetupActivity extends AppCompatActivity {
    public static final String TAG = "SetupActivity";

    private ProjetFinalViewModel projetFinalViewModel;

    private Spinner bufferSizeSpinner;
    private Spinner sampleRateSpinner;
    private Spinner maxSpectrumValueSpiner;
    private Spinner maxGraphValueSpinner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        projetFinalViewModel = ProjetFinalViewModelFactory.getInstance(ProjetFinalViewModel.class);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.setup_title));
        bindView();
        configureSpinners();
        configureSpinnersListeners();
    }

    private void bindView(){
        bufferSizeSpinner = findViewById(R.id.buffer_size_spinner);
        sampleRateSpinner = findViewById(R.id.samplerate_spinner);
        maxSpectrumValueSpiner = findViewById(R.id.max_spectrum_value_spinner);
        maxGraphValueSpinner = findViewById(R.id.max_graph_value_spinner);
    }

    private void configureSpinners(){
        ArrayAdapter<CharSequence> adapter;

        adapter = ArrayAdapter.createFromResource(this,
                R.array.setup_array_buffer_size, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bufferSizeSpinner.setAdapter(adapter);
        bufferSizeSpinner.setSelection(adapter.getPosition(projetFinalViewModel.getBufferSize().toString()));

        adapter = ArrayAdapter.createFromResource(this,
                R.array.setup_array_sample_rate, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sampleRateSpinner.setAdapter(adapter);
        sampleRateSpinner.setSelection(adapter.getPosition(projetFinalViewModel.getSampleRate().toString()));

        adapter = ArrayAdapter.createFromResource(this,
                R.array.setup_array_max_spectrum_value, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        maxSpectrumValueSpiner.setAdapter(adapter);
        maxSpectrumValueSpiner.setSelection(adapter.getPosition(String.valueOf(projetFinalViewModel.getMaxSpectrumValue())));

        adapter = ArrayAdapter.createFromResource(this,
                R.array.setup_array_max_graph_value, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        maxGraphValueSpinner.setAdapter(adapter);
        maxGraphValueSpinner.setSelection(adapter.getPosition(String.valueOf(projetFinalViewModel.getMaxGraphValue())));
    }

    private void configureSpinnersListeners(){
        bufferSizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                projetFinalViewModel.setBufferSize(Integer.parseInt((String)parent.getItemAtPosition(position)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sampleRateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                projetFinalViewModel.setSampleRate(Integer.parseInt((String)parent.getItemAtPosition(position)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        maxSpectrumValueSpiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                projetFinalViewModel.setMaxSpectrumValue(Float.parseFloat((String)parent.getItemAtPosition(position)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        maxGraphValueSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                projetFinalViewModel.setMaxGraphValue(Short.parseShort((String)parent.getItemAtPosition(position)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
