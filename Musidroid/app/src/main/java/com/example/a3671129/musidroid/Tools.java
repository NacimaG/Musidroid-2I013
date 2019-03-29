package com.example.a3671129.musidroid;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import l2i013.musidroid.model.InstrumentPart;
import l2i013.musidroid.model.Note;
import l2i013.musidroid.model.Partition;
import l2i013.musidroid.util.InstrumentName;
import l2i013.musidroid.util.NoteName;

public abstract class Tools {
    public static Document readXMLFile(String fname) {
        try {
            DocumentBuilderFactory factory =
                    DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(new FileInputStream(fname));
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static Document xmlToDoc(String xml) {
        Document doc;
        try {
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));

            doc = db.parse(is);
        }
        catch(Exception e) {
            e.printStackTrace();
            return null;
        }

        return doc;
    }

    public static Partition docToPartition(Document doc) {
        if (doc == null) {
            Log.wtf("DOC", "DOC est NULL ! ");
            return null;
        }


        final Element racine = doc.getDocumentElement();



        final NodeList racineNoeuds = racine.getChildNodes();
        final int nbRacineNoeuds = racineNoeuds.getLength();
        Partition partition = new Partition(Integer.parseInt(racine.getAttribute("tempo")));

        for (int i = 0; i < nbRacineNoeuds; i++) {
            if (racineNoeuds.item(i).getNodeType() == Node.ELEMENT_NODE) {
                final Element instrumentPart = (Element) racineNoeuds.item(i);


                int idIp = partition.addPart(InstrumentName.valueOf(instrumentPart.getAttribute("name")), Integer.parseInt(instrumentPart.getAttribute("octave")));
                final NodeList notes = instrumentPart.getElementsByTagName("note");
                final int nbNotes = notes.getLength();
                InstrumentPart ip = partition.getPart(idIp);

                for (int j = 0; j < nbNotes; j++) {

                    final Element note = (Element) notes.item(j);
                    ip.addNote(Integer.parseInt(note.getAttribute("instant")), NoteName.valueOf(note.getAttribute("name").replace("#", "DIESE")), Integer.parseInt(note.getAttribute("duration")));

                }
            }
        }
        return partition;

    }

    public static String docToPartName(Document doc) {
        if (doc == null) {
            Log.wtf("DOC", "DOC est NULL ! ");
            return null;

        }
        final Element racine = doc.getDocumentElement();
        return racine.getAttribute("name");

    }
    public static int docToInstants(Document doc) {
        if (doc == null) {
            Log.wtf("DOC", "DOC est NULL ! ");

        }
            final Element racine = doc.getDocumentElement();
            return Integer.parseInt(racine.getAttribute("instantDerniereGrille"));

    }

    public static String partitionToXML(Partition p,String namePartition, int instants) {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +

                "<Partition tempo=\"" + p.getTempo() + "\" name=\""+namePartition+"\" instantDerniereGrille=\""+instants+"\"> \n";
        for (InstrumentPart ip : p.getParts()) {
            xml = xml + "\t<instrument_part name=\"" + ip.getInstrument() + "\" octave=\"" + ip.getOctave() + "\">\n";
            for (Note n : ip.getNotes()) {
                xml = xml + "\t\t<note name=\"" + n.getName() + "\" instant=\"" + n.getInstant() + "\" duration=\"" + n.getDuration() + "\"/>\n";
            }


            xml = xml + "\t</instrument_part>\n";
        }

        xml = xml + "</Partition>";
        return xml;
    }
    public static ArrayList<PartitionBDD> jsonObjectToDbParts(JSONObject json) {
        ArrayList<PartitionBDD> boxParts =new  ArrayList<PartitionBDD>();
        try {
            for (Iterator iterator = json.keys(); iterator.hasNext(); ) {
                Object cle = iterator.next();
                Object val = json.get(String.valueOf(cle));
                Document doc = xmlToDoc(val.toString());
                synchronized (doc) {
                    if (doc != null) {
                        PartitionBDD dbPart = new PartitionBDD(docToPartition(doc), Integer.parseInt(cle.toString()), docToPartName(doc), docToInstants(doc));

                        boxParts.add(dbPart);
                    } else {
                        Log.d("Doc jsonObjectToDbParts", "doc est null");
                    }
                }
            }
        }
        catch(JSONException e) {
            e.printStackTrace();
        }
        return boxParts;
    }

    public static String xmlString(String fchemin) {
 String contenu="";
        try{
            FileInputStream flux = new FileInputStream(fchemin);
            InputStreamReader lecture=new InputStreamReader(flux);
            BufferedReader buff=new BufferedReader(lecture);
            String ligne="";
            while ((ligne=buff.readLine())!=null){
                Log.wtf("contenu","ligne = "+ligne);

                contenu= new String(contenu.toString()+ligne.toString());
            }
            buff.close();
        }
        catch (Exception e){
            Log.wtf("xmlString : Exception","Exception survenue lors la lecture du fichier "+fchemin);
           e.printStackTrace();
        }
        return contenu;
    }



}
