package fr.gallyoko.app.fastart.service;

import android.content.Context;
import org.json.JSONObject;

public class Api {
    public ApiResponse delegate = null;
    private Context context;
    private String url;
    private String method;
    private String json;

    public Api(Context context, String url, String method, String json, ApiResponse delegate) {
        this.context = context;
        this.url = url;
        this.method = method;
        this.json = json;
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
        fetchTask.execute(this.url, this.method, this.json);
    }
}
