package com.example.a3671129.musidroid;

import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import l2i013.musidroid.model.InstrumentPart;


public class OctaveChangeListener implements SeekBar.OnSeekBarChangeListener {

    private TheApplication app;
    private TextView octaveText;
    private int octave;

    public OctaveChangeListener(TheApplication app, TextView ct) {
        super();
        this.app = app;
        this.octaveText = ct;
        InstrumentPart ip = app.getInstrument();

        octave =   ip.getOctave();

    }

    public void onProgressChanged(SeekBar sb, int n, boolean b) {
        InstrumentPart ip = app.getInstrument();

        if (app != null) {
            octave = n;
            octaveText.setText(ip.getInstrument() + " \tOctave ( "+ octave +" )");
        }
        else
        {
            Log.wtf("OctaveChangeListener Methode onProgressChanged", "app NULL !");
        }
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
        InstrumentPart ip = app.getInstrument();

        octaveText.setText(ip.getInstrument() + " \tOctave ( "+ octave +" )");
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        InstrumentPart ip = app.getInstrument();
        ip.setOctave(octave);
    }

}