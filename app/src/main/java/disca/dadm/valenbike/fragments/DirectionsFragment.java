package disca.dadm.valenbike.fragments;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.nio.file.Watchable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import disca.dadm.valenbike.R;
import disca.dadm.valenbike.interfaces.OnPetitionTaskCompleted;
import disca.dadm.valenbike.models.Station;
import disca.dadm.valenbike.tasks.PetitionAsyncTask;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static disca.dadm.valenbike.fragments.MapFragment.LIMIT_MAP;
import static disca.dadm.valenbike.utils.Tools.getStations;
import static disca.dadm.valenbike.utils.Tools.isNetworkConnected;
import static disca.dadm.valenbike.utils.Tools.showSnackBar;

public class DirectionsFragment extends Fragment implements OnPetitionTaskCompleted {

    public static final String ROUTE = "route";
    public static final String SOURCE_POSITION = "source_position";
    public static final String SOURCE_ADDRESS = "source_address";
    public static final String DESTINATION_POSITION = "destination_position";
    public static final String DESTINATION_ADDRESS = "destination_address";
    private final boolean NEAREST_BIKE = true;
    private final boolean NEAREST_PARKING = false;

    private View rootView;
    private String locationAddress, sourceAddress, destinationAddress;
    private int routeDirections;
    private LatLng sourcePosition, destinationPosition, sourceStation, destinationStation, locationPosition;
    private List<Station> allStation;
    private AutocompleteSupportFragment autocompleteSearchSource, autocompleteSearchDestination;

    private EditText sourceText, destinationText;
    private ImageButton swapRouteButton;
    private CheckBox locationCheck;
    private FloatingActionButton fabDirections;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestStations(0);

    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle args = getArguments();
        if (args != null) {
            routeDirections = args.getInt(ROUTE);

            locationAddress = args.getString(SOURCE_ADDRESS);
            sourceText.setText(locationAddress);
            sourceAddress = locationAddress;

            locationPosition = args.getParcelable(SOURCE_POSITION);
            sourcePosition = locationPosition;

            destinationAddress = args.getString(DESTINATION_ADDRESS);
            destinationText.setText(destinationAddress);
            destinationPosition = args.getParcelable(DESTINATION_POSITION);

            if (locationAddress.equals("")) {
                locationCheck.setBackground(getResources().getDrawable(R.drawable.ic_my_location_light_grey_24dp, null));
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_directions, container, false);
        // hide bottom navigation and show back button on actionBar
        BottomNavigationView navigationView = getActivity().findViewById(R.id.bottomView);
        navigationView.setVisibility(View.GONE);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        locationCheck = rootView.findViewById(R.id.directionsLocation);
        swapRouteButton = rootView.findViewById(R.id.directionsSwapRoute);
        fabDirections = rootView.findViewById(R.id.directionsFabNavigation);
        autocompleteSearchSource = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.autocompleteSearchSource);
        autocompleteSearchDestination = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.autocompleteSearchDestination);

        initSearch();
        initListeners();

        return rootView;
    }

    private void initSearch() {
        // Source listener
        initSourceListener();
        // Destination listener
        initDestinationListener();
    }


    private void initSourceListener() {
        // Specify the types of place data to return, country and limit in the map
        autocompleteSearchSource.setPlaceFields(Arrays.asList(Place.Field.ADDRESS, Place.Field.NAME, Place.Field.LAT_LNG));
        autocompleteSearchSource.setCountry("ES");
        autocompleteSearchSource.setLocationRestriction(RectangularBounds.newInstance(LIMIT_MAP));

        // obtain reference to the edit text
        sourceText = autocompleteSearchSource.getView().findViewById(R.id.places_autocomplete_search_input);
        sourceText.setHint(getString(R.string.directions_source));
        // remove icon search
        autocompleteSearchSource.getView().findViewById(R.id.places_autocomplete_search_button).setVisibility(View.GONE);
        // Set up a PlaceSelectionListener to handle the response.
        autocompleteSearchSource.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                sourcePosition = place.getLatLng();
            }

            @Override
            public void onError(@NonNull Status status) {
                showSnackBar(rootView, true, getString(R.string.places_search_error));
            }
        });
    }

    private void initDestinationListener() {

        // Specify the types of place data to return, country and limit in the map
        autocompleteSearchDestination.setPlaceFields(Arrays.asList(Place.Field.ADDRESS, Place.Field.NAME, Place.Field.LAT_LNG));
        autocompleteSearchDestination.setCountry("ES");
        autocompleteSearchDestination.setLocationRestriction(RectangularBounds.newInstance(LIMIT_MAP));

        // obtain reference to the edit text
        destinationText = autocompleteSearchDestination.getView().findViewById(R.id.places_autocomplete_search_input);
        destinationText.setHint(getString(R.string.directions_destination));
        // remove icon search
        autocompleteSearchDestination.getView().findViewById(R.id.places_autocomplete_search_button).setVisibility(View.GONE);

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteSearchDestination.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                destinationPosition = place.getLatLng();
            }

            @Override
            public void onError(@NonNull Status status) {
                showSnackBar(rootView, true, getString(R.string.places_search_error));
            }
        });
    }

    private void initListeners() {

        sourceText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.toString().equals(locationAddress) && !locationAddress.equals("")) {
                    locationCheck.setBackground(getResources().getDrawable(R.drawable.ic_my_location_accent_24dp, null));
                } else {
                    locationCheck.setBackground(getResources().getDrawable(R.drawable.ic_my_location_light_grey_24dp, null));
                }
            }
        });

        locationCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!locationAddress.equals("")) {
                    sourceText.setText(locationAddress);
                    locationCheck.setBackground(getResources().getDrawable(R.drawable.ic_my_location_accent_24dp, null));
                } else {
                    showSnackBar(rootView, false, getString(R.string.directions_no_gps_location));
                }
            }
        });

        swapRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String aux = destinationText.getText().toString();
                destinationText.setText(sourceText.getText().toString());
                sourceText.setText(aux);
            }
        });

        fabDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sourceText.getText().toString().trim().equals("") || destinationText.getText().toString().trim().equals("")){
                    showSnackBar(rootView, false, getString(R.string.directions_no_source_destination));
                } else {
                    showSnackBar(rootView, false, "buscar ruta");
                }
            }
        });

    }

    /*todo remove all this statiosn and make the request*/
    private void requestStations(int number) {
        /*
        if (isNetworkConnected(getContext())){
            PetitionAsyncTask petitionAsyncTask = new PetitionAsyncTask(getContext(),this);
            petitionAsyncTask.execute(number);
        }*/
        allStation = getStations();
    }

    @Override
    public void receivedAllStations(List<Station> stations) {
        allStation = stations;
    }

    @Override
    public void receivedStation(Station station) {
    }

    private void searchRoute() {

    }

    private Station getNearestStationToPosition(boolean type, LatLng latLng) {
        Location location = new Location("provider");
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);
        Station nearestStation = null;
        float nearestMeters = Float.MAX_VALUE;

        for (int i = 0; i < allStation.size(); i++) {
            Station station = allStation.get(i);
            if (station.isActive() && ((type && station.getAvailableBikes() > 0) || (!type && station.getAvailableBikeStands() > 0))) {
                Location locationStation = new Location("provider");
                locationStation.setLatitude(station.getPosition().getLat());
                locationStation.setLongitude(station.getPosition().getLng());
                float distance = location.distanceTo(locationStation);
                if (distance < nearestMeters) {
                    nearestMeters = distance;
                    nearestStation = station;
                }
            }
        }

        return nearestStation;
    }
}
