package com.weeple.weeple.weeple;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RegistrationActivity extends ActionBarActivity {

    EditText email;
    EditText username;
    EditText password;
    EditText password2;
    Button register;
    String inputEmail;
    String inputUsername;
    String inputPassword;
    String inputPassword2;
    Intent intent;
    String serverResponse;
    Button goToLogin;
    RadioButton radio1;
    RadioButton radio2;
    String profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        email = (EditText) findViewById(R.id.editText2);
        username = (EditText) findViewById(R.id.editText);
        password = (EditText) findViewById(R.id.editText3);
        password2 = (EditText) findViewById(R.id.editText4);
        register = (Button) findViewById(R.id.button);
        goToLogin = (Button) findViewById(R.id.button4);
        radio1 = (RadioButton) findViewById(R.id.radio1);
        radio2 = (RadioButton) findViewById(R.id.radio2);
        intent = new Intent(this, LoginActivity.class);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (radio1.isChecked()){
                    profile = "constituent";
                } else if(radio2.isChecked()){
                    profile = "politician";
                }
                inputEmail = email.getText().toString();
                inputUsername = username.getText().toString();
                inputPassword = password.getText().toString();
                inputPassword2 = password2.getText().toString();
                if (inputPassword.equals(inputPassword2)) {
                    if (inputEmail.contains("@")) {
                        if(radio1.isChecked() || radio2.isChecked()) {
                            try {
                                serverResponse = new sendData().execute(inputEmail, inputUsername, inputPassword, profile).get().toString();
                            } catch (Exception e) {
                                Log.e("log_tag3", "FAILED TO GET RESPONSE FROM sendData");
                            }
                            String fileName = "Weeple_Authentification_Data.txt";
                            String content = inputUsername + "," + inputPassword + "," + profile;

                            FileOutputStream outputStream = null;
                            try {
                                outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
                                outputStream.write(content.getBytes());
                                outputStream.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Log.e("serverresponse", serverResponse);
                            if (serverResponse.equals("Thank you for registering!")) {
                                ((MyApplication) getApplicationContext()).setProfile(profile);
                                startActivity(intent);
                                finish();
                            } else {
                            }
                        } else {
                            Toast.makeText(getApplicationContext(),"Specify if you are a politician or a constituent",Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid e-mail", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Passwords don't match", Toast.LENGTH_LONG).show();
                }
            }
        });
        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_registration, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

class sendData extends AsyncTask<String,Void,String> {
    @Override
    protected String doInBackground(String... inputData) {
        String serverResponse;
        InputStream is = null;
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL("http://ec2-52-89-196-34.us-west-2.compute.amazonaws.com/Weeple/weeple_registration.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            OutputStream out = new BufferedOutputStream(conn.getOutputStream());
            String message = "email=" + inputData[0]+"&username=" + inputData[1]+"&password=" + inputData[2] + "&profile=" + inputData[3];
            out.write(message.getBytes());
            out.flush();
            out.close();
            is = conn.getInputStream();
            int responseCode = conn.getResponseCode();
            Log.i("log_tag", "POST Response Code :: " + responseCode);
        } catch (Exception e){
            Log.e("log_tag1", "CONNECTION FAILED in Registration Activity: " + e);
        }
        try {
            try {
                String line;
                br = new BufferedReader(new InputStreamReader(is));
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e){
            Log.e("log_tag3", "ERROR IN PARSING RESPONSE: " + e);
        }
        serverResponse = sb.toString();
        return serverResponse;
    }
}