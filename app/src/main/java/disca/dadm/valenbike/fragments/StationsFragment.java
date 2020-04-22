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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import disca.dadm.valenbike.R;
//import disca.dadm.valenbike.adapters.StationsAdapter;
import disca.dadm.valenbike.adapters.StationsAdapter;
import disca.dadm.valenbike.models.Position;
import disca.dadm.valenbike.models.Station;
import disca.dadm.valenbike.models.StationGUI;
import disca.dadm.valenbike.utils.Tools;
import disca.dadm.valenbike.utils.test;
//import disca.dadm.valenbike.tasks.StationsAsyncTask;

public class StationsFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<StationGUI> stationsList;

//    private WeakReference<StationsAsyncTask> asyncTaskWeakRef;


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
        //initData();
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
                final String[] options = new String[]{
                        "Estaciones favoritas",
                        "Estaciones con notificaciones"
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

//    private void startNewAsyncTask() {
//        StationsAsyncTask task = new StationsAsyncTask(this);
//        this.asyncTaskWeakRef = new WeakReference<>(task);
//        task.execute();
//    }

    public void initData() {
        // DEBUG without API data.
//        List <Station> stationsApi = Tools.getStations();
//
//        for(int i = 0; i < stationsApi.size(); i++) {
//            Station station = stationsApi.get(i);
//            int number = station.getNumber();
//            String contractName = station.getContractName();
//            String name = station.getName();
//            String address = station.getAddress();
//            Position pos = station.getPosition();
//            boolean banking = station.getBanking();
//            boolean bonus = station.getBonus();
//            int bikeStands = station.getBikeStands();
//            int availableBikeStands = station.getAvailableBikeStands();
//            int availableBikes = station.getAvailableBikes();
//            String status = station.getStatus();
//            long lastUpdate = station.getLastUpdate();
//
//            StationGUI stationGui = new StationGUI(number, contractName, name, address, pos, banking, bonus, bikeStands, availableBikeStands, availableBikes, status, lastUpdate);
//            stationsList.add(stationGui);
//        }

        //initRecyclerView();
    }

    private void initRecyclerView() {
        stationsList = Tools.getStationsGui();
        StationsAdapter stationsAdapter = new StationsAdapter(stationsList);
        recyclerView.setAdapter(stationsAdapter);
    }
}
