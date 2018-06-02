package fr.gallyoko.app.fastart.service;

import org.json.JSONObject;

public interface AsyncResponse {
    void processFinish(JSONObject output);
}