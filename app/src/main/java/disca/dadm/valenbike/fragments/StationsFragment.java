package disca.dadm.valenbike.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import disca.dadm.valenbike.R;
//import disca.dadm.valenbike.adapters.StationsAdapter;
import disca.dadm.valenbike.adapters.StationsAdapter;
import disca.dadm.valenbike.databaseSQLite.ValenbikeSQLiteOpenHelper;
import disca.dadm.valenbike.interfaces.DataPassListener;
import disca.dadm.valenbike.interfaces.OnStationTaskCompleted;
import disca.dadm.valenbike.models.Position;
import disca.dadm.valenbike.models.Station;
import disca.dadm.valenbike.models.StationGUI;
import disca.dadm.valenbike.tasks.StationsAsyncTask;
import disca.dadm.valenbike.utils.Tools;
import disca.dadm.valenbike.utils.test;

import static disca.dadm.valenbike.utils.Tools.getDialogProgressBar;
//import disca.dadm.valenbike.tasks.StationsAsyncTask;

public class StationsFragment extends Fragment implements OnStationTaskCompleted {

    private RecyclerView recyclerView;
    private List<StationGUI> stationsList;
    private DataPassListener dataPassListener;
    public static final String STATION_RESPONSE = "station_response";

//    private WeakReference<StationsAsyncTask> asyncTaskWeakRef;

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

    }

    /**
     * Creates the View associated to this Fragment from a Layout resource.
     */


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stations, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewStations);

        StationsAsyncTask stationsAsyncTask = new StationsAsyncTask(getContext(), this);
        stationsAsyncTask.execute(StationsAsyncTask.GET_ALL_STATIONS);

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

    public void setDataPassListener(DataPassListener callback) {
        this.dataPassListener = callback;
    }

    @Override
    public void responseStationDatabase(List<StationGUI> list) {
        stationsList = Tools.getStationsGui();

        for (StationGUI stationGUIDatabase : list) {
            for (StationGUI stationGUI : stationsList) {
                if (stationGUI.getNumber() == stationGUIDatabase.getNumber()) {
                    stationGUI.setFavouriteCheck(stationGUIDatabase.isFavouriteCheck());
                    stationGUI.setReminderCheck(stationGUIDatabase.isReminderCheck());
                    break;
                }
            }
        }
        StationsAdapter stationsAdapter = new StationsAdapter(stationsList, dataPassListener, getContext());
        recyclerView.setAdapter(stationsAdapter);
    }
}
