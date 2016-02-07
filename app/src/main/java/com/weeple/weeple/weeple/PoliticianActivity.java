package com.weeple.weeple.weeple;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class PoliticianActivity extends AppCompatActivity {

    Button logOut;
    Intent logOutIntent;
    Bundle logOutBooleanBundle;

    ImageView profilePic;
    TextView name;

    EditText firstEditS;
    EditText secondEditS;
    EditText thirdEditS;
    EditText fourthEditS;
    EditText fifthEditS;
    String stringFirstEditS;
    String stringSecondEditS;
    String stringThirdEditS;
    String stringFourthEditS;
    String stringFifthEditS;

    TextView firstTextS;
    TextView secondTextS;
    TextView thirdTextS;
    TextView fourthTextS;
    TextView fifthTextS;

    TextView firstEval;
    TextView secondEval;
    TextView thirdEval;
    TextView fourthEval;
    TextView fifthEval;

    Button submit;

    String username;
    String profile;

    String imagePath;
    Integer rotation;

    Intent recoverIntentSelection;

    ArrayList<String> imageData = new ArrayList<>();

    Bitmap bm;

    ArrayList<String> statementsData = new ArrayList<>();

    final int SELECT_PHOTO = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_politician);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recoverIntentSelection = getIntent();
        Bundle bundle = recoverIntentSelection.getExtras();
        //logOut = (Button) findViewById(R.id.button5);
        username = ((MyApplication) getApplicationContext()).getUsername();
        profile  = ((MyApplication) getApplicationContext()).getProfile();
        profilePic = (ImageView) findViewById(R.id.imageView2);
        name = (TextView) findViewById(R.id.textView3);
        firstEditS = (EditText) findViewById(R.id.editText7);
        secondEditS = (EditText) findViewById(R.id.editText8);
        thirdEditS = (EditText) findViewById(R.id.editText9);
        fourthEditS = (EditText) findViewById(R.id.editText10);
        fifthEditS = (EditText) findViewById(R.id.editText11);
        firstTextS = (TextView) findViewById(R.id.textView12);
        secondTextS = (TextView) findViewById(R.id.textView13);
        thirdTextS = (TextView) findViewById(R.id.textView14);
        fourthTextS = (TextView) findViewById(R.id.textView15);
        fifthTextS = (TextView) findViewById(R.id.textView16);
        firstEval = (TextView) findViewById(R.id.textView7);
        secondEval = (TextView) findViewById(R.id.textView8);
        thirdEval = (TextView) findViewById(R.id.textView9);
        fourthEval = (TextView) findViewById(R.id.textView10);
        fifthEval = (TextView) findViewById(R.id.textView11);
        submit = (Button) findViewById(R.id.button7);
        if(bundle != null){
            username = bundle.getString("username");
        }



        try {
            statementsData = new getStatementsData().execute(username).get();
        } catch(Exception e) {
            Log.e("log_error","fail to get statementsData");
        }

        if (profile.equals("politician")) {
            setTitle(username);
            name.setText(username);
            try {
                Log.e("log_check","trying getPicPath");
                imageData = new getPicPath().execute(username).get();
            } catch (Exception e){
                Log.e("log_error","failed to retrieve imageData");
            }
            if (!imageData.get(0).equals("null")) {
                Log.e("log_check", imageData.get(0));
                imagePath = "http://ec2-52-89-196-34.us-west-2.compute.amazonaws.com/Weeple/ProfilePics/" + imageData.get(0);
                rotation = Integer.valueOf(imageData.get(1));
                try {
                    bm = new setProfilePic().execute(imagePath).get();
                } catch (Exception e) {
                    Log.e("log_error", "failed to get bitmap");
                }
                profilePic.setImageBitmap(bm);
                profilePic.setRotation((float) rotation);
            }
            firstTextS.setVisibility(View.GONE);
            secondTextS.setVisibility(View.GONE);
            thirdTextS.setVisibility(View.GONE);
            fourthTextS.setVisibility(View.GONE);
            fifthTextS.setVisibility(View.GONE);
            profilePic.setClickable(true);
            if (statementsData != null){
                for (int x = 0; x < statementsData.size();x++){
                    if (statementsData.get(x).equals("null")){
                        statementsData.set(x,"");
                    }
                }
                firstEditS.setText(statementsData.get(0));
                secondEditS.setText(statementsData.get(1));
                thirdEditS.setText(statementsData.get(2));
                fourthEditS.setText(statementsData.get(3));
                fifthEditS.setText(statementsData.get(4));

                firstEval.setText("23yes/20no");
                secondEval.setText("64yes/12no");
                thirdEval.setText("98yes/22no");
                fourthEval.setText("21yes/96no");
                fifthEval.setText("65yes/20no");

                firstEval.setBackgroundColor(Color.parseColor("#e5e500"));
                //secondEval.setBackgroundColor();
                //thirdEval.setBackgroundColor();
                fourthEval.setBackgroundColor(Color.parseColor("#e50000"));
                //firstEval.setBackgroundColor();
            }
        } else if (recoverIntentSelection != null){
            //grab username and other info about candidate from listview through bundle
            setTitle(username);
            name.setText(username);
            try {
                Log.e("log_check","trying getPicPath");
                imageData = new getPicPath().execute(username).get();
            } catch (Exception e){
                Log.e("log_error","failed to retrieve imageData");
            }
            if (!imageData.get(0).equals("null")) {
                Log.e("log_check", imageData.get(0));
                imagePath = "http://ec2-52-89-196-34.us-west-2.compute.amazonaws.com/Weeple/ProfilePics/" + imageData.get(0);
                rotation = Integer.valueOf(imageData.get(1));
                try {
                    bm = new setProfilePic().execute(imagePath).get();
                } catch (Exception e) {
                    Log.e("log_error", "failed to get bitmap");
                }
                profilePic.setImageBitmap(bm);
                profilePic.setRotation((float) rotation);
            }
            firstEditS.setVisibility(View.INVISIBLE);
            secondEditS.setVisibility(View.INVISIBLE);
            thirdEditS.setVisibility(View.INVISIBLE);
            fourthEditS.setVisibility(View.INVISIBLE);
            fifthEditS.setVisibility(View.INVISIBLE);
            submit.setVisibility(View.INVISIBLE);
            profilePic.setClickable(false);
            if (statementsData != null){
                for (int x = 0; x < statementsData.size();x++){
                    if (statementsData.get(x).equals("")){
                        statementsData.set(x,"No statement");
                    }
                }
                firstTextS.setText(statementsData.get(0));
                secondTextS.setText(statementsData.get(1));
                thirdTextS.setText(statementsData.get(2));
                fourthTextS.setText(statementsData.get(3));
                fifthTextS.setText(statementsData.get(4));

                firstEval.setText("23yes/20no");
                secondEval.setText("64yes/12no");
                thirdEval.setText("98yes/22no");
                fourthEval.setText("21yes/96no");
                fifthEval.setText("65yes/20no");

                firstEval.setBackgroundColor(Color.parseColor("#e5e500"));
                //secondEval.setBackgroundColor();
                //thirdEval.setBackgroundColor();
                fourthEval.setBackgroundColor(Color.parseColor("#e50000"));
                //firstEval.setBackgroundColor();
            }
        }

        /*Matrix matrix = new Matrix();
        profilePic.setScaleType(ImageView.ScaleType.MATRIX);   //required
        matrix.postRotate((float) 90.0, (float) 100.0, (float) 100.0);
        profilePic.setImageMatrix(matrix);*/

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stringFirstEditS = firstEditS.getText().toString();
                stringSecondEditS = secondEditS.getText().toString();
                stringThirdEditS = thirdEditS.getText().toString();
                stringFourthEditS = fourthEditS.getText().toString();
                stringFifthEditS = fifthEditS.getText().toString();
                try {
                    new sendStatements().execute(stringFirstEditS, stringSecondEditS, stringThirdEditS, stringFourthEditS, stringFifthEditS, username).get();
                } catch(Exception e){
                    Log.e("log_error","failed to send statements");
                }
            }
        });

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopUp();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_politician, menu);
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

        if (id == R.id.logout) {
            File authData = new File(getFilesDir(), "Weeple_Authentification_Data.txt");
            Boolean infoDeleted = authData.delete();
            logOutIntent = new Intent(this,LoginActivity.class);
            logOutBooleanBundle = new Bundle();
            logOutBooleanBundle.putBoolean("infoDeleted", infoDeleted);
            logOutIntent.putExtras(logOutBooleanBundle);
            startActivity(logOutIntent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showPopUp(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String message = "Upload new profile image?";
        String positive = "Upload";
        String negative = "Cancel";
        builder.setMessage(message)
                .setPositiveButton(positive,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                                photoPickerIntent.setType("image/*");
                                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
                                d.dismiss();
                            }
                        })
                .setNegativeButton(negative,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                d.cancel();
                            }
                        });
        builder.create().show();
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case SELECT_PHOTO:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(
                            selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();


                    Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);
                    int rotation = getCameraPhotoOrientation(getApplicationContext(), selectedImage, filePath);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    yourSelectedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] imageBytes = baos.toByteArray();
                    String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                    String username = ((MyApplication) getApplicationContext()).getUsername();
                    Log.i("username", username);
                    //imageBytes = Base64.decode(encodedImage, Base64.DEFAULT);
                    //Bitmap bm = BitmapFactory.decodeByteArray(imageBytes,0,imageBytes.length);
                    //profilePic.setImageBitmap(bm);
                    //profilePic.setRotation((float) rotation);
                    //String decoded = "hey";
                    /*try{
                        decoded = new String(imageBytes, "UTF-8");
                    }catch(Exception e){
                        Log.e("log_error", "failed decoding");
                    }*/
                    try {
                        new uploadPic().execute(filePath).get();
                        new setPicInDb().execute(filePath,username,Integer.toString(rotation));
                        //Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG);
                        //Log.i("MYSQL ERROR", response);
                    } catch (Exception e){
                        Log.e("log_error","couldn't exec setProfilePic");
                    }
                    try {
                        Log.e("log_check","trying getPicPath");
                        imageData = new getPicPath().execute(username).get();
                    } catch (Exception e){
                        Log.e("log_error","failed to retrieve imageData after upload");
                    }
                    if (!imageData.get(0).equals("null")) {
                        Log.e("log_check", imageData.get(0));
                        imagePath = "http://ec2-52-89-196-34.us-west-2.compute.amazonaws.com/Weeple/ProfilePics/" + imageData.get(0);
                        rotation = Integer.valueOf(imageData.get(1));
                        try {
                            bm = new setProfilePic().execute(imagePath).get();
                        } catch (Exception e) {
                            Log.e("log_error", "failed to get bitmap after upload");
                        }
                        profilePic.setImageBitmap(bm);
                        profilePic.setRotation((float) rotation);
                    }
                }
        }
    }

    public int getCameraPhotoOrientation(Context context, Uri imageUri, String imagePath){
        int rotate = 0;
        try {
            context.getContentResolver().notifyChange(imageUri, null);
            File imageFile = new File(imagePath);

            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }

            Log.i("RotateImage", "Exif orientation: " + orientation);
            Log.i("RotateImage", "Rotate value: " + rotate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

}

class uploadPic extends AsyncTask<String, Void, Void>{

    @Override
    protected Void doInBackground(String... strings){
        /*String answer;
        InputStream is = null;
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();*/
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        DataInputStream inputStream = null;
        //Log.e("oldpath",strings[0]);
        //String username = strings[1];
        //String[] arr = strings[0].split("/");
        //arr[arr.length - 1] = username;
        String pathToOurFile = strings[0];
        /*for (int x = 0; x < arr.length; x++){
            pathToOurFile = pathToOurFile + arr[x];
            if (x < arr.length - 1){
                pathToOurFile = pathToOurFile + "/";
            }
        }*/
        //pathToOurFile = pathToOurFile + ".jpg";
        Log.e("newpath",pathToOurFile);
        String urlServer = "http://ec2-52-89-196-34.us-west-2.compute.amazonaws.com/Weeple/handle_pic_upload.php";
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary =  "*****";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1*1024*1024;

        try
        {
            FileInputStream fileInputStream = new FileInputStream(new File(pathToOurFile) );

            URL url = new URL(urlServer);
            connection = (HttpURLConnection) url.openConnection();

            // Allow Inputs &amp; Outputs.
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            // Set HTTP method to POST.
            connection.setRequestMethod("POST");

            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);

            outputStream = new DataOutputStream( connection.getOutputStream() );
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + pathToOurFile +"\"" + lineEnd);
            outputStream.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // Read file
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0)
            {
                outputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // Responses from the server (code and message)
            int serverResponseCode = connection.getResponseCode();
            String serverResponseMessage = connection.getResponseMessage();

            fileInputStream.close();
            outputStream.flush();
            outputStream.close();
        }
        catch (Exception ex)
        {
            //Exception handling
        }
        return null;

        /*try {
            URL url = new URL("http://ec2-52-89-196-34.us-west-2.compute.amazonaws.com/Weeple/add_profile_pic.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            OutputStream out = new BufferedOutputStream(conn.getOutputStream());
            //Log.i("encoded image","rotation=" + strings[1] + "&username=" + strings[2] + "&image=" + strings[0]);
            Log.i("lengthtotal",Integer.toString(strings[0].length()));
            Log.i("rotation",strings[1]);
            String output = "image=" + strings[0] + "&rotation=" + strings[1] + "&username=" + strings[2];
            out.write(output.getBytes());
            out.flush();
            out.close();
            is = conn.getInputStream();
            int responseCode = conn.getResponseCode();
            Log.i("log_tag", "POST Response Code :: " + responseCode);

        } catch (Exception e) {
            Log.e("log_error","failed to connect setProfilePic " + e);
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
        } catch (Exception e) {
            Log.e("log_tag3", "ERROR IN PARSING RESPONSE: " + e);
        }

        answer = sb.toString();
        return answer;*/
    }
}

class setPicInDb extends AsyncTask<String, Void, Void>{

    @Override
    protected Void doInBackground(String... strings){
        InputStream is = null;

        try{

            URL url = new URL("http://ec2-52-89-196-34.us-west-2.compute.amazonaws.com/Weeple/add_profile_pic.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            OutputStream out = new BufferedOutputStream(conn.getOutputStream());
            String[] arr = strings[0].split("/");
            String message = "image=" + arr[arr.length-1] + "&username=" + strings[1] + "&rotation=" + strings[2];
            out.write(message.getBytes());
            out.flush();
            out.close();

            is = conn.getInputStream();
            int responseCode = conn.getResponseCode();
            Log.i("log_tag", "POST Response Code :: " + responseCode);

        } catch(Exception e){

        }
        return null;
    }
}

class getPicPath extends AsyncTask<String, Void, ArrayList<String>>{

    @Override
    protected ArrayList<String> doInBackground(String... strings){
        String path = "";
        String rotation = "";
        String result = "";
        ArrayList<String> data = new ArrayList<>();
        InputStream input = null;

        try{

            URL url = new URL("http://ec2-52-89-196-34.us-west-2.compute.amazonaws.com/Weeple/retrieve_profile_picture.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            OutputStream out = new BufferedOutputStream(conn.getOutputStream());
            String message = "username=" + strings[0];
            out.write(message.getBytes());
            out.flush();
            out.close();

            input = conn.getInputStream();
            int responseCode = conn.getResponseCode();
            Log.i("log_tag", "POST Response Code :: " + responseCode);

        } catch(Exception e){

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
                path = new String(json.getString("image"));
                rotation = new String(json.getString("rotation"));
            }
        }
        catch(Exception e){
            Log.e("log_tag3", "Error Parsing Data " + e.toString());
        }
        data.add(path);
        data.add(rotation);
        Log.e("log_check", data.get(0));
        return data;
    }
}

class setProfilePic extends AsyncTask<String, Void, Bitmap>{

    @Override
    protected Bitmap doInBackground(String... strings){
        String imagePath = strings[0];
        Bitmap bitmap = null;

        try {
            URL url = new URL(imagePath);
            InputStream inputStream = url.openStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch(Exception e){
            Log.e("log_error","failed to retrieve image: " + e);
        }
        return bitmap;
    }
}

class sendStatements extends AsyncTask<String, Void, Void>{

    @Override
    protected Void doInBackground(String... strings){
        InputStream is = null;

        try{
            URL url = new URL("http://ec2-52-89-196-34.us-west-2.compute.amazonaws.com/Weeple/add_statements.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            OutputStream out = new BufferedOutputStream(conn.getOutputStream());
            String message = "statement1=" + strings[0] + "&statement2=" + strings[1] + "&statement3=" + strings[2] + "&statement4=" + strings[3] + "&statement5=" + strings[4] + "&username=" + strings[5];
            out.write(message.getBytes());
            out.flush();
            out.close();

            int responseCode = conn.getResponseCode();
            Log.i("responsecode",Integer.toString(responseCode));
            is = conn.getInputStream();

        } catch (Exception e){
            Log.e("log_error","failed to send statements");
        }
        return null;
    }
}

class getStatementsData extends AsyncTask<String, Void, ArrayList<String>>{

    @Override
    protected ArrayList<String> doInBackground(String... strings){
        String statement1 = "";
        String statement2 = "";
        String statement3 = "";
        String statement4 = "";
        String statement5 = "";
        ArrayList<String> statementsData = new ArrayList<>();
        InputStream input = null;
        String result = "";

        try {
            URL url = new URL("http://ec2-52-89-196-34.us-west-2.compute.amazonaws.com/Weeple/retrieve_statements_data.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            String message = "username=" + strings[0];
            OutputStream out = new BufferedOutputStream(conn.getOutputStream());
            out.write(message.getBytes());
            out.flush();
            out.close();

            input = conn.getInputStream();
            int responseCode = conn.getResponseCode();

        } catch(Exception e){
            Log.e("log_error","failed to retrieve statements data");
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
                statement1 = new String(json.getString("statement1"));
                statement2 = new String(json.getString("statement2"));
                statement3 = new String(json.getString("statement3"));
                statement4 = new String(json.getString("statement4"));
                statement5 = new String(json.getString("statement5"));
            }
        }
        catch(Exception e){
            Log.e("log_tag3", "Error Parsing Data " + e.toString());
        }

        statementsData.add(statement1);
        statementsData.add(statement2);
        statementsData.add(statement3);
        statementsData.add(statement4);
        statementsData.add(statement5);
        return statementsData;
    }
}