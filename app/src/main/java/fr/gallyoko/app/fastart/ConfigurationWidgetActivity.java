package fr.gallyoko.app.fastart;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

import android.appwidget.AppWidgetManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import fr.gallyoko.app.fastart.bdd.entity.WidgetEntity;
import fr.gallyoko.app.fastart.bdd.entity.WidgetTypeEntity;
import fr.gallyoko.app.fastart.bdd.entity.ApiEntity;
import fr.gallyoko.app.fastart.bdd.repository.WidgetRepository;
import fr.gallyoko.app.fastart.bdd.repository.WidgetTypeRepository;
import fr.gallyoko.app.fastart.bdd.repository.ApiRepository;

public class ConfigurationWidgetActivity extends Activity implements OnItemSelectedListener {

    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private int typePositionSelect = -1;
    private ArrayList<WidgetTypeEntity> widgetTypes = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            setResult(RESULT_CANCELED);
            finish();
        }

        this.updateConfig();

        setContentView(R.layout.activity_config_widget);

        Spinner dropdown = findViewById(R.id.type);

        WidgetTypeRepository widgetTypeRepository = new WidgetTypeRepository(this);
        widgetTypeRepository.open();
        widgetTypes = widgetTypeRepository.getAll();
        widgetTypeRepository.close();

        String[] items = new String[widgetTypes.size()];
        int index = 0;
        for (WidgetTypeEntity widgetType: widgetTypes) {
            items[index] = widgetType.getName();
            index ++;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(this);

        findViewById(R.id.buttonOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveWidget();
            }
        });

        findViewById(R.id.buttonCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        this.typePositionSelect = position;
        Toast.makeText(this, "position = "+position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void saveWidget() {
        final Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        WidgetRepository widgetRepository = new WidgetRepository(this);
        widgetRepository.open();
        if (widgetRepository.getByAppWidgetId(mAppWidgetId) == null) {
            WidgetTypeRepository widgetTypeRepository = new WidgetTypeRepository(this);
            widgetTypeRepository.open();
            WidgetTypeEntity widgetTypeEntity = widgetTypeRepository.getById(widgetTypes.get(this.typePositionSelect).getId());
            widgetTypeRepository.close();

            ApiRepository apiRepository = new ApiRepository(this);
            apiRepository.open();
            ApiEntity apiEntity = null;
            String titleWidget = "";
            if (widgetTypes.get(this.typePositionSelect).getName().equals("button")) {
                apiEntity = apiRepository.getById(1);
                titleWidget = "Lumière salon";
            } else if (widgetTypes.get(this.typePositionSelect).getName().equals("toggle")) {
                apiEntity = apiRepository.getById(2);
                titleWidget = "Lumière TV";
            }
            apiRepository.close();
            WidgetEntity widgetEntity = new WidgetEntity(mAppWidgetId,
                    titleWidget, widgetTypeEntity, apiEntity);
            widgetRepository.insert(widgetEntity);
        }
        widgetRepository.close();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("type_" + mAppWidgetId, widgetTypes.get(this.typePositionSelect).getName());
        editor.commit();
        setResult(RESULT_OK, resultValue);
        Intent refreshIntent = new Intent(this, AppWidget.class);
        refreshIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        refreshIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        sendBroadcast(refreshIntent);
        finish();
    }

    private void updateConfig() {
        WidgetTypeRepository widgetTypeRepository = new WidgetTypeRepository(this);
        widgetTypeRepository.open();
        if (widgetTypeRepository.getByName("button") == null) {
            WidgetTypeEntity widgetTypeEntity1 = new WidgetTypeEntity("button");
            widgetTypeRepository.insert(widgetTypeEntity1);
        }
        if (widgetTypeRepository.getByName("toggle") == null) {
            WidgetTypeEntity widgetTypeEntity2 = new WidgetTypeEntity("toggle");
            widgetTypeRepository.insert(widgetTypeEntity2);
        }
        widgetTypeRepository.close();

        ApiRepository apiRepository = new ApiRepository(this);
        apiRepository.open();
        if (apiRepository.getByName("lumière salon") == null) {
            ApiEntity apiEntity = new ApiEntity("lumière salon",
                    "Allume ou éteind l'halogène du salon",
                    "http://172.20.0.2:8091/api/light", "/put/on/1", "Lumière salon allumée.",
                    "/put/off/1", "Lumière salon éteinte.");
            apiRepository.insert(apiEntity);
        }
        if (apiRepository.getByName("lumière TV") == null) {
            ApiEntity apiEntity = new ApiEntity("lumière salon",
                    "Allume ou éteind la petite lumière TV du salon",
                    "http://172.20.0.2:8091/api/light", "/put/on/2", "Lumière TV allumée.",
                    "/put/off/2", "Lumière TV éteinte.");
            apiRepository.insert(apiEntity);
        }
        apiRepository.close();
    }
}