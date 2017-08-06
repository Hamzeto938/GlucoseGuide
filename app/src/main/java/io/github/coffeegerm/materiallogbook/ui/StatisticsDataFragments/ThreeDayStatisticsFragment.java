package io.github.coffeegerm.materiallogbook.ui.StatisticsDataFragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.coffeegerm.materiallogbook.R;
import io.github.coffeegerm.materiallogbook.model.EntryItem;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by David Yarzebinski on 7/28/2017.
 * <p>
 * Fragment used with Statistics ViewPager to show
 * the last three days of statistics
 */

public class ThreeDayStatisticsFragment extends Fragment {

    private static final String TAG = "ThreeDaysStatistics";

    @BindView(R.id.three_days_lowest)
    TextView lowestBloodGlucose;
    @BindView(R.id.three_days_highest)
    TextView highestBloodGlucose;
    @BindView(R.id.three_days_average)
    TextView averageBloodGlucose;
    @BindView(R.id.three_days_average_label)
    TextView averageLabel;
    @BindView(R.id.three_days_highest_label)
    TextView highestLabel;
    @BindView(R.id.three_days_lowest_label)
    TextView lowestLabel;
    Realm realm;
    private String pageTitle;
    private int pageNumber;

    public static ThreeDayStatisticsFragment newInstance(int pageNumber, String pageTitle) {
        ThreeDayStatisticsFragment threeDayStatisticsFragment = new ThreeDayStatisticsFragment();
        Bundle args = new Bundle();
        args.putInt("pageNumber", pageNumber);
        args.putString("pageTitle", pageTitle);
        threeDayStatisticsFragment.setArguments(args);
        return threeDayStatisticsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageTitle = getArguments().getString("pageTitle");
        pageNumber = getArguments().getInt("pageNumber");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View threeDaysStatisticsView = inflater.inflate(R.layout.fragment_three_days_stats, container, false);
        ButterKnife.bind(this, threeDaysStatisticsView);
        realm = Realm.getDefaultInstance();
        Date threeDaysAgo = getDateThreeDaysAgo();
        RealmResults<EntryItem> entriesFromLastThreeDays = realm.where(EntryItem.class).greaterThan("date", threeDaysAgo).greaterThan("bloodGlucose", 0).findAll();
        setFonts();

        if (entriesFromLastThreeDays.size() == 0) {
            averageBloodGlucose.setText(R.string.dash);
            highestBloodGlucose.setText(R.string.dash);
            lowestBloodGlucose.setText(R.string.dash);
        } else {
            averageBloodGlucose.setText(String.valueOf(getAverageGlucose(threeDaysAgo)));
            highestBloodGlucose.setText(String.valueOf(getHighestGlucose(threeDaysAgo)));
            lowestBloodGlucose.setText(String.valueOf(getLowestGlucose(threeDaysAgo)));
        }
        return threeDaysStatisticsView;
    }

    public int getAverageGlucose(Date threeDaysAgo) {
        int total = 0;
        RealmResults<EntryItem> entriesFromLastThreeDays = realm.where(EntryItem.class).greaterThan("date", threeDaysAgo).greaterThan("bloodGlucose", 0).findAll();
        for (int position = 0; position < entriesFromLastThreeDays.size(); position++) {
            EntryItem currentItem = entriesFromLastThreeDays.get(position);
            total += currentItem.getBloodGlucose();
        }
        return total / entriesFromLastThreeDays.size();
    }

    public int getHighestGlucose(Date threeDaysAgo) {
        int highest = 0;
        RealmResults<EntryItem> entriesFromLastThreeDays = realm.where(EntryItem.class).greaterThan("date", threeDaysAgo).greaterThan("bloodGlucose", 0).findAll();
        for (int position = 0; position < entriesFromLastThreeDays.size(); position++) {
            EntryItem currentItem = entriesFromLastThreeDays.get(position);
            if (currentItem.getBloodGlucose() > highest) {
                highest = currentItem.getBloodGlucose();
            }
        }
        return highest;
    }

    public int getLowestGlucose(Date threeDaysAgo) {
        int lowest = 1000;
        RealmResults<EntryItem> entriesFromLastThreeDays = realm.where(EntryItem.class).greaterThan("date", threeDaysAgo).greaterThan("bloodGlucose", 0).findAll();
        for (int position = 0; position < entriesFromLastThreeDays.size(); position++) {
            EntryItem currentItem = entriesFromLastThreeDays.get(position);
            if (currentItem.getBloodGlucose() < lowest) {
                lowest = currentItem.getBloodGlucose();
            }
        }
        return lowest;
    }

    public Date getDateThreeDaysAgo() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -3);
        return calendar.getTime();
    }

    private void setFonts() {
        Typeface avenirRegular = Typeface.createFromAsset(getContext().getAssets(), "fonts/AvenirNext-Regular.otf");
        Typeface avenirMedium = Typeface.createFromAsset(getContext().getAssets(), "fonts/AvenirNext-Medium.otf");
        averageBloodGlucose.setTypeface(avenirMedium);
        highestBloodGlucose.setTypeface(avenirMedium);
        lowestBloodGlucose.setTypeface(avenirMedium);
        averageLabel.setTypeface(avenirRegular);
        highestLabel.setTypeface(avenirRegular);
        lowestLabel.setTypeface(avenirRegular);
    }
}
