package com.example.a3671129.musidroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.example.a3671129.musidroid.myrequest.MyRequest;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import l2i013.musidroid.model.InstrumentPart;
import l2i013.musidroid.model.Note;
import l2i013.musidroid.model.Partition;
import l2i013.musidroid.util.InstrumentName;
import l2i013.musidroid.util.MidiFile2I013;
import l2i013.musidroid.util.NoteName;

public class TheApplication extends Application {
    String namePartition;

    public void setP(Partition p) {
        this.p = p;
    }

    private Partition p;

    private InstrumentPart instrument;

    private int instantDebut = 0;
    private ArrayList<PartitionBDD> dbParts=new ArrayList<PartitionBDD>();

    private int instants = 44;

    public ArrayList<PartitionBDD> getDbParts() {
        return dbParts;
    }




    @Override
    public void onCreate() {
        super.onCreate();
        p = new Partition(120);
        p.addPart(InstrumentName.ACOUSTIC_GRAND_PIANO, 7);
        p.addPart(InstrumentName.VIOLIN, 7);
        p.addPart(InstrumentName.GUITAR_HARMONICS, 7);
        p.addPart(InstrumentName.CLAVINET, 7);
        p.addPart(InstrumentName.PAD_1_NEW_AGE, 7);
        instrument = p.getPart(0);


    }

    InstrumentPart getInstrument() {
        return instrument;
    }

    void setInstrument(InstrumentPart instrument) {
        this.instrument = instrument;

    }

    public Partition getPartition() {

        return p;
    }

    public void updateDbParts (int memberID) {
        synchronized(dbParts) {
            RequestQueue queue;
            MyRequest request;
            queue = VolleySingleton.getInstance(this).getRequestQueue();
            request = new MyRequest(this, queue);
            request.getParts(memberID, new MyRequest.getPartsCallBack() {
                @Override
                public void onSuccess(ArrayList<PartitionBDD> dbParts) {
                    setDbParts(dbParts);
                    Log.d("loadPartsBox:onSuccess", "Partitions chargées");
                }

                @Override
                public void onError(String message) {
                    Log.d("loadPartsBox", "onErrorMember");


                }
            });
        }
    }

    public void savePartition(String fname) {

            FileOutputStream oc = null;
            String xml = Tools.partitionToXML(p,namePartition,instants);

            try {
                oc = openFileOutput(fname, Context.MODE_PRIVATE);
                oc.write(xml.getBytes());
                oc.close();
            } catch (FileNotFoundException e) {
            } catch (IOException e) {
                try {
                    oc.close();
                } catch (IOException ee) {
                }
            }




}
    public void setDbParts(ArrayList<PartitionBDD> dbParts) {
        this.dbParts = dbParts;
    }


    public int getInstants() {
        return instants;
    }

    public void setInstants(int instants) {
        this.instants = instants;
    }

    public int getInstantDebut() {
        return instantDebut;
    }

    public void setInstantDebut(int instantDebut) {
        this.instantDebut = instantDebut;
    }

    String getNamePartition() {
        return namePartition;
    }
    public void setNamePartition(String namePartition) {
        this.namePartition = namePartition;
    }


    public void playPart(Partition p) {

        MediaPlayer.OnPreparedListener nPrepared = new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer playerM) {
            }
        };
        MediaPlayer nPlayer = new MediaPlayer();

        nPlayer.setOnPreparedListener(nPrepared);
        try
        {
            File f = new File(getFilesDir(), "tmp.mid");
            MidiFile2I013.write(f, p);
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

    public void playPart () {
        playPart(p);
    }
    public void loadPartition(Partition p, String namePartition, int instants)  {
        this.p=p;
        this.instrument=p.getPart(0);
        this.namePartition=namePartition;
        this.instants=instants;
    }
    public  void save(final Activity activity) {

        boolean presenceNote = false;
        for (InstrumentPart ip : getPartition().getParts()) {
            if (!ip.getNotes().isEmpty()) {
                presenceNote = true;
                break;
            }
        }
        if (presenceNote) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context)activity);
            builder.setCancelable(false);
            builder.setTitle("Nom de la nouvelle Partition");

// Set up the input
            final EditText input = new EditText((Context)activity);
// Specify the type of input expected; (Context)activity, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT);
            input.setText(getNamePartition(), TextView.BufferType.EDITABLE);

            builder.setView(input);

// Set up the buttons
            builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (input.getText().toString().matches("^[a-zA-Z]{1,20}[0-9]{0,3}$")) {
                        setNamePartition(input.getText().toString());
                        savePartition(input.getText().toString() + ".xml");

                        Toast.makeText((Context)activity, "Partition enregistrée", Toast.LENGTH_SHORT).show();
                        //activity.finish();
                    } else {
                        Log.wtf("Invalide", input.getText().toString());
                        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
                        alertDialog.setCancelable(false);
                        alertDialog.setTitle("Nom de la nouvelle Partition");
                        alertDialog.setMessage("Saisir un nom valide !");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    }
                }
            });
            builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();

        } else

        {
            AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
            alertDialog.setTitle("Musidroid");
            alertDialog.setCancelable(false);
            alertDialog.setMessage("On n'enregistre pas une Partition vide");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }


    }
}