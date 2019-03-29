package com.example.a3671129.musidroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import l2i013.musidroid.model.InstrumentPart;
import l2i013.musidroid.util.InstrumentName;
import l2i013.musidroid.util.MidiFile2I013;

public class PartitionActivity extends Activity {
    TheApplication app;
    ListView instrumentsLV;
    ArrayAdapter<InstrumentName> adapterIN;
    ArrayList<InstrumentName> instrumentsIN;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partition);
        app = (TheApplication)(this.getApplication());
        instrumentsLV = (ListView) findViewById(R.id.listView);


        Spinner spinnerinstrument = (Spinner) findViewById(R.id.instrumentChoice);
        List<InstrumentName> list = Arrays.asList(InstrumentName.values());
        final ArrayAdapter<InstrumentName> dataAdapter = new ArrayAdapter<InstrumentName>(this, R.layout.spinner_item, list);
        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerinstrument.setAdapter(dataAdapter);

        instrumentsIN = new ArrayList<>();
        for (InstrumentPart tmp : app.getPartition().getParts()) {
            instrumentsIN.add(tmp.getInstrument());
        }

        adapterIN = new ArrayAdapter<>(PartitionActivity.this,
                android.R.layout.simple_list_item_1,instrumentsIN);

        instrumentsLV.setAdapter(adapterIN);
        instrumentsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(PartitionActivity.this);
                builder.setTitle("Instruments");
                builder.setCancelable(false);

                builder.setItems(new CharSequence[]
                                {"Modifier","Effacer", "Retour"},
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // The 'which' argument contains the index position
                                // of the selected item
                                switch (which) {
                                    case 0:
                                        app.setInstrument(app.getPartition().getPart(position));
                                        Intent playIntent = new Intent(getApplicationContext(), InstrumentActivity.class);
                                        playIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(playIntent);
                                        break;
                                    case 1:
                                        if(app.getPartition().getParts().size() !=1) {
                                            AlertDialog.Builder adb=new AlertDialog.Builder(PartitionActivity.this);
                                            adb.setTitle("Liste d'Instruments");
                                            adb.setMessage("Effacer " + app.getPartition().getPart(position).getInstrument() + " ? ");
                                            adb.setCancelable(false);
                                            final int positionToRemove = position;
                                            adb.setPositiveButton("Confirmer", new AlertDialog.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {

                                                    instrumentsIN.remove(positionToRemove);
                                                    app.getPartition().removePart(positionToRemove);
                                                    Log.wtf("instrumentsIN",positionToRemove + " / " + instrumentsIN.toString());
                                                    adapterIN.notifyDataSetChanged();
                                                }});
                                            adb.setNegativeButton("Annuler", new AlertDialog.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {

                                                }});
                                            adb.show();


                                        }
                                        else
                                        {
                                            AlertDialog alertDialog = new AlertDialog.Builder(PartitionActivity.this).create();
                                            alertDialog.setTitle("Liste d'Instruments");
                                            alertDialog.setMessage("Il faut garder au moins un instrument !");
                                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();
                                                        }
                                                    });
                                            alertDialog.show();

                                        }
                                        break;
                                    case 2:
                                        //retour
                                        break;

                                }
                            }
                        });
                builder.show();
            }
        });

    }


    public void onClickJouer(View v) {
            MediaPlayer nPlayer = new MediaPlayer();
        MediaPlayer.OnPreparedListener nPrepared = new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer playerM) {
            }
        };
            nPlayer.setOnPreparedListener(nPrepared);
            try
            {
                TheApplication app = (TheApplication)this.getApplicationContext() ;
                File f = new File(app.getFilesDir(), "tmp.mid");
                MidiFile2I013.write(f, app.getPartition());
                nPlayer.setDataSource((f).getPath());
                nPlayer.setLooping(false);
                nPlayer.prepare();
                if (!nPlayer.isPlaying()) {
                    nPlayer.start();
                }

            }

            catch(Exception e)

            {
            }


    }
    public void onClickAddInstrument(View v) {
        Spinner spinnerinstrument = (Spinner) findViewById(R.id.instrumentChoice);

        int instrumentId = app.getPartition().addPart(InstrumentName.values()[spinnerinstrument.getSelectedItemPosition()],7);
        Log.d("onClickAddInstrument", app.getPartition().getPart(instrumentId).toString() );
        instrumentsIN.add(InstrumentName.values()[spinnerinstrument.getSelectedItemPosition()]);
        adapterIN.notifyDataSetChanged();


    }




    public void onClickTouch(View v) {
        Intent playIntent = new Intent(this, TouchActivity.class);
        startActivity(playIntent);

    }



    public void onClickInstrument(View v) {
        Intent playIntent = new Intent(this, InstrumentActivity.class);
        startActivity(playIntent);
    }



}
