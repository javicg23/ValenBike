package disca.dadm.valenbike.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
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
import disca.dadm.valenbike.models.Station;
import disca.dadm.valenbike.tasks.StationsAsyncTask;

public class StationsFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Station> stationsList;

    private WeakReference<StationsAsyncTask> asyncTaskWeakRef;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        startNewAsyncTask();
        initRecyclerView();

        return view;
    }

    private void startNewAsyncTask() {
        StationsAsyncTask task = new StationsAsyncTask(this);
        this.asyncTaskWeakRef = new WeakReference<>(task);
        task.execute();
    }

    public void initData(List<disca.dadm.valenbike.database.Station> dbStations) {

        for(int i = 0; i < dbStations.size(); i++) {
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

        initRecyclerView();
    }

    private void initRecyclerView() {
        StationsAdapter stationsAdapter = new StationsAdapter(stationsList);
        recyclerView.setAdapter(stationsAdapter);
    }
}
