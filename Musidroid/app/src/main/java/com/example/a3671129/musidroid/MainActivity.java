package com.example.a3671129.musidroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.example.a3671129.musidroid.myrequest.MyRequest;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import l2i013.musidroid.model.Partition;
import l2i013.musidroid.util.MidiFile2I013;

public class MainActivity extends Activity {
    TheApplication app;
    List<String> grilles = new ArrayList<>();
    ArrayAdapter<String> adapterG;
    private TextInputLayout til_pseudo,til_password;
    private Button btn_send;
    private ProgressBar pb_loader;
    private RequestQueue queue;
    private MyRequest request;
    private Handler handler;
    private SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        app = (TheApplication) (this.getApplication());
        Button btn_register = (Button) findViewById(R.id.btn_register);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });
        adapterG = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, grilles);
        ListView listePartition = (ListView) findViewById(R.id.list_partitions);

        final ArrayAdapter<String> adapterGer = adapterG;

        for (String f : fileList()) {
            String tampon = f;
            if (tampon.contains(".xml"))
                grilles.add(f);

        }

        listePartition.setAdapter(adapterGer);

        listePartition.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(grilles.get(position));

                final int finalPosition = position;

                builder.setItems(new CharSequence[]
                                {"Jouer","Charger", "Effacer"},
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // The 'which' argument contains the index position
                                // of the selected item
                                switch (which) {
                                    case 0:

                                        MediaPlayer nPlayer = new MediaPlayer();
                                        MediaPlayer.OnPreparedListener nPrepared = new MediaPlayer.OnPreparedListener() {
                                            public void onPrepared(MediaPlayer playerM) {
                                            }
                                        };
                                        nPlayer.setOnPreparedListener(nPrepared);
                                        try
                                        {
                                            File f = new File(app.getFilesDir(), "tmp.mid");
                                            MidiFile2I013.write(f, Tools.docToPartition(Tools.readXMLFile(getFilesDir() + "/"+grilles.get(finalPosition))));
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

                                        break;
                                    case 1:
                                        Document doc = Tools.readXMLFile(getFilesDir() + "/" + grilles.get(finalPosition));
                                        Partition p = Tools.docToPartition(doc);
                                        app.setP(p);
                                        app.setInstrument(p.getPart(0));
                                        app.setNamePartition(grilles.get(position).replace(".xml", ""));
                                        adapterGer.notifyDataSetChanged();

                                        Intent intent = new Intent(getApplicationContext(), PartitionActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        getApplicationContext().startActivity(intent);
                                        Toast.makeText(MainActivity.this, grilles.get(position) + " chargé", Toast.LENGTH_SHORT).show();

                                        break;
                                    case 2:
                                        deleteFile(grilles.get(finalPosition));
                                        grilles.remove(finalPosition);
                                        adapterGer.notifyDataSetChanged();
                                        Toast.makeText(MainActivity.this, "Partition effacée", Toast.LENGTH_SHORT).show();
                                        //Log.wtf("Grilles arraylist",""+grilles);

                                        break;

                                }
                            }
                        });
                builder.show();
            }
        });
        til_pseudo=(TextInputLayout)findViewById(R.id.til_pseudo);
        til_password=(TextInputLayout)findViewById(R.id.til_password);
        btn_send=(Button)findViewById(R.id.btn_send);
        pb_loader=(ProgressBar)findViewById(R.id.pb_loader);

        queue = VolleySingleton.getInstance(this).getRequestQueue();
        request = new MyRequest(this, queue);
        handler = new Handler();
        sessionManager=new SessionManager(this);
        btn_send.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                final String pseudo = til_pseudo.getEditText().getText().toString().trim();
                final String password = til_password.getEditText().getText().toString().trim();
                pb_loader.setVisibility(View.VISIBLE);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        request.connection(pseudo, password, new MyRequest.LoginCallBack() {


                            @Override
                            public void onSuccess(int id, String pseudo) {

                                pb_loader.setVisibility(View.GONE);
                                sessionManager.insertUser(id,pseudo);
                                Intent intent = new Intent(getApplicationContext(),MemberActivity.class);
                                synchronized (intent) {
                                    app.updateDbParts(id);
                                }
                                startActivity(intent);
                                finish();

                            }

                            @Override
                            public void onError(String message) {
                                pb_loader.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                },1000);
                if(pseudo.length()>0 && password.length()>0) {

                }
                else
                {

                    Toast.makeText(getApplicationContext(),"Remplir tous les champs",Toast.LENGTH_SHORT).show();

                }
            }
        });
    }




    public void onClickExit(View v) {
        finish();
    }


    public void onClickNewPartition(View v) {

                    Intent intent = new Intent(getApplicationContext(), PartitionActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(intent);
   }
public void onResume() {
        super.onResume();
        grilles.clear();
        String [] files = fileList();
    for (int i = 0; i < files.length; i++) {
        String tampon = files[i];
        if (tampon.contains(".xml"))
            grilles.add(files[i]);

    }
    adapterG.notifyDataSetChanged();
}
}

