package fr.gallyoko.app.fastart.service;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;

public class FetchTask extends AsyncTask<String, Void, String> {

    public AsyncResponse delegate = null;
    public Context context;
    private String method = "GET";
    private JSONObject result = null;

    public FetchTask(AsyncResponse delegate){
        this.delegate = delegate;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        InputStream inputStream = null;
        HttpURLConnection conn = null;
        try {
            boolean isSetContentType = false;
            String stringUrl = strings[0];
            URL url = new URL(stringUrl);
            conn = (HttpURLConnection) url.openConnection();
            this.method = strings[1];
            if (!strings[3].equals("") && !strings[3].equals("null") && !strings[3].equals("{}")) {
                try {
                    JSONObject headers = new JSONObject(strings[3]);
                    conn.setRequestProperty("Content-Type", headers.getString("CONTENT_TYPE"));
                    conn.setRequestProperty("X-Fbx-App-Auth", headers.getString("FREEBOX_APP_AUTH"));
                    isSetContentType = true;
                } catch (JSONException e) {
                    return null;
                } finally {
                    //return null;
                }
            }
            if (!this.method.equals("GET")) {
                conn.setDoOutput(true);
                conn.setRequestMethod(this.method);
                if (!isSetContentType) {
                    conn.setRequestProperty("Content-Type", "application/json");
                }
                if (!strings[2].equals("")) {
                    conn.setDoInput(true);
                    try {
                        JSONObject postData = new JSONObject(strings[2]);
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
                        writer.write(postData.toString());
                        writer.flush();
                    } catch (JSONException e) {
                        return null;
                    } finally {
                        //return null;
                    }
                }
            }

            conn.connect();
            int response = conn.getResponseCode();
            if (response == 500) {
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
        JSONObject jObject = null;
        if (s != null) {
            try {
                jObject = new JSONObject(s);
            } catch (JSONException e) {
                jObject = null;
            } finally {
                delegate.processFinish(jObject);
            }
        }
    }
}
