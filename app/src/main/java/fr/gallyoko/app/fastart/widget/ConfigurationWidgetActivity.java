package fr.gallyoko.app.fastart.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import fr.gallyoko.app.fastart.R;
import fr.gallyoko.app.fastart.bdd.entity.ApiEntity;
import fr.gallyoko.app.fastart.bdd.entity.ColorEntity;
import fr.gallyoko.app.fastart.bdd.entity.WidgetEntity;
import fr.gallyoko.app.fastart.bdd.entity.WidgetTypeEntity;
import fr.gallyoko.app.fastart.bdd.repository.ApiRepository;
import fr.gallyoko.app.fastart.bdd.repository.ColorRepository;
import fr.gallyoko.app.fastart.bdd.repository.WidgetRepository;
import fr.gallyoko.app.fastart.bdd.repository.WidgetTypeRepository;

public class ConfigurationWidgetActivity extends Activity {

    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private int typePositionSelect = -1;
    private int api1PositionSelect = -1;
    private int api2PositionSelect = -1;
    private ArrayList<WidgetTypeEntity> widgetTypes = null;
    private ArrayList<ApiEntity> apis = null;
    private EditText title = null;

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

        this.initView();
    }

    private void initView() {
        setContentView(R.layout.activity_config_widget);

        this.initViewType();
        this.initViewApi();

        this.title = findViewById(R.id.titleWidget);

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

    private void initViewType() {
        Spinner dropdown = findViewById(R.id.type);

        WidgetTypeRepository widgetTypeRepository = new WidgetTypeRepository(this);
        widgetTypeRepository.open();
        widgetTypes = widgetTypeRepository.getAll();
        widgetTypeRepository.close();

        String[] items = new String[widgetTypes.size()+1];
        items[0] = "Select type ...";
        int index = 1;
        for (WidgetTypeEntity widgetType: widgetTypes) {
            items[index] = widgetType.getName();
            index ++;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0) {
                    typePositionSelect = position;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                typePositionSelect = -1;

            }
        });
    }

    private void initViewApi() {
        Spinner dropdownApi1 = findViewById(R.id.api1);
        Spinner dropdownApi2 = findViewById(R.id.api2);

        ApiRepository apiRepository = new ApiRepository(this);
        apiRepository.open();
        apis = apiRepository.getAll();
        apiRepository.close();

        String[] itemsApi = new String[apis.size()+1];
        itemsApi[0] = "Select action ...";
        int indexApi = 1;
        for (ApiEntity api: apis) {
            itemsApi[indexApi] = api.getName();
            indexApi ++;
        }

        ArrayAdapter<String> adapterApi1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, itemsApi);
        dropdownApi1.setAdapter(adapterApi1);
        ArrayAdapter<String> adapterApi2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, itemsApi);
        dropdownApi2.setAdapter(adapterApi2);

        dropdownApi1.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0) {
                    api1PositionSelect = position;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                api1PositionSelect = -1;
            }
        });

        dropdownApi2.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0) {
                    api2PositionSelect = position;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                api2PositionSelect = -1;
            }
        });
    }

    private void saveWidget() {
        final Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        WidgetRepository widgetRepository = new WidgetRepository(this);
        widgetRepository.open();
        if (widgetRepository.getByAppWidgetId(mAppWidgetId) == null) {
            String message = "";
            try {
                if (this.typePositionSelect <= 0) {
                    throw new Exception("Veuillez sélectionner un type.");
                } else if (this.api1PositionSelect <= 0 && this.api2PositionSelect <= 0) {
                    throw new Exception("Veuillez sélectionner une action.");
                } else if (title.getText().toString().equals("")) {
                    throw new Exception("Veuillez saisir un titre.");
                }
                WidgetTypeRepository widgetTypeRepository = new WidgetTypeRepository(this);
                widgetTypeRepository.open();
                WidgetTypeEntity widgetTypeEntity = widgetTypeRepository.getById(widgetTypes.get(this.typePositionSelect-1).getId());
                widgetTypeRepository.close();

                ArrayList<ApiEntity> apisToSave = new ArrayList();
                ApiRepository apiRepository = new ApiRepository(this);
                apiRepository.open();
                if (this.api1PositionSelect > 0) {
                    ApiEntity apiEntity1 = apiRepository.getById(apis.get(this.api1PositionSelect-1).getId());
                    apisToSave.add(apiEntity1);
                }
                if (this.api2PositionSelect > 0) {
                    ApiEntity apiEntity2 = apiRepository.getById(apis.get(this.api2PositionSelect-1).getId());
                    apisToSave.add(apiEntity2);
                }
                apiRepository.close();

                WidgetEntity widgetEntity = new WidgetEntity(mAppWidgetId,
                        title.getText().toString(), widgetTypeEntity, apisToSave, 0);
                widgetRepository.insert(widgetEntity);
                widgetRepository.close();
            } catch (Exception ex) {
                message = ex.getMessage();
            } finally {
                if (!message.equals("")) {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                } else {
                    Intent refreshIntent = new Intent(this, AppWidget.class);
                    refreshIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                    refreshIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                    sendBroadcast(refreshIntent);
                    setResult(RESULT_OK, resultValue);
                    finish();
                }
            }
        }
    }

    private void updateConfig() {
        String urlDev = "http://172.20.0.2:8091";
        //String urlProd = "http://83.157.150.119:9394";

        WidgetTypeRepository widgetTypeRepository = new WidgetTypeRepository(this);
        widgetTypeRepository.open();
        if (widgetTypeRepository.getByName("Bouton") == null) {
            WidgetTypeEntity widgetTypeEntity1 = new WidgetTypeEntity("Bouton", R.mipmap.button_on, R.mipmap.button_off);
            widgetTypeRepository.insert(widgetTypeEntity1);
        }
        if (widgetTypeRepository.getByName("Switch") == null) {
            WidgetTypeEntity widgetTypeEntity2 = new WidgetTypeEntity("Switch", R.mipmap.switch_off_1x1, R.mipmap.switch_on_1x1);
            widgetTypeRepository.insert(widgetTypeEntity2);
        }
        if (widgetTypeRepository.getByName("Ampoule") == null) {
            WidgetTypeEntity widgetTypeEntity3 = new WidgetTypeEntity("Ampoule", R.mipmap.light_off_1x1, R.mipmap.light_on_1x1);
            widgetTypeRepository.insert(widgetTypeEntity3);
        }
        widgetTypeRepository.close();

        ApiRepository apiRepository = new ApiRepository(this);
        apiRepository.open();
        if (apiRepository.getByName("lumière salon") == null) {
            ApiEntity apiEntity = new ApiEntity("lumière salon",
                    "Allume ou éteind l'halogène du salon",
                    urlDev + "/api/light", "/put/on/1", "Lumière salon allumée.",
                    "/put/off/1", "Lumière salon éteinte.");
            apiRepository.insert(apiEntity);
        }
        if (apiRepository.getByName("lumière TV") == null) {
            ApiEntity apiEntity = new ApiEntity("lumière TV",
                    "Allume ou éteind la petite lumière TV du salon",
                    urlDev + "/api/light", "/put/on/2", "Lumière TV allumée.",
                    "/put/off/2", "Lumière TV éteinte.");
            apiRepository.insert(apiEntity);
        }
        apiRepository.close();

        ColorRepository colorRepository = new ColorRepository(this);
        colorRepository.open();
        if (colorRepository.getByName("red") == null) {
            ColorEntity colorEntity1 = new ColorEntity("red", Color.RED);
            colorRepository.insert(colorEntity1);
        }
        if (colorRepository.getByName("blue") == null) {
            ColorEntity colorEntity2 = new ColorEntity("blue", Color.BLUE);
            colorRepository.insert(colorEntity2);
        }
        if (colorRepository.getByName("yellow") == null) {
            ColorEntity colorEntity3 = new ColorEntity("yellow", Color.YELLOW);
            colorRepository.insert(colorEntity3);
        }
        if (colorRepository.getByName("green") == null) {
            ColorEntity colorEntity4 = new ColorEntity("green", Color.GREEN);
            colorRepository.insert(colorEntity4);
        }
        colorRepository.close();
    }
}