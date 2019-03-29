package com.example.a3671129.musidroid.myrequest;

import android.content.Context;
import android.text.Editable;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.a3671129.musidroid.PartitionBDD;
import com.example.a3671129.musidroid.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;


public class MyRequest {

    private Context context;
    private RequestQueue queue;

    public MyRequest(Context context, RequestQueue queue) {
        this.context = context;
        this.queue = queue;
    }

    public void register(final String pseudo, final String email, final String password, final String password2, final RegisterCallBack callback){

        String url = "http://musidroid.alwaysdata.net/register.php";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Map<String,String> errors = new HashMap<>();

                try {
                    JSONObject json = new JSONObject(response);
                    Boolean error = json.getBoolean("error");

                    if (!error) {
                        callback.onSuccess("Inscription effectuee");
                    } else {
                        JSONObject messages = json.getJSONObject("message");
                        if (messages.has("pseudo")) {
                            errors.put("pseudo", messages.getString("pseudo"));
                        }
                        if (messages.has("email")) {
                            errors.put("email", messages.getString("email"));
                        }
                        if (messages.has("password")) {
                            errors.put("password", messages.getString("password"));
                        }
                        callback.inputErrors(errors);
                    }
                } catch(JSONException e) {
                    e.printStackTrace();
                }

                Log.wtf("APP", response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NetworkError) {
                    callback.onError("Impossible de se connecter à internet");
                } else if (error instanceof VolleyError) {
                    Log.d("APP", "ERROR = " + error);
                callback.onError("Une erreur s'est produite");
            }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<>();
                map.put("pseudo", pseudo);
                map.put("email", email);
                map.put("password", password);
                map.put("password2", password2);

                return map;
            }
        };

        queue.add(request);

    }
    public interface RegisterCallBack {
        void onSuccess(String message);
        void inputErrors(Map<String,String> errors);
        void onError(String message);

    }
    public void connection(final String pseudo, final String password, final LoginCallBack callback) {
        String url = "http://musidroid.alwaysdata.net/login.php";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject json = new JSONObject(response);
                    Boolean error = json.getBoolean("error");

                    if (!error) {
                        int id = json.getInt("id");
                        String pseudo = json.getString("pseudo");

                        callback.onSuccess(id,pseudo);
                    } else {
                        callback.onError(json.getString("message"));
                    }
                } catch(JSONException e) {
                    e.printStackTrace();
                }

                Log.wtf("APP", response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NetworkError) {
                    callback.onError("Impossible de se connecter à internet");
                } else if (error instanceof VolleyError) {
                    Log.d("APP", "ERROR = " + error);
                    callback.onError("Une erreur s'est produite");
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<>();
                map.put("pseudo", pseudo);
                map.put("password", password);

                return map;
            }
        };

        queue.add(request);
    }
    public interface LoginCallBack {
        void onSuccess(int id, String pseudo);
        void onError(String message);
    }

    public void sendPartition(final int idSender, final String xml, final String pseudoReceiver, final sendPartitionCallBack callback) {
        String url = "http://musidroid.alwaysdata.net/partition.php?send=ON";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    Boolean error = json.getBoolean("error");

                    if (!error) {
                        String message = json.getString("message");
                        callback.onSuccess(message);
                    } else {
                        callback.onError(json.getString("message"));
                    }
                } catch(JSONException e) {
                    e.printStackTrace();
                }

                Log.wtf("APP", response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NetworkError) {
                    callback.onError("Impossible de se connecter à internet");
                } else if (error instanceof VolleyError) {
                    Log.d("APP", "ERROR = " + error);
                    callback.onError("Une erreur s'est produite");
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<>();
                map.put("idSender", ""+idSender);
                map.put("xml", xml);
                map.put("pseudoReceiver",""+pseudoReceiver);

                return map;
            }
        };

        queue.add(request);
    }
    public interface sendPartitionCallBack {
        void onSuccess(String message);
        void onError(String message);
    }

    public void getParts (final int memberId, final getPartsCallBack callback) {
        String url = "http://musidroid.alwaysdata.net/partition.php?getParts=ON";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject json = new JSONObject(response);
                    Boolean error = json.getBoolean("error");
                    System.out.println("Error est"+error.toString());
                    if (!error) {
                        //JSONObject partitions = json.getJSONObject("partitions");
                        String partitionsString = json.getString("partitions");
                        System.out.println("alors "+partitionsString.substring(partitionsString.indexOf("{"), partitionsString.lastIndexOf("}") + 1));

                        JSONObject partitions = new JSONObject(partitionsString.substring(partitionsString.indexOf("{"), partitionsString.lastIndexOf("}") + 1));


                        callback.onSuccess(Tools.jsonObjectToDbParts(partitions));
                    } else {
                        callback.onError(json.getString("message"));


                    }
                } catch(JSONException e) {
                    e.printStackTrace();
                    Log.wtf("JSONException","probleme avec le JSONOBject");
                }

                Log.wtf("APP", response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NetworkError) {
                    callback.onError("Impossible de se connecter à internet");
                } else if (error instanceof VolleyError) {
                    Log.d("APP", "ERROR = " + error);
                    callback.onError("Une erreur s'est produite");
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<>();
                map.put("memberId", ""+memberId);

                return map;
            }
        };

        queue.add(request);
    }



    public interface getPartsCallBack {
        void onSuccess(ArrayList<PartitionBDD> dbParts);
        void onError(String message);
    }



    public void deletePartition(final int memberId, final int partitionId, final deletePartitionCallBack callback) {
        String url = "http://musidroid.alwaysdata.net/partition.php?delete=ON";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    Boolean error = json.getBoolean("error");

                    if (!error) {
                        String message = json.getString("message");
                        callback.onSuccess(message);
                    } else {
                        callback.onError(json.getString("message"));
                    }
                } catch(JSONException e) {
                    e.printStackTrace();
                }

                Log.wtf("APP", response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NetworkError) {
                    callback.onError("Impossible de se connecter à internet");
                } else if (error instanceof VolleyError) {
                    Log.d("APP", "ERROR = " + error);
                    callback.onError("Une erreur s'est produite");
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<>();
                map.put("memberId", ""+memberId);
                map.put("partitionId",""+partitionId);

                return map;
            }
        };

        queue.add(request);
    }
    public interface deletePartitionCallBack {
        void onSuccess(String message);
        void onError(String message);
    }





}
