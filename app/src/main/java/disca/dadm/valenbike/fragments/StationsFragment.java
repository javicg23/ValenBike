package disca.dadm.valenbike.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import disca.dadm.valenbike.R;
import disca.dadm.valenbike.adapters.StationsAdapter;
import disca.dadm.valenbike.interfaces.DataPassListener;
import disca.dadm.valenbike.database.StationDb;
import disca.dadm.valenbike.interfaces.OnPetitionTaskCompleted;
import disca.dadm.valenbike.models.Station;
import disca.dadm.valenbike.models.StationGUI;
import disca.dadm.valenbike.tasks.PetitionAsyncTask;
import disca.dadm.valenbike.tasks.StationsDbAsyncTask;
import disca.dadm.valenbike.tasks.UpdateDbAsyncTask;
import disca.dadm.valenbike.utils.Tools;




public class StationsFragment extends Fragment implements OnPetitionTaskCompleted {
    public static final String STATION_RESPONSE = "station_response";
    private static final String TAG = "StationsFragment";

    private RecyclerView recyclerView;
    private List<StationGUI> stationsList;
    private DataPassListener dataPassListener;
    private List<Station> receivedStations;
    private List<StationDb> dbStations;
    private List<StationDb> updatedStations;
    private StationsAdapter stationsAdapter;

    private static final int FAVOURITE_STATIONS = 0;
    private static final int NOTIFIED_STATIONS = 1;

    public StationsFragment() {
        // Required empty public constructor
    }

    public static StationsFragment newInstance() {
        return new StationsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        setRetainInstance(true);

        dbStations = new ArrayList<>();
        updatedStations = new ArrayList<>();
    }

    /**
     * Creates the View associated to this Fragment from a Layout resource.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stations, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewStations);

        StationsDbAsyncTask dbTask = new StationsDbAsyncTask(this);
        dbTask.execute();

        if (Tools.isNetworkConnected(Objects.requireNonNull(getContext()))) {
            PetitionAsyncTask petitionAsyncTask = new PetitionAsyncTask(getContext(), this);
            petitionAsyncTask.execute(0);
        }
        initRecyclerView();
        return view;
    }

    @Override
    public void onStart() {
        initRecyclerView();
        super.onStart();
    }

    @Override
    public void onPause() {
        UpdateDbAsyncTask task = new UpdateDbAsyncTask(getContext());
        task.execute(updatedStations);
        super.onPause();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.filter_station_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
        Log.d("DEBUG", "SE HA CREADO EL MENU DE OPCIONES");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_filter:
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this.getContext());

                final boolean[] checkedOptions = new boolean[]{
                        false, // Favourite stations
                        false,  // Notified stations
                };

                builder.setMultiChoiceItems(R.array.filter_menu_options, checkedOptions, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        checkedOptions[which] = isChecked;
                    }
                });

                builder.setCancelable(true);
                builder.setTitle(R.string.selection_filter_menu_station);
                builder.setPositiveButton(R.string.filter_menu_station, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        List<StationGUI> filteredStations = new ArrayList<>();
                        if (checkedOptions[FAVOURITE_STATIONS]) {
                            filteredStations = getFavouriteStations();
                            setStationsList(filteredStations);

                            if (checkedOptions[NOTIFIED_STATIONS]) {
                                filteredStations = getNotifiedStations();
                                filteredStations.addAll(getNotifiedStations());
                                setStationsList(filteredStations);
                            }
                        } else {
                            filteredStations = getNotifiedStations();
                            setStationsList(filteredStations);
                        }

                    }
                });
                builder.setNegativeButton(R.string.cancel_filter_option, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d("DEBUG", "SE HA CANCELADO LA OPCION DE FILTRAR");
                    }
                });
                builder.create().show();
                return true;

            default:
                super.onOptionsItemSelected(item);
                return true;
        }
    }


    @Override
    public void receivedAllStations(List<Station> stations) {
        receivedStations = stations;
        initData();
    }

    @Override
    public void receivedStation(Station station) {
        // Required to be declared.
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            dataPassListener = (DataPassListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement DataPassListener");
        }
    }


    public void getListDb(List<StationDb> list) {
        dbStations = list;
        initData();
    }

    /*
        Merge api stations with db info about each one.
     */
    public void initData() {
        if (receivedStations.isEmpty()) {
            Log.d("DEBUG", "NO HAN LLEGADO LAS ESTACIONES DE LA API");
            return;
        }
        if (dbStations.isEmpty()) {
            Log.d("DEBUG", "NO HAN LLEGADO LAS ESTACIONES DE LA DB");
            return;
        }

        stationsList = Tools.mergeToStationGui(receivedStations, dbStations);
        initRecyclerView();
    }


    public void setDataPassListener(DataPassListener callback) {
        this.dataPassListener = callback;
    }

    public List<StationGUI> getStationsList() {
        return this.stationsList;
    }

    public void addUpdatedStation(StationGUI stationGui) {
        StationDb stationDb = new StationDb(stationGui.getNumber(), stationGui.isFavouriteCheck(), stationGui.isReminderCheck());
        updatedStations.add(stationDb);
    }

    public void setStationsList(List<StationGUI> stations) {
        this.stationsList = stations;
        stationsAdapter.notifyDataSetChanged();
    }


    private void initRecyclerView() {
        stationsAdapter = new StationsAdapter(this, stationsList, dataPassListener, getContext());
        recyclerView.setAdapter(stationsAdapter);
    }

    private List<StationGUI> getFavouriteStations() {
        List<StationGUI> favouriteStations = new ArrayList<>();

        for (int i = 0; i < stationsList.size(); i++) {
            if (stationsList.get(i).isFavouriteCheck()) {
                favouriteStations.add(stationsList.get(i));
            }
        }

        return favouriteStations;
    }

    private List<StationGUI> getNotifiedStations() {
        List<StationGUI> notifiedStations = new ArrayList<>();

        for (int i = 0; i < stationsList.size(); i++) {
            if (stationsList.get(i).isReminderCheck()) {
                notifiedStations.add(stationsList.get(i));
            }
        }

        return notifiedStations;
    }
}
