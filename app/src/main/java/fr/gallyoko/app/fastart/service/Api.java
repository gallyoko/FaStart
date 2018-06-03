package fr.gallyoko.app.fastart.service;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import fr.gallyoko.app.fastart.bdd.entity.ContentTypeEntity;

public class Api {
    public ApiResponse delegate = null;
    private Context context;
    private String url;
    private String method;
    private String json;
    private ArrayList<ContentTypeEntity> contentTypes = null;

    public Api(Context context, String url, String method, String json, ArrayList<ContentTypeEntity> contentTypes, ApiResponse delegate) {
        this.context = context;
        this.url = url;
        this.method = method;
        this.json = json;
        this.contentTypes = contentTypes;
        this.delegate = delegate;
    }

    public void exec() {
        FetchTask fetchTask = new FetchTask(new AsyncResponse() {
            @Override
            public void processFinish(JSONObject output) {
                delegate.getResponse(output);
            }
        });
        fetchTask.context = this.context;
        JSONObject contentsType = new JSONObject();
        if (this.contentTypes != null) {
            try {
                for (final ContentTypeEntity contentType: this.contentTypes) {
                    contentsType.put(contentType.getName(), contentType.getValue());
                }
            } catch (JSONException e) {
                //return null;
                contentsType = new JSONObject();
            } finally {
                //return null;
            }

        }
        fetchTask.execute(this.url, this.method, this.json, contentsType.toString());
    }
}
