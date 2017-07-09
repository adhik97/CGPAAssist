package com.imadhik.cgpaassist;

import android.util.Base64;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import android.util.Base64;
import android.util.Log;

/**
 * Created by Adhik on 01-05-2017.
 */

class requestClass {
    private String pageUrl;
    private String username;
    private String password;

    public void setPageUrl(String url) {
        pageUrl = url;
    }

    public void setUsername(String uname) {
        username = uname;
    }

    public void setPassword(String pass) {
        password = pass;
    }

    public requestClass(String url, String uname, String pas) {
        pageUrl = url;
        username = uname;
        password = pas;
    }

    public String getJsonFromGET() {

        try {

            URL url = new URL(pageUrl);
            URLConnection uc = url.openConnection();

            String userpass = username + ":" + password;
            String basicAuth = "Basic " + new String(Base64.encode(userpass.getBytes(), 0));

            uc.setRequestProperty("Authorization", basicAuth);
            //uc.connect();
            InputStream in = uc.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder out = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                out.append(line);
            }
            reader.close();
            Log.i("TAG", out.toString());
            return out.toString();

        } catch (NullPointerException e) {
            Log.i("TAG", "aa " + e.getMessage());
            return "error";
        } catch (MalformedURLException e) {
            Log.i("TAG", "mal " + e.getMessage());
            return "error";
        } catch (IOException e) {

            Log.i("TAG", "mal " + e.getMessage());
            return "error";


        }
    }
}
