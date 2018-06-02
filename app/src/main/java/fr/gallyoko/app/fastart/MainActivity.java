package fr.gallyoko.app.fastart;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import org.json.JSONObject;
import android.util.Log;
import fr.gallyoko.app.fastart.service.ApiResponse;
import fr.gallyoko.app.fastart.service.Api;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.buttonTest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Api apiService = new Api(MainActivity.this,
                        "http://172.20.0.2:8091/api/light/test",
                        "POST", "{'test': 'ok test'}",
                        new ApiResponse() {
                            @Override
                            public void getResponse(JSONObject output) {
                                Toast.makeText(MainActivity.this, "Résultat : " + output.toString(), Toast.LENGTH_SHORT).show();
                                Log.i("RESULT RETURN", output.toString());
                            }
                });
                apiService.exec();
            }
        });

    }
}