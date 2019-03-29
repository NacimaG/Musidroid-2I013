package com.example.a3671129.musidroid;

import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import l2i013.musidroid.model.InstrumentPart;


public class TempoChangeListener implements SeekBar.OnSeekBarChangeListener {

    private TheApplication app;
    private TextView tempoText;
    private int tempo;

    public TempoChangeListener(TheApplication app, TextView ct) {
        super();
        this.app = app;
        this.tempoText = ct;
        InstrumentPart ip = app.getInstrument();

        tempo =   app.getPartition().getTempo();


    }

    public void onProgressChanged(SeekBar sb, int n, boolean b) {
        if (app != null) {
            tempo = n;
            tempoText.setText("Partition\t Tempo (" + tempo + ")");
        }
        else
        {
            Log.wtf("TempoChangeListener Methode onProgressChanged", "app NULL !");
        }
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
        tempoText.setText("Partition \t Tempo (" + tempo + ")");
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        app.getPartition().setTempo(tempo);
    }

}