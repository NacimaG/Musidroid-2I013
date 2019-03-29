package com.example.a3671129.musidroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.example.a3671129.musidroid.myrequest.MyRequest;
import org.w3c.dom.Document;
import java.util.ArrayList;
import java.util.List;
import l2i013.musidroid.model.Partition;


public class MemberActivity extends Activity {
    TheApplication app;
    private TextView titleMemberTE;

    Context context;
    ListView listePartition;
    List<String> grilles = new ArrayList<>();
    ArrayAdapter<String> adapterG;

    private SessionManager sessionManager;
    private TextView textView;
    private Button btn_logout;

    private ListView boxParts;
    private ArrayAdapter<PartitionBDD> adapterDbParts;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);
        context=this;

        app = (TheApplication)(this.getApplication());


        titleMemberTE = (TextView) findViewById(R.id.titleMember);
        titleMemberTE.setText("\t" + app.getNamePartition());


        adapterG = new ArrayAdapter<String>(MemberActivity.this, android.R.layout.simple_list_item_1, grilles);
        listePartition = (ListView) findViewById(R.id.listePartitionsSauvegardees);

        final ArrayAdapter<String> adapterGer = adapterG;

        for (String f : fileList()) {
            String tampon = f;
            if (tampon.contains(".xml"))
                grilles.add(f);

        }

        listePartition.setAdapter(adapterGer);

        listePartition.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MemberActivity.this);
                builder.setTitle(grilles.get(position));

                final int finalPosition = position;

                builder.setItems(new CharSequence[]
                                {"Jouer","Charger", "Partager", "Effacer"},
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // The 'which' argument contains the index position
                                // of the selected item
                                switch (which) {
                                    case 0:

                                        app.playPart(Tools.docToPartition(Tools.readXMLFile(getFilesDir() + "/"+grilles.get(finalPosition))));

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
                                        finish();
                                        Toast.makeText(MemberActivity.this, grilles.get(position) + " chargé", Toast.LENGTH_SHORT).show();

                                        break;
                                    case 2:
                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                        builder.setCancelable(false);
                                        builder.setTitle("Partager "+grilles.get(finalPosition)+" avec");
                                        final EditText input = new EditText(context);
                                        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT);
                                        builder.setView(input);
                                        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                RequestQueue queue = VolleySingleton.getInstance(context).getRequestQueue();
                                                MyRequest request = new MyRequest(context, queue);
                                                    request.sendPartition(sessionManager.getId(), Tools.xmlString(getFilesDir() + "/" + grilles.get(finalPosition)), input.getText().toString(), new MyRequest.sendPartitionCallBack() {

                                                        public void onSuccess(String message) {
                                                            Toast.makeText(MemberActivity.this, message, Toast.LENGTH_SHORT).show();
                                                        }

                                                        @Override
                                                        public void onError(String message) {
                                                            //  pb_loader.setVisibility(View.GONE);
                                                            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                                                            alertDialog.setCancelable(false);
                                                            alertDialog.setTitle("Echec de partage");
                                                            alertDialog.setMessage(message);
                                                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                                                    new DialogInterface.OnClickListener() {
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                            dialog.dismiss();
                                                                        }
                                                                    });
                                                            alertDialog.show();


                                                        }
                                                    });


                                            }

                                });



                                        builder.setPositiveButton("Annuler", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        });

                                        builder.show();
                                        break;
                                    case 3:
                                        deleteFile(grilles.get(finalPosition));
                                        grilles.remove(finalPosition);
                                        adapterGer.notifyDataSetChanged();
                                        Toast.makeText(MemberActivity.this, "Partition effacée", Toast.LENGTH_SHORT).show();

                                        break;

                                }
                            }
                        });
                builder.show();
            }
        });

        textView=(TextView)findViewById(R.id.titleMember);
        btn_logout = (Button)findViewById(R.id.btn_logout);
        sessionManager = new SessionManager(this);
        if(sessionManager.booleanIsLogged()) {
            String pseudo= sessionManager.getPseudo();
            textView.setText(pseudo);
        }
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.logout();
                Intent intent = new Intent(context,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });



        boxParts = (ListView) findViewById(R.id.listePartitionsRecues);
        showBoxParts(app.getDbParts());

        context=this;
        synchronized (boxParts) {
            boxParts.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(MemberActivity.this);
                    builder.setTitle(app.getDbParts().get(position).getName());

                    final int finalPosition = position;

                    builder.setItems(new CharSequence[]
                                    {"Jouer", "Charger", "Effacer"},
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0:

                                            app.playPart(app.getDbParts().get(finalPosition).getPartition());
                                            Toast.makeText(MemberActivity.this, app.getDbParts().get(finalPosition).getName() + " jouée", Toast.LENGTH_SHORT).show();

                                            break;
                                        case 1:
                                            Partition p = app.getDbParts().get(finalPosition).getPartition();
                                            PartitionBDD dbP = app.getDbParts().get(finalPosition);
                                            app.loadPartition(p, dbP.getName(), dbP.getInstants());

                                            adapterDbParts.notifyDataSetChanged();

                                            Intent intent = new Intent(getApplicationContext(), PartitionActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            getApplicationContext().startActivity(intent);
                                            finish();
                                            Toast.makeText(MemberActivity.this, app.getDbParts().get(finalPosition).getName() + " chargée", Toast.LENGTH_SHORT).show();

                                            break;
                                        case 2:
                                            //app.getDbParts().get(finalPosition)
                                            //Tools.deleteDbPart(finalPosition,sessionManager.getId());
                                            RequestQueue queue = VolleySingleton.getInstance(context).getRequestQueue();
                                            MyRequest request = new MyRequest(context, queue);
                                            request.deletePartition(sessionManager.getId(), app.getDbParts().get(finalPosition).getId(), new MyRequest.deletePartitionCallBack() {

                                                public void onSuccess(String message) {
                                                    Toast.makeText(MemberActivity.this, message, Toast.LENGTH_SHORT).show();

                                                    app.getDbParts().remove(finalPosition);
                                                    adapterDbParts.notifyDataSetChanged();
                                                    refresh();
                                                }

                                                @Override
                                                public void onError(String message) {
                                                    //  pb_loader.setVisibility(View.GONE);
                                                    AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                                                    alertDialog.setCancelable(false);
                                                    alertDialog.setTitle("Echec de suppression");
                                                    alertDialog.setMessage(message);
                                                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                                            new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    dialog.dismiss();
                                                                }
                                                            });
                                                    alertDialog.show();


                                                }
                                            });

                                            Toast.makeText(MemberActivity.this, "Partition effacée", Toast.LENGTH_SHORT).show();

                                            break;

                                    }
                                }
                            });
                    builder.show();
                }
            });
        }

        synchronized (app) {
            refresh();
        }

    }



    public void showBoxParts(ArrayList<PartitionBDD> dbParts) {
        if(dbParts!=null) {
            adapterDbParts = new ArrayAdapter<PartitionBDD>(MemberActivity.this, android.R.layout.simple_list_item_1, app.getDbParts());
            boxParts = (ListView) findViewById(R.id.listePartitionsRecues);
            boxParts.setAdapter(adapterDbParts);
        }
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
        refresh();

    }

    public void refresh() {
     app.updateDbParts(sessionManager.getId());
       showBoxParts(app.getDbParts());
    }
    public void onClickRefresh(View v) {
        refresh();
    }

    }
