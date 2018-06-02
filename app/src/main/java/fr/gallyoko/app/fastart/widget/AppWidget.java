package fr.gallyoko.app.fastart.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.RemoteViews;
import android.widget.Toast;

import org.json.JSONObject;

import fr.gallyoko.app.fastart.bdd.entity.ApiEntity;
import fr.gallyoko.app.fastart.service.Api;
import fr.gallyoko.app.fastart.service.ApiResponse;
import fr.gallyoko.app.fastart.R;
import fr.gallyoko.app.fastart.bdd.entity.WidgetEntity;
import fr.gallyoko.app.fastart.bdd.repository.WidgetRepository;

/**
 * Implementation of App Widget functionality.
 */
public class AppWidget extends AppWidgetProvider {

    private final static String EXTRA_ACTIVE = "extraActive";
    private final static String EXTRA_FIRST_LAUNCH = "extraFirstLaunch";
    private boolean active = false;
    private boolean firstLaunch = true;
    private String textButton = "Titre";
    private Integer backgroundButton = R.mipmap.button_on_1x1;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        final int length = appWidgetIds.length;
        for (int i = 0 ; i < length ; i++) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget_button);
            views.setTextViewText(R.id.titleWidget, textButton);
            views.setImageViewResource(R.id.element, backgroundButton);
            views.setOnClickPendingIntent(R.id.element, buildButtonPendingIntent(context, appWidgetIds[i],
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
            Boolean extraFirstLaunch = intent.getBooleanExtra(EXTRA_FIRST_LAUNCH, true);
            final Boolean extraActive = intent.getBooleanExtra(EXTRA_ACTIVE, false);

            if (!this.isConnected(context)) {
                Toast.makeText(context, "Aucune connexion à internet.", Toast.LENGTH_SHORT).show();
            } else {
                if (!extraFirstLaunch) {
                    WidgetRepository widgetRepository = new WidgetRepository(context);
                    widgetRepository.open();
                    WidgetEntity widget = widgetRepository.getByAppWidgetId(widgetId);
                    widgetRepository.close();
                    textButton = widget.getTitle();
                    if (extraActive) {
                        active = false;
                        backgroundButton = widget.getType().getImageOn();
                    } else {
                        active = true;
                        backgroundButton = widget.getType().getImageOff();
                    }
                    final Context apiContext = context;
                    for (final ApiEntity api: widget.getApis()) {
                        String url = "";
                        if (extraActive) {
                            url = api.getUrl()+api.getPutOff();
                        } else {
                            url = api.getUrl()+api.getPutOn();
                        }
                        Api apiService = new Api(apiContext, url, "GET", "", new ApiResponse(){
                            @Override
                            public void getResponse(JSONObject output) {
                                if (extraActive) {
                                    Toast.makeText(apiContext, api.getPutOffMsg(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(apiContext, api.getPutOnMsg(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        apiService.exec();
                    }
                }
                firstLaunch = false;
                this.updateWidgetTypeListener(context, widgetId, active);
            }
        }

        super.onReceive(context, intent);
    }

    private void updateWidgetTypeListener(Context context, int appWidgetId, boolean active) {
        WidgetRepository widgetRepository = new WidgetRepository(context);
        widgetRepository.open();
        WidgetEntity widget = widgetRepository.getByAppWidgetId(appWidgetId);
        if (widget != null) {
            if (widget.getInit()==0) {
                textButton = widget.getTitle();
                backgroundButton = widget.getType().getImageOn();
            }
            widget.setInit(1);
            widgetRepository.update(widget.getId(), widget);
            widgetRepository.close();
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget_button);
            views.setTextViewText(R.id.titleWidget, textButton);
            views.setImageViewResource(R.id.element, backgroundButton);
            views.setOnClickPendingIntent(R.id.element, AppWidget.buildButtonPendingIntent(context, appWidgetId, active, firstLaunch));
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


