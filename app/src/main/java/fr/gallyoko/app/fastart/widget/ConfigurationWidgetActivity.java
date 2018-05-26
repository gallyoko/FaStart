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
    private int apiPositionSelect = -1;
    private int colorOnPositionSelect = -1;
    private int colorOffPositionSelect = -1;
    private ArrayList<WidgetTypeEntity> widgetTypes = null;
    private ArrayList<ApiEntity> apis = null;
    private ArrayList<ColorEntity> colorsOn = null;
    private ArrayList<ColorEntity> colorsOff = null;
    private EditText textOn = null;
    private EditText textOff = null;

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
        this.initViewColors();

        this.textOn = findViewById(R.id.textOn);
        this.textOff = findViewById(R.id.textOff);

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

        String[] items = new String[widgetTypes.size()];
        int index = 0;
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
        Spinner dropdownApi = findViewById(R.id.api);

        ApiRepository apiRepository = new ApiRepository(this);
        apiRepository.open();
        apis = apiRepository.getAll();
        apiRepository.close();

        String[] itemsApi = new String[apis.size()];
        int indexApi = 0;
        for (ApiEntity api: apis) {
            itemsApi[indexApi] = api.getName();
            indexApi ++;
        }

        ArrayAdapter<String> adapterApi = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, itemsApi);
        dropdownApi.setAdapter(adapterApi);

        dropdownApi.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0) {
                    apiPositionSelect = position;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                apiPositionSelect = -1;
            }
        });
    }

    private void initViewColors() {
        ColorRepository colorRepository = new ColorRepository(this);
        colorRepository.open();
        colorsOn = colorRepository.getAll();
        colorsOff = colorRepository.getAll();
        colorRepository.close();

        String[] itemsColorOn = new String[colorsOn.size()];
        String[] itemsColorOff = new String[colorsOff.size()];
        int indexColor = 0;
        for (ColorEntity color: colorsOn) {
            itemsColorOn[indexColor] = color.getName();
            itemsColorOff[indexColor] = color.getName();
            indexColor ++;
        }

        Spinner dropdownColorOn = findViewById(R.id.colorOn);

        ArrayAdapter<String> adapterColorOn = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, itemsColorOn);
        dropdownColorOn.setAdapter(adapterColorOn);

        dropdownColorOn.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0) {
                    colorOnPositionSelect = position;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                colorOnPositionSelect = -1;
            }
        });

        Spinner dropdownColorOff = findViewById(R.id.colorOff);

        ArrayAdapter<String> adapterColorOff = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, itemsColorOff);
        dropdownColorOff.setAdapter(adapterColorOff);

        dropdownColorOff.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0) {
                    colorOffPositionSelect = position;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                colorOffPositionSelect = -1;
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
                if (this.typePositionSelect < 0) {
                    throw new Exception("Veuillez sélectionner un type.");
                } else if (this.apiPositionSelect < 0) {
                    throw new Exception("Veuillez sélectionner une action.");
                } else if (this.colorOnPositionSelect < 0) {
                    throw new Exception("Veuillez sélectionner une couleur pour l'action ON.");
                } else if (this.colorOffPositionSelect < 0) {
                    throw new Exception("Veuillez sélectionner une couleur pour l'action OFF.");
                } else if (textOn.getText().toString().equals("")) {
                    throw new Exception("Veuillez saisir un texte pour l'action ON.");
                } else if (textOff.getText().toString().equals("")) {
                    throw new Exception("Veuillez saisir un texte pour l'action OFF.");
                }
                WidgetTypeRepository widgetTypeRepository = new WidgetTypeRepository(this);
                widgetTypeRepository.open();
                WidgetTypeEntity widgetTypeEntity = widgetTypeRepository.getById(widgetTypes.get(this.typePositionSelect).getId());
                widgetTypeRepository.close();

                ApiRepository apiRepository = new ApiRepository(this);
                apiRepository.open();
                String titleWidget = apis.get(this.apiPositionSelect).getName();
                ApiEntity apiEntity = apiRepository.getById(apis.get(this.apiPositionSelect).getId());
                apiRepository.close();

                ColorRepository colorRepository = new ColorRepository(this);
                colorRepository.open();
                ColorEntity colorEntityOn = colorRepository.getById(colorsOn.get(this.colorOnPositionSelect).getId());
                ColorEntity colorEntityOff = colorRepository.getById(colorsOff.get(this.colorOffPositionSelect).getId());
                colorRepository.close();

                WidgetEntity widgetEntity = new WidgetEntity(mAppWidgetId,
                        titleWidget, textOn.getText().toString(), colorEntityOn,
                        textOff.getText().toString(), colorEntityOff, widgetTypeEntity, apiEntity, 0);
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
            ApiEntity apiEntity = new ApiEntity("lumière TV",
                    "Allume ou éteind la petite lumière TV du salon",
                    "http://172.20.0.2:8091/api/light", "/put/on/2", "Lumière TV allumée.",
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