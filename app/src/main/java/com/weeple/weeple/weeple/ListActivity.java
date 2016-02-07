package com.weeple.weeple.weeple;

import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ListActivity extends android.app.ListActivity {

    Button logOut;
    Intent logOutIntent;
    Bundle logOutBooleanBundle;
    ArrayList<String> texts;
    ArrayList<String> pluses;
    ArrayList<String> minuses;
    ArrayList<String> usernames;
    ArrayList<String> images;
    ArrayList<String> rotations;
    String username;
    ImageView photo;
    //ListView listView;
    //ListAdapter adapter;
    ArrayList<String> allPoliticians = new ArrayList<>();
    ArrayList<String> allImagePaths = new ArrayList<>();
    ArrayList<String> allRotationData = new ArrayList<>();
    ArrayList[] data;
    Bundle bundle;
    Intent intent;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_new);
        //setSupportActionBar(toolbar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setActionBar(toolbar1);
        photo = (ImageView) findViewById(R.id.icon);
        username = ((MyApplication) getApplicationContext()).getUsername();
        setTitle("Welcome " + username + "!");
        //listView = (ListView) findViewById(R.id.listView);
        logOut = (Button) findViewById(R.id.button6);
        logOutIntent = new Intent(this,LoginActivity.class);
        logOutBooleanBundle = new Bundle();
        bundle = new Bundle();
        intent = new Intent(this,PoliticianActivity.class);
        //allPoliticians.add("hi");
        //allPoliticians.add("my name");
        //allPoliticians.add("is");
        //refresh();
        try {
            data = new getPoliticiansData().execute().get();
        } catch(Exception e ){
            Log.e("log_error", "failed to get polit data");
        }
        if (data != null) {
            allPoliticians = data[0];
            allImagePaths = data[1];
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, R.layout.mylist,
                R.id.Itemname,allPoliticians);
        this.setListAdapter(adapter);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File authData = new File(getFilesDir(), "Weeple_Authentification_Data.txt");
                Boolean infoDeleted = authData.delete();
                logOutBooleanBundle.putBoolean("infoDeleted", infoDeleted);
                logOutIntent.putExtras(logOutBooleanBundle);
                startActivity(logOutIntent);
                finish();
            }
        });

    }

    /*private void refresh() {

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_gallery_item, allPoliticians);
        listView.setAdapter(adapter);
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id){
        super.onListItemClick(l, v, position, id);
        bundle.putString("username", allPoliticians.get(position));
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_presidential) {
            setTitle("Presidential Candidates");
            return true;
        }

        if (id == R.id.action_federal) {
            setTitle("Federal politicians");
            return true;
        }

        if (id == R.id.action_local) {
            setTitle("Local politicians");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

class getPoliticiansData extends AsyncTask<String, Void, ArrayList[]>{

    @Override
    protected ArrayList[] doInBackground(String... params){
        ArrayList<String> allPoliticians = new ArrayList<>();
        ArrayList<String> allImagePaths = new ArrayList<>();
        ArrayList<String> allRotationData = new ArrayList<>();
        ArrayList[] data = {allPoliticians, allImagePaths, allRotationData};
        InputStream input = null;
        String result = "";

        try{
            URL url = new URL("http://ec2-52-89-196-34.us-west-2.compute.amazonaws.com/Weeple/retrieve_all_politicians.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            OutputStream out = new BufferedOutputStream(conn.getOutputStream());

            input = conn.getInputStream();
            int responseCode = conn.getResponseCode();
        } catch (Exception e) {
            Log.e("log_error", "failed to retrieve data");
        }
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(input,"iso-8859-1"),8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null){
                sb.append(line + "/n");
            }
            input.close();

            result = sb.toString();
        }
        catch(Exception e){
            Log.e("log_tag2", "Error converting result".toString());
        }

        try{
            JSONArray jArray = new JSONArray(result);

            for(int i = 0; i < jArray.length(); i++){
                JSONObject json = jArray.getJSONObject(i);
                allPoliticians.add(new String(json.getString("username")));
                allImagePaths.add(new String(json.getString("image")));
                allRotationData.add(new String(json.getString("rotation")));
            }
        }
        catch(Exception e){
            Log.e("log_tag3", "Error Parsing Data " + e.toString());
        }
        return data;
    }
}
