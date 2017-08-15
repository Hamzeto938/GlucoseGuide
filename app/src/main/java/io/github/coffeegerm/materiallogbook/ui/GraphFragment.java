package io.github.coffeegerm.materiallogbook.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.coffeegerm.materiallogbook.R;
import io.github.coffeegerm.materiallogbook.adapter.GraphAdapter;
import io.github.coffeegerm.materiallogbook.model.EntryItem;
import io.github.coffeegerm.materiallogbook.utils.XAxisValueFormatter;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by David Yarzebinski on 6/25/2017.
 * <p>
 * Graph View fragment that will show a graph of sugar levels to show
 * patterns of highs and lows
 */

public class GraphFragment extends Fragment {

    private static final String TAG = "GraphFragment";
    @BindView(R.id.line_chart)
    LineChart lineChart;
    @BindView(R.id.graph_rec_view)
    RecyclerView recyclerView;
    GraphAdapter graphAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View graphView = inflater.inflate(R.layout.fragment_graph, container, false);
        ButterKnife.bind(this, graphView);
        setupRecView();
        setupGraph();
        return graphView;
    }

    private void setupRecView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        graphAdapter = new GraphAdapter(getDescendingDataList(), getActivity());
        recyclerView.setAdapter(graphAdapter);
    }

    private void setupGraph() {
        // Get sorted List from RealmResults
        List<EntryItem> entryObjects = getAscendingDataList();
        // List of Entries for Graph
        List<Entry> graphEntryPoints = new ArrayList<>();
        for (int positionInList = 0; positionInList < entryObjects.size(); positionInList++) {
            // X value = Date/Time
            float itemDate = entryObjects.get(positionInList).getDate().getTime();
            // Y value = Blood glucose level
            float itemGlucoseLevel = entryObjects.get(positionInList).getBloodGlucose();
            // Set X and Y values in the graphEntryPoints list
            graphEntryPoints.add(new Entry(itemDate, itemGlucoseLevel));
        }

        Log.i(TAG, "setupGraph: " + graphEntryPoints.toString());

        if (graphEntryPoints.size() == 0) {
            Log.i(TAG, "setupGraph: No graphEntryPoints found");
            lineChart.setNoDataText("No data to display");
        } else {
            LineDataSet dataSet = new LineDataSet(graphEntryPoints, "Blood Sugar Levels"); // Adding graphEntryPoints to dataset
            dataSet.setLineWidth(1f);
            dataSet.setValueTextColor(Color.BLACK); // Values on side will have text color of black
            LineData lineData = new LineData(dataSet); // Sets the data found in the database to the LineChart
            lineChart.setData(lineData);
            lineChart.getDescription().setEnabled(false); // Disables description below chart
            lineChart.setDrawGridBackground(false);
            lineChart.setScaleMinima(5f, 1f);
            lineChart.setScaleEnabled(true);
            lineChart.getXAxis().setLabelCount(graphEntryPoints.size());
            lineChart.setDragEnabled(true); // Enables the user to drag the chart left and right to see varying days and times of pattern
            lineChart.setPinchZoom(false); // Disables the ability to pinch the chart to zoom in
            lineChart.setDoubleTapToZoomEnabled(false); // Disables the user to double tap to zoom, rather useless feature in present day form factor
            lineChart.getLegend().setEnabled(false); // Disables the legend at the bottom
            lineChart.getAxisLeft().setDrawGridLines(false);
            // Disables Y values on the right of chart
            YAxis yAxisRight = lineChart.getAxisRight();
            yAxisRight.setEnabled(false);
            // Handles X Axis formatting, position and such
            XAxis xAxis = lineChart.getXAxis();
            // Changes text color according to dark mode preference
            if (MainActivity.sharedPreferences.getBoolean("pref_dark_mode", false)) {
                int white = getResources().getColor(R.color.white);
                xAxis.setTextColor(white);
                YAxis yAxisLeft = lineChart.getAxisLeft();
                yAxisLeft.setTextColor(white);
            }
            IAxisValueFormatter xAxisFormatter = new XAxisValueFormatter();
            xAxis.setValueFormatter(xAxisFormatter);
            xAxis.setGranularity(1f);
            xAxis.setDrawGridLines(false);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // Positions text of X Axis on bottom of Graph

            lineChart.invalidate(); // Refreshes graph
        }
    }

    // Method to get List of Realm Entries in timeline from newest to oldest
    private List<EntryItem> getDescendingDataList() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<EntryItem> entryItems = realm.where(EntryItem.class)
                .greaterThan("bloodGlucose", 0) // Only accounts for glucose levels higher than zero
                .findAllSorted("date", Sort.DESCENDING); // Finds all entries with Blood Glucose higher than zero order from newest to oldest
        return new ArrayList<>(entryItems);
    }

    // Method to get List of Realm Entries for LineChart so that they are from the oldest to the newest
    private List<EntryItem> getAscendingDataList() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<EntryItem> entryItems = realm.where(EntryItem.class)
                .greaterThan("bloodGlucose", 0) // Only accounts for glucose levels higher than zero
                .findAllSorted("date", Sort.ASCENDING); // Finds all entries with Blood Glucose higher than zero order from oldest to newest
        return new ArrayList<>(entryItems);
    }
}
