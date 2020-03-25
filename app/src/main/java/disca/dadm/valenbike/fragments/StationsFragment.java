package disca.dadm.valenbike.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import disca.dadm.valenbike.R;
import disca.dadm.valenbike.adapters.StationsAdapter;
import disca.dadm.valenbike.models.Station;

public class StationsFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Station> stationsList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Creates the View associated to this Fragment from a Layout resource.
     */


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stations, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewStations);

        initData();
        initRecyclerView();

        return view;
    }

    private void initData() {
        stationsList = new ArrayList<>();

    }

    private void initRecyclerView() {
        StationsAdapter stationsAdapter = new StationsAdapter(stationsList);
        recyclerView.setAdapter(stationsAdapter);
    }
}
