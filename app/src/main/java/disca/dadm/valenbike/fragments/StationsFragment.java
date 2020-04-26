package disca.dadm.valenbike.fragments;

import android.app.AlertDialog;
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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import disca.dadm.valenbike.R;
//import disca.dadm.valenbike.adapters.StationsAdapter;
import disca.dadm.valenbike.adapters.StationsAdapter;
import disca.dadm.valenbike.interfaces.DataPassListener;
import disca.dadm.valenbike.models.Position;
import disca.dadm.valenbike.database.StationDb;
import disca.dadm.valenbike.interfaces.OnPetitionTaskCompleted;
import disca.dadm.valenbike.models.Station;
import disca.dadm.valenbike.models.StationGUI;
import disca.dadm.valenbike.tasks.HistoryAsyncTask;
import disca.dadm.valenbike.tasks.PetitionAsyncTask;
import disca.dadm.valenbike.tasks.StationsDbAsyncTask;
import disca.dadm.valenbike.tasks.UpdateDbAsyncTask;
import disca.dadm.valenbike.utils.Tools;
import disca.dadm.valenbike.utils.test;

import static disca.dadm.valenbike.utils.Tools.getDialogProgressBar;
import static disca.dadm.valenbike.utils.Tools.isNetworkConnected;


public class StationsFragment extends Fragment implements OnPetitionTaskCompleted {
    public static final String STATION_RESPONSE = "station_response";
    private static final String TAG = "StationsFragment";
    private static final int FAVOURITE_STATIONS = 0;
    private static final int NOTIFIED_STATIONS = 1;
    private RecyclerView recyclerView;
    private List<StationGUI> stationsList;
    private DataPassListener dataPassListener;
    private List<Station> receivedStations;
    private List<StationDb> dbStations;
    private List<StationDb> updatedStations;
    private StationsAdapter stationsAdapter;
    private WeakReference<UpdateDbAsyncTask> asyncTaskWeakRef;

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

        receivedStations = Tools.getStations();

        StationsDbAsyncTask dbTask = new StationsDbAsyncTask(this);
        dbTask.execute();

//        if (isNetworkConnected(Objects.requireNonNull(getContext()))) {
//            PetitionAsyncTask petitionAsyncTask = new PetitionAsyncTask(getContext(), this);
//            petitionAsyncTask.execute(0);
//        }
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
                        "Estaciones con notificaciones",
                };
                final boolean[] checkedOptions = new boolean[]{
                        false, // Favourite stations
                        false,  // Notified stations
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
                        if (checkedOptions[FAVOURITE_STATIONS]) {
                            stationsList = getFavouriteStations();

                            if (checkedOptions[NOTIFIED_STATIONS]) {
                                stationsList.addAll(getNotifiedStations());
                            }
                        } else {
                            stationsList = getNotifiedStations();
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
//        if (dbStations.isEmpty()) {
//            Log.d("DEBUG", "NO HAN LLEGADO LAS ESTACIONES DE LA DB");
//            return;
//        }

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

    // TODO: May be public to implement notifications.
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
