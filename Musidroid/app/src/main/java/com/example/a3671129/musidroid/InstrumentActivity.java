package com.example.a3671129.musidroid;
import android.app.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import l2i013.musidroid.model.InstrumentPart;
import l2i013.musidroid.model.Partition;
import l2i013.musidroid.util.InstrumentName;
import l2i013.musidroid.util.MidiFile2I013;

public class InstrumentActivity extends Activity implements AdapterView.OnItemSelectedListener {

    TheApplication  app ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instrument);
        app = (TheApplication)(this.getApplication());

        TextView PartName = (TextView)findViewById(R.id.NomPartition);

        if(app.getNamePartition()==null){
            PartName.setText("My Partition");
        }else {
            PartName.setText(app.getNamePartition());
        }

        SeekBar instantDebutSeekbar=(SeekBar)findViewById(R.id.changeInstantDebut);

        instantDebutSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int nouveauIB;

            public void onProgressChanged(SeekBar sb, int n, boolean b) {
                nouveauIB=n;
                app.setInstantDebut(nouveauIB);
                ((InstrumentBoard)findViewById(R.id.boardSurface)).reDraw();
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                 app.setInstantDebut(nouveauIB);
                ((InstrumentBoard)findViewById(R.id.boardSurface)).reDraw();
            }

            public void onStopTrackingTouch(SeekBar seekBar) {

                app.setInstantDebut(nouveauIB);
                ((InstrumentBoard)findViewById(R.id.boardSurface)).reDraw();
            }
        });
        instantDebutSeekbar.setMax(app.getInstants());
        instantDebutSeekbar.setProgress(app.getInstantDebut());


        Spinner spinnerinstrument = (Spinner) findViewById(R.id.instrumentChoice);
        spinnerinstrument.setOnItemSelectedListener(this);
        ArrayList<InstrumentPart> listIP = app.getPartition().getParts();

        ArrayList<InstrumentName> list = new ArrayList<>();

        for (InstrumentPart tmp : listIP) {
            list.add(tmp.getInstrument());
        }

        ArrayAdapter<InstrumentName> dataAdapter = new ArrayAdapter<InstrumentName>(this,R.layout.spinner_item, list);
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerinstrument.setAdapter(dataAdapter);
        spinnerinstrument.setSelection(app.getPartition().getParts().indexOf(app.getInstrument()));


    }

    public void onResume() {
        super.onResume();
        SeekBar instantDebutSeekbar=(SeekBar)findViewById(R.id.changeInstantDebut);
        instantDebutSeekbar.setMax(app.getInstants());
        TextView PartName = (TextView)findViewById(R.id.NomPartition);

        PartName.setText(app.getNamePartition());

    }

    public void onClickReset(View v) {
        InstrumentPart ip = app.getInstrument();

        ip.getNotes().clear();
        ((InstrumentBoard)findViewById(R.id.boardSurface)).reDraw();

    }

    public void onClickResetPartition(View v) {
        for (InstrumentPart ipTMP : app.getPartition().getParts()) {
            ipTMP.getNotes().clear();
        }
        ((InstrumentBoard)findViewById(R.id.boardSurface)).reDraw();

    }
    void setInstrument(int position) {

        app.setInstrument(app.getPartition().getPart(position));

    }

    public void onItemSelected(AdapterView<?> parent,View view, int position, long id) {

        setInstrument(position);

        ((InstrumentBoard)findViewById(R.id.boardSurface)).reDraw();

        InstrumentPart ip = app.getInstrument();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
    public void onClickPlayInstrument(View v) {
            app.playPart();


    }

    public void onClickPlayInstrumentPart(View v) {
        Partition pTmp = new Partition(app.getPartition().getTempo());
        pTmp.addPart(app.getInstrument().getInstrument(),app.getInstrument().getOctave());
        pTmp.getParts().set(0,app.getInstrument());
        app.playPart(pTmp);

    }
    public void onClickParam(View v) {
        Intent playIntent = new Intent(this, ParamActivity.class);
        startActivity(playIntent);
    }

    public void onClickSave(final View view) {
        app.save(InstrumentActivity.this);
    }

}
