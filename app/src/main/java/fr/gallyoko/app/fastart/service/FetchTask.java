package fr.gallyoko.app.fastart.service;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FetchTask extends AsyncTask<String, Void, String> {

    public Context context;
    public String messageSuccess = "";

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        InputStream inputStream = null;
        HttpURLConnection conn = null;

        String stringUrl = strings[0];
        try {
            URL url = new URL(stringUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            int response = conn.getResponseCode();
            if (response != 200) {
                return null;
            }

            inputStream = conn.getInputStream();
            if (inputStream == null) {
                return null;
            }

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader reader = new BufferedReader(inputStreamReader);
            StringBuffer buffer = new StringBuffer();
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
                buffer.append("\n");
            }

            return new String(buffer);
        } catch (IOException e) {
            return null;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (s == null) {
            Log.i("Widget", "Erreur");
            Toast.makeText(context, "Erreur " + this.messageSuccess, Toast.LENGTH_SHORT).show();
        } else {
            Log.i("Widget", s);
            String message = this.messageSuccess;
            try {
                JSONObject jObject = new JSONObject(s);
                boolean aJsonBoolean = jObject.getBoolean("success");
                if (!aJsonBoolean) {
                    message = "Erreur lors de l'action";
                }
            } catch (Exception e) {
                message = "Erreur de parsing";
            } finally {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        }
    }
}