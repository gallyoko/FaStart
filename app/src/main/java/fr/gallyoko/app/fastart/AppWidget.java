package fr.gallyoko.app.fastart;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * Implementation of App Widget functionality.
 */
public class AppWidget extends AppWidgetProvider {

    private final static String EXTRA_ACTIVE = "extraActive";
    private final static String EXTRA_FIRST_LAUNCH = "extraFirstLaunch";
    private boolean active = false;
    private boolean firstLaunch = true;
    private String textButton = "ON";
    private String textToggle = "ALLUME";
    private Integer backgroundButton = Color.GREEN;
    private Integer backgroundToggle = Color.BLUE;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        final int length = appWidgetIds.length;
        for (int i = 0 ; i < length ; i++) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget_button);
            views.setTextViewText(R.id.light1, textButton);
            views.setInt(R.id.light1, "setBackgroundColor", backgroundButton);
            views.setOnClickPendingIntent(R.id.light1, buildButtonPendingIntent(context, appWidgetIds[i],
                    this.active, this.firstLaunch));
            pushWidgetUpdate(context, views, appWidgetIds[i]);
        }
    }

    public static void pushWidgetUpdate(Context context, RemoteViews remoteViews, int appWidgetId) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(appWidgetId, remoteViews);
    }

    public static PendingIntent buildButtonPendingIntent(Context context, int appWidgetId,
                                                         boolean active, boolean firstLaunch) {
        Intent intent = new Intent();
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        intent.putExtra(EXTRA_ACTIVE, active);
        intent.putExtra(EXTRA_FIRST_LAUNCH, firstLaunch);

        return PendingIntent.getBroadcast(context, appWidgetId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            Integer widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            String value = prefs.getString("type_" + widgetId, null);
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
                        if (value.equals("button")) {
                            textButton = "ON";
                            backgroundButton = Color.GREEN;
                        } else if (value.equals("toggle")) {
                            textToggle = "ALLUMER";
                            backgroundToggle = Color.BLUE;
                        }
                    } else {
                        active = true;
                        textButton = "OFF";
                        backgroundButton = Color.RED;
                        if (value.equals("button")) {
                            textButton = "OFF";
                            backgroundButton = Color.RED;
                        } else if (value.equals("toggle")) {
                            textToggle = "ETEINDRE";
                            backgroundToggle = Color.YELLOW;
                        }
                    }
                    FetchTask fetchTask = new FetchTask();
                    fetchTask.context = context;
                    fetchTask.messageSuccess = messageSuccess;
                    fetchTask.execute(url);
                }
                firstLaunch = false;
                this.updateWidgetTypeListener(context, widgetId, active, firstLaunch);
            }
        }

        super.onReceive(context, intent);
    }

    private void updateWidgetTypeListener(Context context, int appWidgetId, boolean active, boolean firstLaunch) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget_button);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String value = prefs.getString("type_" + appWidgetId, null);
        if (value != null) {
            if (value.equals("button")) {
                views.setTextViewText(R.id.light1, textButton);
                views.setInt(R.id.light1, "setBackgroundColor", backgroundButton);
            } else if (value.equals("toggle")) {
                views.setTextViewText(R.id.light1, textToggle);
                views.setInt(R.id.light1, "setBackgroundColor", backgroundToggle);
            }
            views.setOnClickPendingIntent(R.id.light1, AppWidget.buildButtonPendingIntent(context, appWidgetId, active, firstLaunch));
            AppWidget.pushWidgetUpdate(context, views, appWidgetId);
        }
    }

    private boolean isConnected(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}


