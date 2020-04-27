package disca.dadm.valenbike.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import disca.dadm.valenbike.R;
import disca.dadm.valenbike.adapters.StationsAdapter;
import disca.dadm.valenbike.interfaces.DataPassListener;
import disca.dadm.valenbike.interfaces.OnStationTaskCompleted;
import disca.dadm.valenbike.models.Station;
import disca.dadm.valenbike.models.StationGUI;
import disca.dadm.valenbike.tasks.StationsAsyncTask;
import disca.dadm.valenbike.utils.Tools;

import static disca.dadm.valenbike.utils.Tools.cleanString;
import static disca.dadm.valenbike.utils.Tools.hideKeyboard;

public class StationsFragment extends Fragment implements OnStationTaskCompleted {

    private RecyclerView recyclerView;
    private List<StationGUI> stationsList;
    private DataPassListener dataPassListener;
    public static List<StationGUI> stationFavouriteList = new ArrayList<>();
    public static final String STATION_RESPONSE = "station_response";
    public static boolean favouriteChecked = false;
    private TextView tvNotFavouriteText;
    private StationsAdapter stationsAdapter;
    private MenuItem menuItemFilter;
    private BottomNavigationView bottomNavigationView;

    public StationsFragment() {
        // Required empty public constructor
    }

    public static StationsFragment newInstance() {
        return new StationsFragment();
    }

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
        tvNotFavouriteText = view.findViewById(R.id.tvNotFavouriteStations);
        bottomNavigationView = container.getRootView().findViewById(R.id.bottomView);

        StationsAsyncTask stationsAsyncTask = new StationsAsyncTask(getContext(), this);
        stationsAsyncTask.execute(StationsAsyncTask.GET_ALL_STATIONS);

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_stations, menu);
        menuItemFilter = menu.findItem(R.id.menu_item_filter_button);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        item.setChecked(true);
        switch (item.getItemId()) {
            case R.id.menu_item_search_button:
                item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        bottomNavigationView.setVisibility(View.GONE);
                        SearchView searchView = (SearchView) item.getActionView();
                        searchView.onActionViewExpanded();
                        searchView.setQueryHint("Buscar...");
                        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
                        menuItemFilter.setEnabled(false);

                        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                            @Override
                            public boolean onQueryTextSubmit(String query) {
                                return false;
                            }

                            @Override
                            public boolean onQueryTextChange(String newText) {
                                stationsAdapter.getFilter().filter(cleanString(newText).toLowerCase());
                                return false;
                            }
                        });
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        hideKeyboard(getActivity());
                        menuItemFilter.setEnabled(true);
                        bottomNavigationView.setVisibility(View.VISIBLE);
                        return true;
                    }
                });
                return true;
            case R.id.station_filter_alphabetically:
                orderListAndShow(Station.alphabetically, stationsList);
                hideTextView();
                return true;
            case R.id.station_filter_number:
                orderListAndShow(Station.numerically, stationsList);
                hideTextView();
                return true;
            case R.id.station_filter_favourite:
                favouriteChecked = true;
                orderListAndShow(Station.numerically, stationFavouriteList);
                return true;
        }
        return super.onOptionsItemSelected(item);
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
                    if (stationGUIDatabase.isFavouriteCheck()) {
                        stationFavouriteList.add(stationGUIDatabase);
                    }
                    stationGUI.setReminderCheck(stationGUIDatabase.isReminderCheck());
                    break;
                }
            }
        }
        orderListAndShow(Station.numerically, stationsList);
    }

    private void orderListAndShow(Comparator<Station> comparator, List<StationGUI> stationList) {
        Collections.sort(stationList, comparator);
        stationsAdapter = new StationsAdapter(stationList, dataPassListener, getContext(), tvNotFavouriteText);
        recyclerView.setAdapter(stationsAdapter);
    }

    private void hideTextView() {
        favouriteChecked = false;
        tvNotFavouriteText.setVisibility(View.GONE);
    }
}
