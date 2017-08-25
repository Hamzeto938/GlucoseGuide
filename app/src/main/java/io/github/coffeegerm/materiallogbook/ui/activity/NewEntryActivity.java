package io.github.coffeegerm.materiallogbook.ui.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.coffeegerm.materiallogbook.R;
import io.github.coffeegerm.materiallogbook.model.EntryItem;
import io.realm.Realm;

import static io.github.coffeegerm.materiallogbook.utils.Utilities.checkTimeString;

/**
 * Created by David Yarzebinski on 6/25/2017.
 * <p>
 * Activity for a new Entry into the Database
 */

public class NewEntryActivity extends AppCompatActivity {

    private static final String TAG = "NewEntryActivity";

    @BindView(R.id.cancelBtn)
    Button cancelBtn;
    @BindView(R.id.saveBtn)
    Button saveBtn;
    @BindView(R.id.new_entry_date)
    EditText newEntryDate;
    @BindView(R.id.new_entry_time)
    EditText newEntryTime;
    @BindView(R.id.new_entry_blood_glucose_level)
    EditText newEntryBloodGlucose;
    @BindView(R.id.new_entry_carbohydrates_amount)
    EditText newEntryCarbohydrates;
    @BindView(R.id.new_entry_insulin_units)
    EditText newEntryInsulin;
    @BindView(R.id.new_entry_date_time_label)
    TextView dateTimeLabel;
    @BindView(R.id.new_entry_glucose_label)
    TextView glucoseLabel;
    @BindView(R.id.new_entry_carbs_label)
    TextView carbsLabel;
    @BindView(R.id.new_entry_insulin_label)
    TextView insulinLabel;
    @BindView(R.id.breakfast_status)
    ImageButton breakfast;
    @BindView(R.id.lunch_status)
    ImageButton lunch;
    @BindView(R.id.dinner_status)
    ImageButton dinner;
    @BindView(R.id.sick_status)
    ImageButton sick;
    @BindView(R.id.exercise_status)
    ImageButton exercise;
    @BindView(R.id.sweets_status)
    ImageButton sweets;

    private Realm realm;
    private Calendar calendarForDb;
    private int status = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: NewEntryActivity started");
        if (MainActivity.sharedPreferences.getBoolean("pref_dark_mode", false))
            setTheme(R.style.AppTheme_Dark);
        setContentView(R.layout.activity_new_entry);
        ButterKnife.bind(this);
        realm = Realm.getDefaultInstance();
        setFonts();
        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) getSupportActionBar().setTitle(R.string.create_entry);
        final Calendar cal = Calendar.getInstance();
        // Calendar for saving entered Date and Time
        calendarForDb = Calendar.getInstance();

        // Set date and time to current date and time on initial create
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month++;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);

        newEntryDate.setText(dateFix(month, day, year));
        newEntryTime.setText(checkTimeString(hour, minute));

        newEntryDate.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(NewEntryActivity.this,
                    (Build.VERSION.SDK_INT >= 21 ? android.R.style.Theme_Material_Dialog_Alert
                            : android.R.style.Theme_Holo_Light_Dialog),
                    (view, year1, month1, dayOfMonth) -> {
                        month1++;
                        newEntryDate.setText(dateFix(month1, dayOfMonth, year1));
                        month1--;
                        calendarForDb.set(year1, month1, dayOfMonth);
                    }, cal.get(Calendar.YEAR), // year
                    cal.get(Calendar.MONTH), // month
                    cal.get(Calendar.DAY_OF_MONTH)); // day

            if (Build.VERSION.SDK_INT < 21)
                if (dialog.getWindow() != null)
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

            dialog.show();
        });

        newEntryTime.setOnClickListener(v -> {
            Calendar cal1 = Calendar.getInstance();

            TimePickerDialog timePickerDialog = new TimePickerDialog(NewEntryActivity.this,
                    (view, hourOfDay, minute1) -> {
                        newEntryTime.setText(checkTimeString(hourOfDay, minute1));
                        calendarForDb.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendarForDb.set(Calendar.MINUTE, minute1);
                    },
                    cal1.get(Calendar.HOUR_OF_DAY), // current hour
                    cal1.get(Calendar.MINUTE), // current minute
                    false); //no 24 hour view
            timePickerDialog.show();
        });

        breakfast.setOnClickListener(view -> {
            status = 1;
            Log.i(TAG, "status: " + status);
            statusButtonCheck(status);
            Toast.makeText(NewEntryActivity.this, "Breakfast", Toast.LENGTH_SHORT).show();
        });
        lunch.setOnClickListener(view -> {
            status = 2;
            Log.i(TAG, "status: " + status);
            statusButtonCheck(status);
            Toast.makeText(this, "Lunch", Toast.LENGTH_SHORT).show();
        });
        dinner.setOnClickListener(view -> {
            status = 3;
            Log.i(TAG, "status: " + status);
            statusButtonCheck(status);
            Toast.makeText(this, "Dinner", Toast.LENGTH_SHORT).show();
        });
        sick.setOnClickListener(view -> {
            status = 4;
            Log.i(TAG, "status: " + status);
            statusButtonCheck(status);
            Toast.makeText(this, "Sick", Toast.LENGTH_SHORT).show();
        });
        exercise.setOnClickListener(view -> {
            status = 5;
            Log.i(TAG, "status: " + status);
            statusButtonCheck(status);
            Toast.makeText(this, "Exercise", Toast.LENGTH_SHORT).show();
        });
        sweets.setOnClickListener(view -> {
            status = 6;
            Log.i(TAG, "status: " + status);
            statusButtonCheck(status);
            Toast.makeText(this, "Sweets", Toast.LENGTH_SHORT).show();
        });

        cancelBtn.setOnClickListener(v -> finish());
        saveBtn.setOnClickListener(v -> saveEntry());
    }

    private void saveEntry() {
        // Checks to make sure there is a blood glucose given.
        if (newEntryBloodGlucose.getText().toString().equals(""))
            Toast.makeText(this, R.string.no_glucose_toast, Toast.LENGTH_SHORT).show();
        else {
            realm.executeTransaction(realm1 -> {
                // Save Entry to database
                EntryItem entryItem = NewEntryActivity.this.realm.createObject(EntryItem.class);
                // Creates Date object made from the DatePicker and TimePicker
                Date date = calendarForDb.getTime();
                entryItem.setDate(date);
                entryItem.setStatus(status);
                entryItem.setBloodGlucose(Integer.parseInt(newEntryBloodGlucose.getText().toString()));
                // Prevention of NullPointerException
                if (!newEntryCarbohydrates.getText().toString().equals("")) {
                    entryItem.setCarbohydrates(Integer.parseInt(newEntryCarbohydrates.getText().toString()));
                }
                // Prevention of NullPointerException
                if (!newEntryInsulin.getText().toString().equals("")) {
                    entryItem.setInsulin(Double.parseDouble(newEntryInsulin.getText().toString()));
                }
            });

            // After save returns to MainActivity ListFragment
            finish();
        }
    }

    // Fonts used in Activity
    private void setFonts() {
        Typeface avenirRegular = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-Regular.otf");
        Typeface avenirMedium = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-Medium.otf");
        cancelBtn.setTypeface(avenirMedium);
        saveBtn.setTypeface(avenirMedium);
        if (MainActivity.sharedPreferences.getBoolean("pref_dark_mode", false)) {
            int white = getResources().getColor(R.color.white);
            cancelBtn.setTextColor(white);
            saveBtn.setTextColor(white);
        }
        newEntryDate.setTypeface(avenirRegular);
        newEntryTime.setTypeface(avenirRegular);
        newEntryBloodGlucose.setTypeface(avenirRegular);
        newEntryCarbohydrates.setTypeface(avenirRegular);
        newEntryInsulin.setTypeface(avenirRegular);
    }

    // dateFix
    StringBuilder dateFix(int month, int day, int year) {
        return new StringBuilder().append(month).append("/").append(day).append("/").append(year);
    }

    private void statusButtonCheck(int status) {
        Drawable status_checked = getResources().getDrawable(R.drawable.status_checked);
        Drawable status_unchecked = getResources().getDrawable(R.drawable.status_unchecked);
        switch (status) {
            case 1:
                breakfast.setBackground(status_checked);
                lunch.setBackground(status_unchecked);
                dinner.setBackground(status_unchecked);
                sick.setBackground(status_unchecked);
                exercise.setBackground(status_unchecked);
                sweets.setBackground(status_unchecked);
                break;
            case 2:
                breakfast.setBackground(status_unchecked);
                lunch.setBackground(status_checked);
                dinner.setBackground(status_unchecked);
                sick.setBackground(status_unchecked);
                exercise.setBackground(status_unchecked);
                sweets.setBackground(status_unchecked);
                break;
            case 3:
                breakfast.setBackground(status_unchecked);
                lunch.setBackground(status_unchecked);
                dinner.setBackground(status_checked);
                sick.setBackground(status_unchecked);
                exercise.setBackground(status_unchecked);
                sweets.setBackground(status_unchecked);
                break;
            case 4:
                breakfast.setBackground(status_unchecked);
                lunch.setBackground(status_unchecked);
                dinner.setBackground(status_unchecked);
                sick.setBackground(status_checked);
                exercise.setBackground(status_unchecked);
                sweets.setBackground(status_unchecked);
                break;
            case 5:
                breakfast.setBackground(status_unchecked);
                lunch.setBackground(status_unchecked);
                dinner.setBackground(status_unchecked);
                sick.setBackground(status_unchecked);
                exercise.setBackground(status_checked);
                sweets.setBackground(status_unchecked);
                break;
            case 6:
                breakfast.setBackground(status_unchecked);
                lunch.setBackground(status_unchecked);
                dinner.setBackground(status_unchecked);
                sick.setBackground(status_unchecked);
                exercise.setBackground(status_unchecked);
                sweets.setBackground(status_checked);
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        realm.close();
        super.onDestroy();
    }
}