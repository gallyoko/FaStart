package fr.gallyoko.app.fastart;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.Toast;

public class ButtonWidget extends AppWidgetProvider {
    private final static String EXTRA_ACTIVE = "extraActive";
    private final static String EXTRA_FIRST_LAUNCH = "extraFirstLaunch";
    private boolean active = false;
    private boolean firstLaunch = true;
    private String textButton = "ON";
    private Integer backgroundButton = Color.GREEN;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        // Petite astuce : permet de garder la longueur du tableau sans accéder plusieurs fois à l'objet, d'où optimisation
        final int length = appWidgetIds.length;
        for (int i = 0 ; i < length ; i++) {
            // Gestion de la vue
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.button_widget);
            // et du bouton
            views.setTextViewText(R.id.light, textButton);
            views.setInt(R.id.light, "setBackgroundColor", backgroundButton);

            // gestion de l'action
            Intent testIntent = new Intent(context, ButtonWidget.class);
            // On veut que l'intent lance la mise à jour
            testIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

            // On n'oublie pas les identifiants
            testIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

            // On rajoute les éléments à envoyer à l'intent
            testIntent.putExtra(EXTRA_ACTIVE, this.active);
            testIntent.putExtra(EXTRA_FIRST_LAUNCH, this.firstLaunch);

            // Les données inutiles mais qu'il faut rajouter
            Uri data = Uri.withAppendedPath(Uri.parse("WIDGET://widget/id/"), String.valueOf(R.id.light));
            testIntent.setData(data);

            // On insère l'intent dans un PendingIntent
            PendingIntent testPending = PendingIntent.getBroadcast(context, 0, testIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            // Et on l'associe à l'activation du bouton
            views.setOnClickPendingIntent(R.id.light, testPending);

            // Et il faut mettre à jour toutes les vues
            appWidgetManager.updateAppWidget(appWidgetIds[i], views);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Boolean extraFirstLaunch = intent.getBooleanExtra(EXTRA_FIRST_LAUNCH, true);
        Boolean extraActive = intent.getBooleanExtra(EXTRA_ACTIVE, false);
        if (!this.isConnected(context)) {
            Toast.makeText(context, "Aucune connexion à internet.", Toast.LENGTH_SHORT).show();
        } else {
            if (!extraFirstLaunch) {
                String url = "http://172.20.0.2:8091/api/light/put/on/2";
                String messageSuccess = "Lumière allumée.";
                if (extraActive) {
                    url = "http://172.20.0.2:8091/api/light/put/off/2";
                    messageSuccess = "Lumière éteinte.";
                    active = false;
                    textButton = "ON";
                    backgroundButton = Color.GREEN;
                } else {
                    active = true;
                    textButton = "OFF";
                    backgroundButton = Color.RED;
                }
                FetchTask fetchTask = new FetchTask();
                fetchTask.context = context;
                fetchTask.messageSuccess = messageSuccess;
                fetchTask.execute(url);
            }
            firstLaunch = false;
        }

        // On revient au traitement naturel du Receiver, qui va lancer onUpdate s'il y a demande de mise à jour
        super.onReceive(context, intent);
    }

    private boolean isConnected(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

}

