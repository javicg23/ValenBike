package disca.dadm.valenbike.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import disca.dadm.valenbike.R;
import disca.dadm.valenbike.adapters.StationsAdapter;
import disca.dadm.valenbike.database.ValenbikeDatabase;
import disca.dadm.valenbike.models.StationGUI;
import disca.dadm.valenbike.tasks.StationsAsyncTask;

public class StationsFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<StationGUI> stationsList;

    private WeakReference<StationsAsyncTask> asyncTaskWeakRef;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        setRetainInstance(true);

    }

    /**
     * Creates the View associated to this Fragment from a Layout resource.
     */


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stations, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewStations);

        //startNewAsyncTask();
        initData();
        initRecyclerView();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.filter_station_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_filter:
                // Show check menu to select filter options
                AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
                builder.setTitle(R.string.selection_filter_menu_station);
                //CharSequence[] options = new CharSequence[]{R.string.fav_filter_menu_station, R.string.notify_filter_menu_station};
                final String[] options = new String[]{
                        "Estaciones favoritas",
                        "Estciones con avisos"
                };
                final boolean[] checkedOptions = new boolean[] {
                        false,
                        false
                };

                builder.setMultiChoiceItems(options, checkedOptions, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        checkedOptions[which] = isChecked;
                    }
                });

                builder.setPositiveButton(R.string.filter_menu_station, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for(int i = 0; i < options.length; i++) {
                            if (checkedOptions[i]) {
                                // TODO: Get stations that match with the selected filter options.
                            }
                        }
                    }
                });
                builder.create().show();
                return true;

            default:
                break;

        }

        return false;
    }

    private void startNewAsyncTask() {
        StationsAsyncTask task = new StationsAsyncTask(this);
        this.asyncTaskWeakRef = new WeakReference<>(task);
        task.execute();
    }

    public void initData(/*List<disca.dadm.valenbike.database.Station> dbStations*/) {

        /*if (dbStations != null) {
            for (int i = 0; i < dbStations.size(); i++) {
                Station newStation = new Station(
                        dbStations.get(i).getName(),
                        dbStations.get(i).getAddress(),
                        dbStations.get(i).getNumFreeBikes(),
                        dbStations.get(i).getNumFreeGaps(),
                        dbStations.get(i).isFavourite(),
                        dbStations.get(i).getNotify()
                );
                stationsList.add(newStation);
            }
        } else {*/
            // DEBUG without API data.
            stationsList = disca.dadm.valenbike.test.getStationList();
        /*}

        initRecyclerView();*/
    }

    private void initRecyclerView() {
        StationsAdapter stationsAdapter = new StationsAdapter(stationsList);
        recyclerView.setAdapter(stationsAdapter);
    }
}
