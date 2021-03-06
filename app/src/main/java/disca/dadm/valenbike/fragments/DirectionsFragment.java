package disca.dadm.valenbike.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import disca.dadm.valenbike.R;
import disca.dadm.valenbike.interfaces.DataPassListener;
import disca.dadm.valenbike.interfaces.OnPetitionTaskCompleted;
import disca.dadm.valenbike.interfaces.OnRouteTaskCompleted;
import disca.dadm.valenbike.models.ParametersRouteTask;
import disca.dadm.valenbike.models.Station;
import disca.dadm.valenbike.tasks.RouteAsyncTask;

import static disca.dadm.valenbike.fragments.MapFragment.LIMIT_MAP;
import static disca.dadm.valenbike.utils.Tools.getDialogProgressBar;
import static disca.dadm.valenbike.utils.Tools.getStations;
import static disca.dadm.valenbike.utils.Tools.isNetworkConnected;

public class DirectionsFragment extends Fragment implements OnPetitionTaskCompleted, OnRouteTaskCompleted {

    public static final String SOURCE_POSITION = "source_position";
    public static final String SOURCE_ADDRESS = "source_address";
    public static final String DESTINATION_POSITION = "destination_position";
    public static final String DESTINATION_ADDRESS = "destination_address";
    private final boolean NEAREST_BIKE = true;
    private final boolean NEAREST_PARKING = false;

    private View rootView;
    private String locationAddress;
    private LatLng sourcePosition, destinationPosition, locationPosition;
    private List<Station> allStation;
    private AutocompleteSupportFragment autocompleteSearchSource, autocompleteSearchDestination;
    private DataPassListener dataPassListener;

    // to check if asyn task is received all of them
    private boolean receivedBike = true;
    private boolean receivedOrigin = true;
    private boolean receivedDestination = true;
    private String responseBike, responseOrigin, responseDestination;
    private boolean errorResponse = false;

    // loading progress
    private AlertDialog progressDialog;

    private EditText sourceText, destinationText;
    private ImageButton swapRouteButton;
    private CheckBox locationCheck;
    private FloatingActionButton fabDirections;
    private String nearestBikeAddress, nearestStandAddress;

    public DirectionsFragment() {
        // Required empty public constructor
    }

    public static DirectionsFragment newInstance() {
        return new DirectionsFragment();
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestStations(0);

    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle args = getArguments();
        if (args != null) {

            locationAddress = args.getString(SOURCE_ADDRESS);
            sourceText.setText(locationAddress);

            locationPosition = args.getParcelable(SOURCE_POSITION);
            sourcePosition = locationPosition;

            destinationText.setText(args.getString(DESTINATION_ADDRESS));
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
        BottomNavigationView navigationView = container.getRootView().findViewById(R.id.bottomView);
        navigationView.setVisibility(View.GONE);
        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        locationCheck = rootView.findViewById(R.id.directionsLocation);
        swapRouteButton = rootView.findViewById(R.id.directionsSwapRoute);
        fabDirections = rootView.findViewById(R.id.directionsFabNavigation);
        autocompleteSearchSource = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.autocompleteSearchSource);
        autocompleteSearchDestination = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.autocompleteSearchDestination);
        progressDialog = getDialogProgressBar(getContext()).create();

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
        sourceText = Objects.requireNonNull(autocompleteSearchSource.getView()).findViewById(R.id.places_autocomplete_search_input);
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
                Snackbar.make(rootView, getString(R.string.places_search_error), Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void initDestinationListener() {

        // Specify the types of place data to return, country and limit in the map
        autocompleteSearchDestination.setPlaceFields(Arrays.asList(Place.Field.ADDRESS, Place.Field.NAME, Place.Field.LAT_LNG));
        autocompleteSearchDestination.setCountry("ES");
        autocompleteSearchDestination.setLocationRestriction(RectangularBounds.newInstance(LIMIT_MAP));

        // obtain reference to the edit text
        destinationText = Objects.requireNonNull(autocompleteSearchDestination.getView()).findViewById(R.id.places_autocomplete_search_input);
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
                Snackbar.make(rootView, getString(R.string.places_search_error), Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void initListeners() {

        sourceText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.toString().equals(locationAddress) && !locationAddress.equals("")) {
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
                    sourcePosition = locationPosition;
                } else {
                    Snackbar.make(rootView, getString(R.string.directions_no_gps_location), Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        swapRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String auxText = destinationText.getText().toString();
                destinationText.setText(sourceText.getText().toString());
                sourceText.setText(auxText);
                LatLng auxPos = destinationPosition;
                destinationPosition = sourcePosition;
                sourcePosition = auxPos;
            }
        });

        fabDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sourceText.getText().toString().trim().equals("") || destinationText.getText().toString().trim().equals("")) {
                    Snackbar.make(rootView, getString(R.string.directions_no_source_destination), Snackbar.LENGTH_SHORT).show();
                } else {
                    fabDirections.setEnabled(false);
                    searchRoute();
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
        showProgressDialog();

        Station nearestBikeStation = getNearestStationToPosition(NEAREST_BIKE, sourcePosition);
        LatLng nearestBikePosition = new LatLng(nearestBikeStation.getPosition().getLat(), nearestBikeStation.getPosition().getLng());
        nearestBikeAddress = nearestBikeStation.getAddress();
        Station nearestStandStation = getNearestStationToPosition(NEAREST_PARKING, destinationPosition);
        LatLng nearestStandPosition = new LatLng(nearestStandStation.getPosition().getLat(), nearestStandStation.getPosition().getLng());
        nearestStandAddress = nearestStandStation.getAddress();
        if (sourcePosition == null || nearestBikeStation == null || nearestStandStation == null || destinationPosition == null) {
            Snackbar.make(rootView, getString(R.string.directions_route_not_available), Snackbar.LENGTH_SHORT).show();
            hideProgressDialog();
        } else if (isNetworkConnected(Objects.requireNonNull(getContext()))) {
            (new RouteAsyncTask(getContext(), this))
                    .execute(new ParametersRouteTask(ParametersRouteTask.ROUTE_ORIGIN, sourcePosition, nearestBikePosition));
            (new RouteAsyncTask(getContext(), this))
                    .execute(new ParametersRouteTask(ParametersRouteTask.ROUTE_BIKE, nearestBikePosition, nearestStandPosition));
            (new RouteAsyncTask(getContext(), this))
                    .execute(new ParametersRouteTask(ParametersRouteTask.ROUTE_DESTINATION, nearestStandPosition, destinationPosition));
            receivedBike = true;
            receivedOrigin = true;
            receivedDestination = true;
        }
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

    private void receivedAllRoute() {
        if (receivedOrigin && receivedBike && receivedDestination && !errorResponse) {
            // Check that the route was successfully obtained
            if (responseOrigin != null && responseBike != null && responseDestination != null) {
                ArrayList<String> responses = new ArrayList<>();
                responses.add(responseOrigin);
                responses.add(responseBike);
                responses.add(responseDestination);

                dataPassListener.passRouteToMap(responses, nearestBikeAddress, nearestStandAddress);
            }
        }
        // If no route was obtained then show a message saying so
        else {
            Snackbar.make(rootView, getString(R.string.directions_route_not_available), Snackbar.LENGTH_SHORT).show();
            errorResponse = false;
        }
        fabDirections.setEnabled(true);
        hideProgressDialog();
    }

    @Override
    public void receivedOriginRoute(String result) {
        receivedOrigin = true;
        responseOrigin = result;
        checkResponse(result);
        receivedAllRoute();
    }

    @Override
    public void receivedBikeRoute(String result) {
        receivedBike = true;
        responseBike = result;
        checkResponse(result);
        receivedAllRoute();
    }

    @Override
    public void receivedDestinationRoute(String result) {
        receivedDestination = true;
        responseDestination = result;
        checkResponse(result);
        receivedAllRoute();
    }

    private void checkResponse(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            boolean error = null != jsonObject.optString("error_message", null);
            if (error) {
                errorResponse = true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showProgressDialog() {
        progressDialog.show();
    }

    private void hideProgressDialog() {
        progressDialog.dismiss();
    }

}
