package com.imadhik.cgpaassist;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ProgressDialog pd;
    SharedPreferences details;
    String regno;
    String password;
    int paddingLeft,paddingRight,paddingTop,paddingBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         details = getSharedPreferences("CGPAActivity", Context.MODE_PRIVATE);
        Boolean bool = details.getBoolean("loggedIn", false);

        if (bool) {
            Intent intent=new Intent(MainActivity.this,HomeActivity.class);
            startActivity(intent);
        }else{


            Button loginButton = (Button) findViewById(R.id.loginButton);
            final EditText regnoEditText = (EditText) findViewById(R.id.regnoEditText);
            final EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);

            paddingLeft = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
            paddingRight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics());
            paddingTop = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics());
            paddingBottom = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics());
            regnoEditText.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
            passwordEditText.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);



            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    regno = regnoEditText.getText().toString().trim();
                    password = passwordEditText.getText().toString();

                    Pattern p1 = Pattern.compile("[0-9]{2}[A-Z]{3}[0-9]{4}");

                    if ((!p1.matcher(regno).find()) || regno.length() != 9) {
                        regnoEditText.setBackgroundResource(R.drawable.login_edit_text_danger);
                        regnoEditText.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
                        Toast.makeText(MainActivity.this, "Invalid Reg No", Toast.LENGTH_SHORT).show();
                    } else if (password.trim().length() == 0) {
                        regnoEditText.setBackgroundResource(R.drawable.login_edit_text);
                        regnoEditText.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
                        passwordEditText.setBackgroundResource(R.drawable.login_edit_text_danger);
                        passwordEditText.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
                        Toast.makeText(MainActivity.this, "Password field is empty", Toast.LENGTH_SHORT).show();
                    } else {
                        passwordEditText.setBackgroundResource(R.drawable.login_edit_text);
                        passwordEditText.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);

                        requestClass ob = new requestClass("http://vtopapi.herokuapp.com/grades", regno, password);


                        new JsonTask().execute(ob);

                    }


                }
            });
        }
    }

    public void editTextClicked(View view){
        EditText regnoEditText = (EditText)findViewById(R.id.regnoEditText);
        regnoEditText.setBackgroundResource(R.drawable.login_edit_text);
        regnoEditText.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);

    }

    public void passwordClicked(View view){
        EditText passwordEditText = (EditText)findViewById(R.id.passwordEditText);
        passwordEditText.setBackgroundResource(R.drawable.login_edit_text);
        passwordEditText.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private class JsonTask extends AsyncTask<requestClass, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Please wait");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(requestClass... params) {



            String data= params[0].getJsonFromGET();
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd.isShowing()){
                pd.dismiss();
            }
                if(isNetworkAvailable()) {
                    try {

                        JSONObject ob = new JSONObject(result);
                        if (ob.getString("type").equalsIgnoreCase("error")) {
                            Toast.makeText(MainActivity.this, ob.getJSONObject("value").getString("status"), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            JSONObject object = ob.getJSONObject("value");
                            SharedPreferences.Editor editor = details.edit();
                            editor.putString("regno", regno);
                            editor.putString("password", password);
                            editor.putBoolean("loggedIn", true);
                            editor.putString("object", object.toString());
                            editor.commit();
                            Intent i = new Intent(MainActivity.this, HomeActivity.class);
                            startActivity(i);

                        }

                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                else
                    Toast.makeText(MainActivity.this,"No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }
}





