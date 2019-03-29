package com.example.a3671129.musidroid;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private final static String PREFS_NAME = "app_prefs";
    private final static int PRIVATE_MODE=0;
    private final static String IS_LOGGED = "isLogged";
    private final static String PSEUDO = "pseudo";
    private final static String ID="id";
    private Context context;

    public SessionManager(Context context) {
        this.context=context;
        prefs=context.getSharedPreferences(PREFS_NAME,PRIVATE_MODE);
        editor = prefs.edit();
    }
    public boolean booleanIsLogged() {
        return prefs.getBoolean(IS_LOGGED, false);
    }
    public String getPseudo() {
        return prefs.getString(PSEUDO, null);
    }
    public int getId() {
        return prefs.getInt(ID,0);
    }
    public void insertUser(int id,String pseudo) {
        editor.putBoolean(IS_LOGGED, true);
        editor.putInt(ID, id);
        editor.putString(PSEUDO, pseudo);
        editor.commit();
    }
    public void logout() {
        editor.clear().commit();
    }
    }

