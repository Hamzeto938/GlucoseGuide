package io.github.coffeegerm.materiallogbook.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.coffeegerm.materiallogbook.R;
import io.github.coffeegerm.materiallogbook.model.EntryItem;
import io.realm.Realm;

/**
 * Created by David Yarzebinski on 6/25/2017.
 * <p>
 * Activity for changing and showing chosen settings of Material Logbook.
 */

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";
    private static final String HYPERGLYCEMIC_INDEX = "hyperglycemicIndex";
    private static final String HYPOGLYCEMIC_INDEX = "hypoglycemicIndex";
    @BindView(R.id.btn_delete_all)
    Button deleteAllEntries;
    @BindView(R.id.hyperglycemic_edit_text)
    EditText hyperglycemicEditText;
    @BindView(R.id.hypoglycemic_edit_text)
    EditText hypoglycemicEditText;
    @BindView(R.id.toggle_dark_mode)
    Switch toggleDarkMode;
    @BindView(R.id.setting_toolbar)
    Toolbar settingsToolbar;
    @BindView(R.id.donate_button)
    Button donate;
    private Realm realm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (MainActivity.sharedPreferences.getBoolean("pref_dark_mode", false)) {
            setTheme(R.style.AppTheme_Dark);
        }
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        initView();
    }

    public void initView() {
        realm = Realm.getDefaultInstance();
        setupToolbar();
        checkRangeStatus();
        setHints();

        toggleDarkMode.setChecked(MainActivity.sharedPreferences.getBoolean("pref_dark_mode", false));

        toggleDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            MainActivity.sharedPreferences.edit().putBoolean("pref_dark_mode", isChecked).apply();
            recreate();
        });

        hypoglycemicEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.i(TAG, "afterTextChanged: " + s.toString());
                if (!s.toString().equals(""))
                    MainActivity.sharedPreferences.edit()
                            .putInt(HYPOGLYCEMIC_INDEX, Integer.parseInt(s.toString())).apply();
            }
        });

        hyperglycemicEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.i(TAG, "afterTextChanged: " + s.toString());
                if (!s.toString().equals("")) MainActivity.sharedPreferences.edit()
                        .putInt(HYPERGLYCEMIC_INDEX, Integer.parseInt(s.toString())).apply();
            }
        });

        deleteAllEntries.setOnClickListener(v -> {
            final AlertDialog.Builder builder;

            // Sets theme based on VERSION_CODE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                builder = new AlertDialog.Builder(SettingsActivity.this, android.R.style.Theme_Material_Dialog_NoActionBar);
            else builder = new AlertDialog.Builder(SettingsActivity.this);

            builder.setTitle("Delete all entries")
                    .setMessage("Are you sure you want to delete all entries?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        // continue with delete
                        realm.executeTransaction(realm1 -> realm1.delete(EntryItem.class));
                    })
                    .setNegativeButton(android.R.string.no, (dialog, which) -> {
                        // do nothing
                        dialog.dismiss();
                    })
                    .setIcon(R.drawable.ic_trash)
                    .show();
        });

        donate.setOnClickListener(v -> {
            Log.i(TAG, "donate button pressed");
            String paypal = "paypal.me/DavidYarzebinski";
            Intent donate = new Intent(Intent.ACTION_VIEW, Uri.parse(paypal));
            if (donate.resolveActivity(getPackageManager()) != null) {
                startActivity(donate);
            }
        });
    }

    public void checkRangeStatus() {
        int hyperglycemicIndex = MainActivity.sharedPreferences.getInt("hyperglycemicIndex", 0);
        int hypoglycemicIndex = MainActivity.sharedPreferences.getInt("hypoglycemicIndex", 0);
        if (hyperglycemicIndex == 0 && hypoglycemicIndex == 0) {
            MainActivity.sharedPreferences.edit().putInt("hypoglycemicIndex", 80).apply();
            MainActivity.sharedPreferences.edit().putInt("hyperglycemicIndex", 140).apply();
        }
    }

    public void setHints() {
        String hyperString = String.valueOf(MainActivity.sharedPreferences.getInt("hyperglycemicIndex", 0));
        hyperglycemicEditText.setHint(hyperString);

        String hypoString = String.valueOf(MainActivity.sharedPreferences.getInt("hypoglycemicIndex", 0));
        hypoglycemicEditText.setHint(hypoString);
    }

    public void setupToolbar() {
        setSupportActionBar(settingsToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onDestroy() {
        realm.close();
        super.onDestroy();
    }
}