package com.example.a3671129.musidroid;
import android.app.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import l2i013.musidroid.model.InstrumentPart;
import l2i013.musidroid.model.Note;

public class ParamActivity extends Activity {

    TheApplication  app ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_param);
        app = (TheApplication)(this.getApplication());

        InstrumentPart ip = app.getInstrument();

        TextView octaveTexte = (TextView)findViewById(R.id.octaveText);

        octaveTexte.setText("("+ip.getOctave()+")");


        SeekBar octaveSeekBar=(SeekBar)findViewById(R.id.changeOctave);
        OctaveChangeListener octaveListener = new OctaveChangeListener(app, octaveTexte);
        octaveSeekBar.setOnSeekBarChangeListener(octaveListener);
        octaveSeekBar.setMax(10);
        octaveSeekBar.setProgress(ip.getOctave());

        TextView tempoTexte = (TextView)findViewById(R.id.tempoText);

        tempoTexte.setText("Partition\tTempo ("+app.getPartition().getTempo()+")");


        SeekBar tempoSeekBar=(SeekBar)findViewById(R.id.changeTempo);
        TempoChangeListener tempoListener = new TempoChangeListener(app, tempoTexte);
        tempoSeekBar.setOnSeekBarChangeListener(tempoListener);
        tempoSeekBar.setMax(500);
        tempoSeekBar.setProgress(app.getPartition().getTempo());


        final EditText instantsValueText = (EditText) findViewById(R.id.instantsValue);

        String content = instantsValueText.getText().toString();
        instantsValueText.setText("" +app.getInstants());
        instantsValueText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if (s.toString() != "") {

                        final int newInstants=Integer.parseInt(s.toString());
                        if (newInstants <= 993) {
                            if(newInstants<app.getInstants()) {
                                boolean trouveNoteNewInstant=false;
                                for(InstrumentPart ip : app.getPartition().getParts()) {
                                    for(Note n:ip.getNotes()) {
                                        if(n.getInstant()>newInstants) {
                                            trouveNoteNewInstant=true;
                                            break;
                                        }
                                    }

                                    if(!trouveNoteNewInstant)
                                        break;
                                }
                                if(trouveNoteNewInstant) {
                                    AlertDialog.Builder adb=new AlertDialog.Builder(ParamActivity.this);
                                    adb.setTitle("Musidroid");
                                    adb.setMessage("Des Notes saisies après l'instant "+(newInstants+6)+" seront perdues");
                                    adb.setCancelable(false);
                                    adb.setPositiveButton("Confirmer", new AlertDialog.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            for(InstrumentPart ip : app.getPartition().getParts()) {
                                                for(Note n:ip.getNotes()) {
                                                    if(n.getInstant()>newInstants+6) {
                                                        ip.removeNote(n.getInstant(),n.getName());
                                                    }
                                                }
                                            }
                                        }});
                                    adb.setNegativeButton("Annuler", new AlertDialog.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                        }});
                                    adb.show();
                                }
                                else
                                {
                                    for(InstrumentPart ip : app.getPartition().getParts()) {
                                        for(Note n:ip.getNotes()) {
                                            if(n.getInstant()>newInstants+6) {
                                                ip.removeNote(n.getInstant(),n.getName());
                                            }
                                        }
                                    }
                                }
                            }
                            for(InstrumentPart ip : app.getPartition().getParts()) {
                                for(Note n:ip.getNotes()) {
                                    if(n.getInstant()>newInstants+6) {
                                        ip.removeNote(n.getInstant(),n.getName());
                                    }
                                }
                            }
                            app.setInstants(newInstants);
                        }
                        else {
                            app.setInstants(993);
                            instantsValueText.setText("" +app.getInstants());
                            AlertDialog alertDialog = new AlertDialog.Builder(ParamActivity.this).create();
                            alertDialog.setTitle("Paramètres");
                            alertDialog.setCancelable(false);
                            alertDialog.setMessage("Le maximum d'instants est 993");
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
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

            }
        });

    }


}
