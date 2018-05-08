package fr.gallyoko.app.fastart;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.content.Intent;
import android.appwidget.AppWidgetManager;

public class DialogActivity extends Activity {
    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null)
        {
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID)
        {
            setResult(RESULT_CANCELED);
            finish();
        }

        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        displayDialog();
    }

    private void displayDialog() {
        final Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        AlertDialog.Builder builder = new AlertDialog.Builder(getDialogContext());
        builder.setTitle("Choose an animal");
        String[] animals = {"horse", "cow", "camel", "sheep", "goat"};
        builder.setItems(animals, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setResult(RESULT_OK, resultValue);
                switch (which) {
                    case 0:
                        dialog.dismiss();
                        finish();
                        break;
                        case 1:
                            dialog.dismiss();
                            finish();
                            break;
                            case 2:
                                dialog.dismiss();
                                finish();
                                break;
                                case 3:
                                    dialog.dismiss();
                                    finish();
                                    break;
                                    case 4:
                                        dialog.dismiss();
                                        finish();
                                        break;
                }
            }
        });

        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                setResult(RESULT_CANCELED, resultValue);
                dialog.dismiss();
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private Context getDialogContext() {
        final Context context;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            context = new ContextThemeWrapper(this, android.R.style.Theme_Holo_Light);
        } else {
            context = new ContextThemeWrapper(this, android.R.style.Theme_Dialog);
        }

        return context;
    }
}
