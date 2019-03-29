package com.example.a3671129.musidroid;
import android.app.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.Touch;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import l2i013.musidroid.model.InstrumentPart;
import l2i013.musidroid.model.Partition;
import l2i013.musidroid.util.InstrumentName;
import l2i013.musidroid.util.MidiFile2I013;

public class TouchActivity extends Activity implements AdapterView.OnItemSelectedListener {

    TheApplication app;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touch);
        app = (TheApplication) (this.getApplication());


        SeekBar instantDebutSeekbar = (SeekBar) findViewById(R.id.changeInstantDebut);

        instantDebutSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int nouveauIB;

            public void onProgressChanged(SeekBar sb, int n, boolean b) {
                nouveauIB = n;
                app.setInstantDebut(nouveauIB);
                 ((TouchBoard) findViewById(R.id.boardSurface)).reDraw();

                Log.wtf("TOUCH ACTIVITYY MOI APP", nouveauIB + " + " + app.getInstantDebut());

            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                 app.setInstantDebut(nouveauIB);
                ((TouchBoard) findViewById(R.id.boardSurface)).reDraw();

                Log.wtf("TOUCH ACTIVITYY MOI APP", nouveauIB + " + " + app.getInstantDebut());

            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                app.setInstantDebut(nouveauIB);
                ((TouchBoard) findViewById(R.id.boardSurface)).reDraw();

                Log.wtf("TOUCH ACTIVITYY MOI APP", nouveauIB + " + " + app.getInstantDebut());

            }
        });
        instantDebutSeekbar.setMax(app.getInstants());
        instantDebutSeekbar.setProgress(app.getInstantDebut());


        ////////////////////////////
        Spinner spinnerinstrument = (Spinner) findViewById(R.id.instrumentChoice);
        spinnerinstrument.setOnItemSelectedListener(this);

        ArrayList<InstrumentPart> listIP = app.getPartition().getParts();

        ArrayList<InstrumentName> list = new ArrayList<>();

        for (InstrumentPart tmp : listIP) {
            list.add(tmp.getInstrument());
        }

        // ArrayList<InstrumentPart> list = app.getPartition().getParts();
        ArrayAdapter<InstrumentName> dataAdapter = new ArrayAdapter<InstrumentName>(this, R.layout.spinner_item, list);
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerinstrument.setAdapter(dataAdapter);
        spinnerinstrument.setSelection(app.getPartition().getParts().indexOf(app.getInstrument()));

        }



    public void onClickParam(View v) {
        Intent playIntent = new Intent(this, ParamActivity.class);
        startActivity(playIntent);
    }

    public void onClickReset(View v) {
        InstrumentPart ip = app.getInstrument();
        ip.getNotes().clear();
        ((TouchBoard) findViewById(R.id.boardSurface)).reDraw();

    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        InstrumentPart ip = app.getInstrument();

        ip.setInstrument(InstrumentName.values()[position]);
        ((TouchBoard) findViewById(R.id.boardSurface)).reDraw();


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {


    }

    public void onClickPlayTouch(View v) {
        Partition pTmp = new Partition(app.getPartition().getTempo());
        pTmp.addPart(app.getInstrument().getInstrument(),app.getInstrument().getOctave());
        pTmp.getParts().set(0,app.getInstrument());
        app.playPart(pTmp);
    }

    public void onResume() {
        super.onResume();
        SeekBar instantDebutSeekbar = (SeekBar) findViewById(R.id.changeInstantDebut);
        instantDebutSeekbar.setMax(app.getInstants());
        Log.wtf("NOUVEAU", "NOUVEAU " + app.getInstants());
        TextView PartName = (TextView)findViewById(R.id.NomPartition);
        if(app.getNamePartition()==null){
            PartName.setText("My partition");
        }
        else{
            PartName.setText(app.getNamePartition());
        }

    }


    public void onClickSave( View view) {
        app.save(TouchActivity.this);
    }
}
