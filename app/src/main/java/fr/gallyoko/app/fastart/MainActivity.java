package fr.gallyoko.app.fastart;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.util.Log;
import android.view.View.OnClickListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout linearLayout;
    private Button requestButton;
    private Button requestButton2;
    private TextView resultsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeView();
        setContentView(linearLayout);
        Toast.makeText(this, "Requête en cours d'exécution", Toast.LENGTH_SHORT).show();
    }

    // Le layout est généré programmatiquement pour simplifier l'article
    private void makeView() {
        requestButton = new Button(this);
        requestButton.setText("Alumer la lumière");
        requestButton.setOnClickListener(onClickOn);
        requestButton2 = new Button(this);
        requestButton2.setText("Eteindre la lumière");
        requestButton2.setOnClickListener(onClickOff);

        resultsTextView = new TextView(this);

        linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(requestButton);
        linearLayout.addView(requestButton2);
        linearLayout.addView(resultsTextView);
    }

    @Override
    public void onClick(View v) {
        /* Réagir au clic */
        Log.i("Widget", "yes c'est clic !");
    }

    private OnClickListener onClickOn = new OnClickListener() {
        public void onClick(View v) {
            if (!isConnected()) {
                Toast.makeText(v.getContext(), "Aucune connexion à internet.", Toast.LENGTH_SHORT).show();
                return;
            }
            new FetchTask().execute("http://172.20.0.2:8091/api/light/put/on/2");
        }
    };

    private OnClickListener onClickOff = new OnClickListener() {
        public void onClick(View v) {
            if (!isConnected()) {
                Toast.makeText(v.getContext(), "Aucune connexion à internet.", Toast.LENGTH_SHORT).show();
                return;
            }
            new FetchTask().execute("http://172.20.0.2:8091/api/light/put/off/2");
        }
    };

    private boolean isConnected() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}