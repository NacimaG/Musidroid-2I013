package com.example.a3671129.musidroid;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.example.a3671129.musidroid.myrequest.MyRequest;

import java.util.Map;


public class RegisterActivity extends AppCompatActivity {

    private Button btn_send;
    private TextInputLayout til_pseudo,til_email,til_password,til_password2;
    private ProgressBar pb_loader;
    private RequestQueue queue;
    private MyRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btn_send = (Button) findViewById(R.id.btn_send);
        pb_loader=(ProgressBar)findViewById(R.id.pb_loader);
        til_pseudo = (TextInputLayout) findViewById(R.id.til_pseudo);
        til_email = (TextInputLayout) findViewById(R.id.til_email);
        til_password = (TextInputLayout) findViewById(R.id.til_password);
        til_password2 = (TextInputLayout) findViewById(R.id.til_password2);

        queue = VolleySingleton.getInstance(this).getRequestQueue();
        request = new MyRequest(this, queue);

        btn_send.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                pb_loader.setVisibility(View.VISIBLE);
                String pseudo = til_pseudo.getEditText().getText().toString().trim();
                String email = til_email.getEditText().getText().toString().trim();
                String password = til_password.getEditText().getText().toString().trim();
                String password2 = til_password2.getEditText().getText().toString().trim();
                if(pseudo.length()>0 && email.length()>0 && password.length()>0 && password2.length()>0) {
                    request.register(pseudo, email, password, password2, new MyRequest.RegisterCallBack() {
                        @Override
                        public void onSuccess(String message) {
                            pb_loader.setVisibility(View.GONE);
                            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                            intent.putExtra("REGISTER",message);
                            startActivity(intent);
                            finish();

                        }

                        @Override
                        public void inputErrors(Map<String, String> errors) {
                        pb_loader.setVisibility(View.GONE);
                        if(errors.get("pseudo") != null ) {
                            til_pseudo.setError(errors.get("pseudo"));
                        }
                        else
                        {
                            til_pseudo.setErrorEnabled(false);
                        }
                            if(errors.get("email") != null ) {
                                til_email.setError(errors.get("email"));
                            }
                            else
                            {
                                til_email.setErrorEnabled(false);
                            }
                            if(errors.get("password") != null ) {
                                til_password.setError(errors.get("password"));
                            }
                            else
                            {
                                til_password.setErrorEnabled(false);
                            }
                        }

                        @Override
                        public void onError(String message) {
                            pb_loader.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else
                {
                    pb_loader.setVisibility(View.GONE);

                    Toast.makeText(getApplicationContext(),"Saisir tous les champs",Toast.LENGTH_SHORT).show();

                }
            }
        });
     }

}
