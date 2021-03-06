package disca.dadm.valenbike.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import disca.dadm.valenbike.R;
import disca.dadm.valenbike.adapters.RouteAdapter;
import disca.dadm.valenbike.database.ValenbikeSQLiteOpenHelper;
import disca.dadm.valenbike.interfaces.OnGeocoderTaskCompleted;
import disca.dadm.valenbike.interfaces.OnJourneyTaskCompleted;
import disca.dadm.valenbike.interfaces.OnStationTaskCompleted;
import disca.dadm.valenbike.models.ClusterStation;
import disca.dadm.valenbike.interfaces.DataPassListener;
import disca.dadm.valenbike.interfaces.OnPetitionTaskCompleted;
import disca.dadm.valenbike.models.Journey;
import disca.dadm.valenbike.models.ParametersGeocoderTask;
import disca.dadm.valenbike.models.Position;
import disca.dadm.valenbike.models.Route;
import disca.dadm.valenbike.models.Station;
import disca.dadm.valenbike.models.StationGUI;
import disca.dadm.valenbike.tasks.JourneyAsyncTask;
import disca.dadm.valenbike.tasks.PetitionAsyncTask;
import disca.dadm.valenbike.tasks.StationsAsyncTask;
import disca.dadm.valenbike.utils.MarkerClusterRenderer;

import static disca.dadm.valenbike.activities.MainActivity.CHANNEL_ID;
import static disca.dadm.valenbike.utils.Tools.coordinatesToAddress;
import static disca.dadm.valenbike.utils.Tools.getDialogProgressBar;
import static disca.dadm.valenbike.utils.Tools.getMarkerIconFromDrawable;
import static disca.dadm.valenbike.utils.Tools.getStations;
import static disca.dadm.valenbike.utils.Tools.isNetworkConnected;

public class MapFragment extends Fragment implements OnMapReadyCallback, OnPetitionTaskCompleted, OnGeocoderTaskCompleted, OnStationTaskCompleted, OnJourneyTaskCompleted {

    private final float CAMERA_ZOOM_STREET = 17;
    private final float CAMERA_ZOOM_CITY = 12;
    private final float CAMERA_ZOOM_NEIGHBORHOOD = 15;
    private final double POSITION_MARKER_SHEET = -0.001;
    private final String TAG_MARKER_SEARCH = "SEARCH";
    private final String TAG_MAP_TYPE = "MAP_TYPE";
    private final String TAG_CAMERA_POSITION = "CAMERA_POSITION";
    private final String TAG_ROUTE = "ROUTE";
    // Create a LatLngBounds that includes the ValenBisi locations with an edge.
    public static final LatLngBounds LIMIT_MAP = new LatLngBounds(
            new LatLng(39.414708, -0.480004), new LatLng(39.529877, -0.260633));
    private static final int ROUTE_MARKER = 0;
    public static final int ROUTE_STATION = 1;
    public static final String ROUTES_RESPONSES = "route_responses";
    public static final String NEAREST_BIKE = "nearest_bike";
    public static final String NEAREST_STAND = "nearest_station";
    public static final String CHECKBOX_ADD_ROUTE_CHECK = "checkbox_add_route_check";
    public static final String INSERTED_ROUTE = "inserted_route";
    public static final String ID_JOURNEY_DATABASE = "id_journey_database";
    private static int RECEIVED_STATIONS_NO = 0;
    private static int RECEIVED_STATIONS_YES = 1;

    // to avoid repetitions petitions to the api when comming from directions
    private int receivedStations = RECEIVED_STATIONS_NO;

    private View rootView;
    private GoogleMap map;
    private int mapType = GoogleMap.MAP_TYPE_NORMAL;
    private FloatingActionButton fabMapType, fabDirections, fabLocation, fabClear;
    private AutocompleteSupportFragment autocompleteSearch;
    private int autoCompleteSearchHeight;
    private PopupMenu popup;
    private MapView mapView;
    private MarkerOptions markerOptionsSearch;
    private CameraPosition cameraPosition;
    private boolean showIndicationsRoute = false;

    // route and direction
    private Marker markerSearch;
    private LatLng lastLocation, stationLatLng;
    private String addressLastLocation, addressSearch, addressStation;
    private boolean receivedMarkerAddress = true, receivedGPSAddress = true, receivedStationAddress = true;
    private int routeDirections = ROUTE_STATION;
    private boolean locationActive;
    private ArrayList<JSONObject> routeResponses = new ArrayList<>();
    private List<Polyline> polylinesRoutes = new ArrayList<>();
    private List<Marker> markerRoutes = new ArrayList<>();
    private BottomSheetDialog indicationsDialog;
    private ArrayList<String> stringResponses;

    // location attributes
    private FusedLocationProviderClient locationProviderClient;
    private MyGoogleLocationCallback callback;
    private LocationSource.OnLocationChangedListener locationChangedListener;
    private LocationRequest request;

    // loading progress
    private AlertDialog progressDialog;

    // request of stations api
    private boolean requestInProgress = false;
    // open direction fragment and pass objects
    private DataPassListener dataPassListener;

    private int idStationResponse;
    private NotificationManagerCompat notificationManagerCompat;
    private BottomSheetDialog dialog;
    private View dialogView;
    private Station station;
    private CheckBox reminder;
    private CheckBox favourite;
    private BottomNavigationView bottomNavigationView;
    private String nearestBikeAddress, nearestStandAddress;
    private boolean checkBoxAddRouteChecked, insertedRoute;
    private CheckBox checkBoxAddToHistory;
    private int idJourneyDatabase;

    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        fragment.setRetainInstance(true);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            markerOptionsSearch = savedInstanceState.getParcelable(TAG_MARKER_SEARCH);
            mapType = savedInstanceState.getInt(TAG_MAP_TYPE);
            cameraPosition = savedInstanceState.getParcelable(TAG_CAMERA_POSITION);
            stringResponses = savedInstanceState.getStringArrayList(TAG_ROUTE);
            nearestBikeAddress = savedInstanceState.getString(NEAREST_BIKE);
            nearestStandAddress = savedInstanceState.getString(NEAREST_STAND);
            checkBoxAddRouteChecked = savedInstanceState.getBoolean(CHECKBOX_ADD_ROUTE_CHECK);
            insertedRoute = savedInstanceState.getBoolean(INSERTED_ROUTE);
            idJourneyDatabase = savedInstanceState.getInt(ID_JOURNEY_DATABASE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDestroy();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) {
            mapView.onLowMemory();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_map, container, false);

        fabMapType = rootView.findViewById(R.id.fabMapType);
        fabDirections = rootView.findViewById(R.id.fabDirections);
        fabLocation = rootView.findViewById(R.id.fabLocation);
        fabClear = rootView.findViewById(R.id.fabClear);
        autocompleteSearch = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.autocompleteSearch);
        progressDialog = getDialogProgressBar(getContext()).create();
        mapView = rootView.findViewById(R.id.map);
        bottomNavigationView = container.getRootView().findViewById(R.id.bottomView);

        bottomNavigationView.setVisibility(View.GONE);
        initSearch();
        fabsListeners();
        createPopupMenu();
        initMapAndLocation(savedInstanceState);
        return rootView;
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (markerSearch != null) {
            outState.putParcelable(TAG_MARKER_SEARCH, markerOptionsSearch);
        }
        outState.putInt(TAG_MAP_TYPE, mapType);
        //outState.putParcelable(TAG_CAMERA_POSITION, map.getCameraPosition());
        outState.putStringArrayList(TAG_ROUTE, stringResponses);
        outState.putString(NEAREST_BIKE, nearestBikeAddress);
        outState.putString(NEAREST_STAND, nearestStandAddress);
        outState.putBoolean(CHECKBOX_ADD_ROUTE_CHECK, checkBoxAddRouteChecked);
        outState.putBoolean(INSERTED_ROUTE, insertedRoute);
        outState.putInt(ID_JOURNEY_DATABASE, idJourneyDatabase);
        super.onSaveInstanceState(outState);
        if (mapView != null) {
            mapView.onSaveInstanceState(outState);
        }
    }

    /**
     * Disables the location updates (if enabled).
     */
    @Override
    public void onStop() {
        super.onStop();
        // Check the location framework in use and disable
        disableLocation();
        if (mapView != null) {
            mapView.onStop();
        }
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

    @Override
    public void onStart() {
        super.onStart();

        if (mapView != null) {
            mapView.onStart();
        }

        Bundle args = getArguments();
        if (args != null) {
            // pass String to JSONOBject
            idStationResponse = args.getInt("station_response");

            if (idStationResponse == 0) {
                stringResponses = args.getStringArrayList(ROUTES_RESPONSES);
                nearestBikeAddress = args.getString(NEAREST_BIKE);
                nearestStandAddress = args.getString(NEAREST_STAND);
                showIndicationsRoute = true;
                insertedRoute = false;
            }
            routeDirections = ROUTE_MARKER;
        }
    }

    private void responseStringToJSON() {
        try {
            routeResponses = new ArrayList<>();
            if (stringResponses != null) {
                for (int i = 0; i < stringResponses.size(); i++) {
                    routeResponses.add(new JSONObject(stringResponses.get(i)));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showRoute() {
        // created bottom sheet modal with indications
        createdIndicationsRoute();
        // put a marker it start and final
        showMarkersRoutes();

        // show polyline route
        showBikeRoute();

    }

    private int getLocalDurationTimeRouteBy(JSONObject object, String mode) {
        int duration = 0;
        // Parse the response as a JSON object
        try {
            duration = object.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject(mode).getInt("value");
            return duration;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return duration;
    }

    private void showBikeRoute() {
        for (int i = 0; i < routeResponses.size(); i++) {
            String polyline = getPolylineRoute(routeResponses.get(i));
            setPolylineToMap(polyline, i % 2 == 1);
        }
    }

    private void setPolylineToMap(String polyline, boolean bike) {
        PolylineOptions options = new PolylineOptions().width(12).geodesic(true).clickable(true).addAll(PolyUtil.decode(polyline));
        if (bike) {
            options.color(getResources().getColor(R.color.bike_route, null));
        } else {
            options.color(getResources().getColor(R.color.walking_route, null));
        }
        polylinesRoutes.add(map.addPolyline(options));
    }

    private String getPolylineRoute(JSONObject object) {
        String polyline = null;
        try {
            // Get the array named "routes"
            JSONArray routesArray = object.getJSONArray("routes");
            // The first object of this array contains an "overview_polyline" object
            JSONObject route = routesArray.getJSONObject(0);

            polyline = route.getJSONObject("overview_polyline").getString("points");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return polyline;
    }

    private void showMarkersRoutes() {
        try {
            JSONObject startLocation = routeResponses.get(0).getJSONArray("routes")
                    .getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("start_location");

            LatLng startPosition = new LatLng(startLocation.getDouble("lat"), startLocation.getDouble("lng"));

            Drawable drawable = getResources().getDrawable(R.drawable.marker_a, null);
            BitmapDescriptor markerIcon = getMarkerIconFromDrawable(drawable);
            markerRoutes.add(map.addMarker(new MarkerOptions().position(startPosition).icon(markerIcon)));

            JSONObject endLocation = routeResponses.get(routeResponses.size() - 1)
                    .getJSONArray("routes").getJSONObject(0)
                    .getJSONArray("legs").getJSONObject(0).getJSONObject("end_location");
            LatLng endPosition = new LatLng(endLocation.getDouble("lat"), endLocation.getDouble("lng"));

            drawable = getResources().getDrawable(R.drawable.marker_b, null);
            markerIcon = getMarkerIconFromDrawable(drawable);
            markerRoutes.add(map.addMarker(new MarkerOptions().position(endPosition).icon(markerIcon)));

            // move camera and zoom due to distance route
            double latitude = (startPosition.latitude + endPosition.latitude) / 2;
            double longitude = (startPosition.longitude + endPosition.longitude) / 2;
            double difference = Math.pow(startPosition.latitude - endPosition.latitude, 2) + Math.pow(startPosition.longitude - endPosition.longitude, 2);
            float zoom = CAMERA_ZOOM_CITY;
            if (difference < 0.001) {
                zoom = CAMERA_ZOOM_NEIGHBORHOOD;
            }
            moveCamera(new LatLng(latitude, longitude), zoom);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void createdIndicationsRoute() {
        @SuppressLint("InflateParams") View dialogView = getLayoutInflater().inflate(R.layout.bottom_sheet_indications_route, null);
        indicationsDialog = new BottomSheetDialog(Objects.requireNonNull(getActivity()));

        setGeneralDataIndicationsRoutes(dialogView);

        initRouteDataRecycler(dialogView);

        indicationsDialog.setContentView(dialogView);
        if (showIndicationsRoute) {
            indicationsDialog.show();
        }
    }

    private void initRouteDataRecycler(View dialogView) {
        RecyclerView recyclerView = dialogView.findViewById(R.id.mapIndicationsRecyclerView);

        List<Route> routes = new ArrayList<>();
        for (int i = 0; i < routeResponses.size(); i++) {
            String address = getStartAddess(i);
            // foramted from seconds to minutes
            String duration = String.valueOf(getLocalDurationTimeRouteBy(routeResponses.get(i), "duration") / 60);
            int distance = getLocalDurationTimeRouteBy(routeResponses.get(i), "distance");
            // formatted distance to kilometers with one decimal
            double distanceKm = (double) distance / 1000;
            DecimalFormat dfDistance = new DecimalFormat("0.0");
            String formattedDistance = dfDistance.format(distanceKm);

            int mode = Route.MODE_WALK;
            if (i % 2 == 1) {
                mode = Route.MODE_BIKE;
            }
            // obtain steps
            JSONArray steps = null;
            try {

                steps = routeResponses.get(i)
                        .getJSONArray("routes").getJSONObject(0)
                        .getJSONArray("legs").getJSONObject(0)
                        .getJSONArray("steps");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Route route = new Route(mode, address, duration, formattedDistance, steps);
            routes.add(route);
        }
        RouteAdapter routeAdapter = new RouteAdapter(routes);
        recyclerView.setAdapter(routeAdapter);
    }


    private String getGloblalDurationRoute() {
        int durationJourney = 0;
        for (int i = 0; i < routeResponses.size(); i++) {
            durationJourney += getLocalDurationTimeRouteBy(routeResponses.get(i), "duration");
        }
        int durationMin = durationJourney / 60;
        return String.valueOf(durationMin);
    }

    private String getGlobalDistanceRoute() {
        int distanceJourney = 0;
        for (int i = 0; i < routeResponses.size(); i++) {
            distanceJourney += getLocalDurationTimeRouteBy(routeResponses.get(i), "distance");
        }
        // formatted distance to kilometers with one decimal
        double distanceKm = (double) distanceJourney / 1000;
        DecimalFormat dfDistance = new DecimalFormat("0.0");
        return dfDistance.format(distanceKm);

    }

    private void setGeneralDataIndicationsRoutes(View dialogView) {
        TextView source = dialogView.findViewById(R.id.indicationsSource);
        TextView destination = dialogView.findViewById(R.id.indicationsDestination);
        TextView durationText = dialogView.findViewById(R.id.indicationsDuration);
        TextView distanceText = dialogView.findViewById(R.id.indicationsDistance);
        TextView moneyText = dialogView.findViewById(R.id.tvMoneyBottomSheet);
        checkBoxAddToHistory = dialogView.findViewById(R.id.ibAddToHistory);

        indicationsDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (checkBoxAddToHistory.isChecked() && !insertedRoute) {
                    addRouteToHistoryDatabase();
                    insertedRoute = true;
                    Snackbar.make(rootView, getString(R.string.route_added_database), Snackbar.LENGTH_SHORT).show();
                } else if (!checkBoxAddToHistory.isChecked() && insertedRoute) {
                    JourneyAsyncTask journeyAsyncTask = new JourneyAsyncTask(getContext(), MapFragment.this);
                    journeyAsyncTask.execute(JourneyAsyncTask.REMOVE_JOURNEY, String.valueOf(idJourneyDatabase));
                    insertedRoute = false;
                    Snackbar.make(rootView, getString(R.string.route_delete_database), Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        putBinIconRed();

        checkBoxAddToHistory.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    checkBoxAddToHistory.setBackground(getResources().getDrawable(R.drawable.ic_add_circle_outline_accent_24dp, null));
                } else {
                    checkBoxAddToHistory.setBackground(getResources().getDrawable(R.drawable.ic_delete_red_24dp, null));
                }
                checkBoxAddRouteChecked = isChecked;
            }
        });

        // calculate duration in minutes and distance in kilometers
        durationText.setText(getGloblalDurationRoute());
        distanceText.setText(getGlobalDistanceRoute());
        try {
            // obtain start address
            source.setText(getStartAddess(0));

            // obtain end address
            String endAddress = routeResponses.get(routeResponses.size() - 1).getJSONArray("routes")
                    .getJSONObject(0).getJSONArray("legs").getJSONObject(0).getString("end_address");
            destination.setText(endAddress);

            int duration = routeResponses.get(1).getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("duration").getInt("value");
            double durationToMinuts = duration / 60.0;
            double price = 0;
            if (durationToMinuts > 30) {
                price += 0.52;
                durationToMinuts -= 60;
                while (durationToMinuts > 0) {
                    price += 2.08;
                    durationToMinuts -= 60;
                }
            }
            moneyText.setText(String.valueOf(price));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addRouteToHistoryDatabase() {
        try {
            JSONObject object = routeResponses.get(1);
            int distance = object.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("distance").getInt("value");
            double distanceKm = (double) distance / 1000;
            DecimalFormat dfDistance = new DecimalFormat("0.0");
            distanceKm = Double.parseDouble(dfDistance.format(distanceKm).replace(",", "."));
            int duration = object.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("duration").getInt("value");
            double durationToMinuts = duration / 60.0;
            int durationDatabase = (int) durationToMinuts;
            double price = 0;
            if (durationToMinuts > 30) {
                price += 0.52;
                durationToMinuts -= 60;
                while (durationToMinuts > 0) {
                    price += 2.08;
                    durationToMinuts -= 60;
                }
            }
            java.text.DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", new Locale("es_ES"));
            String dateDatabase = dateFormat.format(new Date());
            JourneyAsyncTask journeyAsyncTask = new JourneyAsyncTask(getContext(), this);
            journeyAsyncTask.execute(JourneyAsyncTask.INSERT_JOURNEY, nearestBikeAddress, nearestStandAddress,
                    String.valueOf(distanceKm), String.valueOf(price), String.valueOf(durationDatabase), dateDatabase);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getStartAddess(int indexResponse) {
        String startAddress = "";
        // obtain start address
        try {

            startAddress = routeResponses.get(indexResponse).getJSONArray("routes")
                    .getJSONObject(0).getJSONArray("legs").getJSONObject(0).getString("start_address");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return startAddress;
    }

    public void setDataPassListener(DataPassListener callback) {
        this.dataPassListener = callback;
    }

    private void fabsListeners() {
        fabMapType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.show();
            }
        });
        fabDirections.setEnabled(false);
        fabDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                routeDirections = ROUTE_MARKER;
                requestAddressDirections();
            }
        });

        fabLocation.hide();
        fabLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastLocation != null) {
                    if (checkLimitBounds(lastLocation)) {
                        moveCamera(lastLocation, CAMERA_ZOOM_STREET);
                    } else {
                        Snackbar.make(rootView, getString(R.string.map_location_out_bounds), Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
        });

        fabClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearMap();
            }
        });
    }

    private void clearMap() {
        autocompleteSearch.setText("");
        if (markerSearch != null) {
            markerSearch.remove();
        }
        for (int i = 0; i < markerRoutes.size(); i++) {
            markerRoutes.get(i).remove();
        }
        markerRoutes.clear();
        for (int i = 0; i < polylinesRoutes.size(); i++) {
            polylinesRoutes.get(i).remove();
        }
        polylinesRoutes.clear();
        if (stringResponses != null) {
            stringResponses.clear();
        }
        autocompleteSearch.setText("");
        nearestBikeAddress = null;
        nearestStandAddress = null;
        checkBoxAddRouteChecked = false;
        insertedRoute = false;
        idJourneyDatabase = 0;
    }

    private void initMapAndLocation(Bundle savedInstanceState) {
        //Configure Mapview and sync to google map.
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        mapView.onResume();

        callback = new MyGoogleLocationCallback();
        locationProviderClient = LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(getActivity()));

        request = new LocationRequest();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(3000);
        request.setFastestInterval(2000);

        checkLocationPermissions();
    }

    /**
     * Checks that permissions are granted for the selected location framework.
     */
    private void checkLocationPermissions() {
        if (isLocationPermissionGranted(true)) {
            locationProviderClient.requestLocationUpdates(request, callback, null);
        }
    }

    /**
     * Checks that permissions are granted for the selected location framework
     */
    private boolean isLocationPermissionGranted(boolean request) {

        // Determine whether the user has granted that particular permission
        if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.ACCESS_FINE_LOCATION)) {
            return true;
        }
        // If not, display an activity to request that permission
        else if (request) {
            requestPermissions((new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION}), LocationRequest.PRIORITY_HIGH_ACCURACY);
        }
        return false;
    }

    /**
     * This callback is executed whenever the user has been asked to grant permissions.
     * In this case it will deal with permission to request fine/coarse location updates,
     * and to remove any previous requested location update.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // Check whether any permission has been granted
        if ((grantResults.length > 0) && (PackageManager.PERMISSION_GRANTED == grantResults[0])) {
            locationProviderClient.requestLocationUpdates(request, callback, null);
        }
        // Notify the user that permission were not granted
        else {
            Snackbar.make(rootView, getString(R.string.permissions_not_granted), Snackbar.LENGTH_SHORT).show();
        }
    }

    /**
     * Tries to disable location updates for the selected location framework.
     */
    private void disableLocation() {
        if (isLocationPermissionGranted(false)) {
            locationProviderClient.removeLocationUpdates(callback);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;

        // request for stations of valenbisi
        if (receivedStations == RECEIVED_STATIONS_NO) {
            requestStations(0);
        }
        responseStringToJSON();
        // show route if available
        if (routeResponses.size() != 0) {
            showRoute();
        }

        restoreStateMapFragment();
        // Constrain the camera target to the Valencia bounds.
        map.setLatLngBoundsForCameraTarget(LIMIT_MAP);
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.getUiSettings().setIndoorLevelPickerEnabled(false);
        map.getUiSettings().setMapToolbarEnabled(false);
        map.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {
                if (indicationsDialog != null) {
                    putBinIconRed();
                    indicationsDialog.show();
                }
            }
        });
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if (checkLimitBounds(latLng)) {
                    setMarkerSearch(latLng);
                } else {
                    Snackbar.make(rootView, getString(R.string.map_marker_out_bounds), Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.getTitle() == null) {
                    // group of stations
                    LatLng position = marker.getPosition();
                    float zoom = map.getCameraPosition().zoom + 2;
                    moveCamera(position, zoom);
                } else if (marker.getTitle().equals(TAG_MARKER_SEARCH)) {
                    // search marker or long click
                    markerSearch.remove();
                    autocompleteSearch.setText("");
                    markerSearch = null;
                } else if (!requestInProgress) {
                    // station
                    requestStations(Integer.parseInt(marker.getTitle()));
                }
                return true;
            }
        });
        map.setLocationSource(new LocationSource() {
            @Override
            public void activate(OnLocationChangedListener onLocationChangedListener) {
                locationChangedListener = onLocationChangedListener;
            }

            @Override
            public void deactivate() {
                locationChangedListener = null;
            }
        });

        // Know height of autocompleteSearch & update height
        autocompleteSearch.getView().getViewTreeObserver().addOnWindowFocusChangeListener(new ViewTreeObserver.OnWindowFocusChangeListener() {
            @Override
            public void onWindowFocusChanged(boolean hasFocus) {
                if (autocompleteSearch.getView() != null) {
                    autoCompleteSearchHeight = autocompleteSearch.getView().getHeight();
                    map.setPadding(0, autoCompleteSearchHeight + 20, 0, 0);
                }
            }
        });

        if (idStationResponse != 0) {
            requestStations(idStationResponse);
        }
    }

    private void restoreStateMapFragment() {
        // restore markerSearch
        if (routeDirections == ROUTE_STATION && markerOptionsSearch != null) {
            setMarkerSearch(markerOptionsSearch.getPosition());
        }
        // restore map type
        if (mapType != GoogleMap.MAP_TYPE_NORMAL) {
            map.setMapType(mapType);
        }
        // restore camera position
        if (cameraPosition != null && !showIndicationsRoute) {
            map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    private boolean checkLimitBounds(LatLng latLng) {
        return LIMIT_MAP.southwest.longitude < latLng.longitude && latLng.longitude < LIMIT_MAP.northeast.longitude &&
                LIMIT_MAP.southwest.latitude < latLng.latitude && latLng.latitude < LIMIT_MAP.northeast.latitude;
    }

    @Override
    public void receivedAddressGPS(String address) {
        receivedGPSAddress = true;
        addressLastLocation = address;
        openDirections();
    }

    @Override
    public void receivedAddressMarker(String address) {
        receivedMarkerAddress = true;
        addressSearch = address;
        openDirections();
    }

    @Override
    public void receivedAddressStation(String address) {
        receivedStationAddress = true;
        addressStation = address;
        openDirections();
    }

    private void openDirections() {
        if (receivedStationAddress && receivedMarkerAddress && receivedGPSAddress) {
            LatLng position = null;
            String address = "";
            if (routeDirections == ROUTE_MARKER) {
                if (markerSearch != null) {
                    position = markerSearch.getPosition();
                    address = addressSearch;
                }
            } else if (routeDirections == ROUTE_STATION) {
                position = stationLatLng;
                address = addressStation;
            }
            // open directions fragment and pass location if active, and marker as a destination
            LatLng locationPos = null;
            String locationAdd = "";
            LatLng destinationPos = null;
            String destinationAdd = "";
            if (locationActive && lastLocation != null) {
                if (checkLimitBounds(lastLocation)) {
                    locationPos = lastLocation;
                    locationAdd = addressLastLocation;
                } else {
                    Snackbar.make(rootView, getString(R.string.map_location_out_bounds), Snackbar.LENGTH_SHORT).show();
                }
            }
            if (position != null) {
                destinationPos = position;
                destinationAdd = address;
            }
            bottomNavigationView.setVisibility(View.GONE);
            dataPassListener.passLocationToDirection(locationPos, locationAdd, destinationPos, destinationAdd);
        }
        hideProgressDialog();
    }

    private void requestAddressDirections() {
        showProgressDialog();
        if (routeDirections == ROUTE_MARKER) {
            if (markerSearch != null) {
                receivedMarkerAddress = false;
                coordinatesToAddress(getContext(), this, ParametersGeocoderTask.LOCATION_MARKER, markerSearch.getPosition());
            }
        } else {
            receivedStationAddress = false;
            coordinatesToAddress(getContext(), this, ParametersGeocoderTask.LOCATION_STATION, stationLatLng);
        }
        if (locationActive && lastLocation != null) {
            receivedGPSAddress = false;
            coordinatesToAddress(getContext(), this, ParametersGeocoderTask.LOCATION_GPS, lastLocation);
        }

        if (receivedStationAddress && receivedMarkerAddress && receivedGPSAddress) {
            openDirections();
        }
    }

    private void moveCamera(LatLng position, float zoom) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(position, zoom));
    }

    private void setMarkerSearch(LatLng latLng) {
        if (markerSearch != null) {
            markerSearch.remove();
        }
        markerOptionsSearch = new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)).title(TAG_MARKER_SEARCH);
        markerSearch = map.addMarker(markerOptionsSearch);
    }

    /**
     * Updates the user interface to display the new latitude and longitude.
     * It will also start an asynchronous task to translate those coordinates into a human readable address.
     */
    private void updateUI(Location location) {
        lastLocation = new LatLng(location.getLatitude(), location.getLongitude());

        if (locationChangedListener != null) {
            locationChangedListener.onLocationChanged(location);
        }
    }

    private void changeMapType(int mapType) {
        if (this.mapType != mapType) {
            this.mapType = mapType;
            map.setMapType(mapType);
        }
    }

    private void createPopupMenu() {
        //Creating the instance of PopupMenu
        popup = new PopupMenu(getActivity(), rootView.findViewById(R.id.fabMapType));
        // to inflate the menu resource (defined in XML) into the PopupMenu
        popup.getMenuInflater().inflate(R.menu.map_type, popup.getMenu());
        // when restore state check the correct radio button
        if (mapType != GoogleMap.MAP_TYPE_NORMAL) {
            int itemId = mapType == GoogleMap.MAP_TYPE_SATELLITE ? R.id.map_type_satellite : R.id.map_type_terrain;
            popup.getMenu().findItem(itemId).setChecked(true);
        }
        //popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                item.setChecked(true);
                switch (item.getItemId()) {
                    case R.id.map_type_traffic:
                        changeMapType(GoogleMap.MAP_TYPE_NORMAL);
                        break;
                    case R.id.map_type_satellite:
                        changeMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        break;
                    case R.id.map_type_terrain:
                        changeMapType(GoogleMap.MAP_TYPE_TERRAIN);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    private void initSearch() {
        Places.initialize(Objects.requireNonNull(getContext()), getString(R.string.google_maps_key));

        // Specify the types of place data to return, country and limit in the map
        autocompleteSearch.setPlaceFields(Arrays.asList(Place.Field.ADDRESS, Place.Field.NAME, Place.Field.LAT_LNG));
        autocompleteSearch.setCountry("ES");
        autocompleteSearch.setLocationRestriction(RectangularBounds.newInstance(LIMIT_MAP));
        autocompleteSearch.setHint(getString(R.string.map_search_hint));
        autocompleteSearch.setText("");

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteSearch.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                setMarkerSearch(place.getLatLng());
                moveCamera(place.getLatLng(), CAMERA_ZOOM_STREET);
                addressSearch = place.getAddress();
            }

            @Override
            public void onError(@NonNull Status status) {
                Snackbar.make(rootView, getString(R.string.places_error_search), Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void requestStations(int number) {
        if (isNetworkConnected(Objects.requireNonNull(getContext()))) {
            showProgressDialog();
            requestInProgress = true;
            PetitionAsyncTask petitionAsyncTask = new PetitionAsyncTask(getContext(), this);
            petitionAsyncTask.execute(number);
        } else {
            bottomNavigationView.setVisibility(View.VISIBLE);
        }
    }

    private void requestDone() {
        requestInProgress = false;
        hideProgressDialog();
        if (idStationResponse == 0) {
            bottomNavigationView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void receivedAllStations(List<Station> stations) {
        receivedStations = RECEIVED_STATIONS_YES;
        /*todo remove this assignment*/
        stations = getStations();

        ClusterManager<ClusterStation> clusterManager = new ClusterManager<>(Objects.requireNonNull(getActivity()), map);
        clusterManager.setRenderer(new MarkerClusterRenderer(getActivity(), map, clusterManager));
        map.setOnCameraIdleListener(clusterManager);
        List<ClusterStation> items = new ArrayList<>();
        for (int i = 0; i < stations.size(); i++) {
            Station station = stations.get(i);
            int number = station.getNumber();
            LatLng latLng = new LatLng(station.getPosition().getLat(), station.getPosition().getLng());
            boolean freeBikes = station.getAvailableBikes() > 0;
            boolean active = station.getStatus().equals("OPEN");
            ClusterStation clusterStation = new ClusterStation(number, latLng, freeBikes, active);
            items.add(clusterStation);
        }
        clusterManager.addItems(items);
        clusterManager.cluster();
        requestDone();
    }

    @Override
    public void receivedStation(Station station) {
        dialogView = getLayoutInflater().inflate(R.layout.bottom_sheet_marker, null);
        dialog = new BottomSheetDialog(Objects.requireNonNull(getActivity()));
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                bottomNavigationView.setVisibility(View.VISIBLE);
            }
        });
        this.station = station;
        StationsAsyncTask stationsAsyncTask = new StationsAsyncTask(getContext(), this);
        stationsAsyncTask.execute(StationsAsyncTask.GET_STATION, station.getNumber());
    }

    private void initDialogStation(final BottomSheetDialog dialog, final View dialogView, final Station station, final StationGUI stationDatabase) {
        TextView numberStation = dialogView.findViewById(R.id.sheetNumberStation);
        final TextView address = dialogView.findViewById(R.id.sheetAddress);
        TextView bikes = dialogView.findViewById(R.id.sheetAvailableBikes);
        final TextView stands = dialogView.findViewById(R.id.sheetAvailableStands);
        TextView lastUpdate = dialogView.findViewById(R.id.sheetLastUpdate);
        ImageView payment = dialogView.findViewById(R.id.sheetPayment);
        reminder = dialogView.findViewById(R.id.sheetReminder);
        favourite = dialogView.findViewById(R.id.sheetFavourite);
        Button directions = dialogView.findViewById(R.id.sheetDirections);
        LinearLayout layoutInfo = dialogView.findViewById(R.id.sheetLayoutInfo);
        LinearLayout layoutClosed = dialogView.findViewById(R.id.sheetLayoutClosed);
        LinearLayout layoutDirections = dialogView.findViewById(R.id.sheetLayoutDirections);

        numberStation.setText(String.valueOf(station.getNumber()));
        address.setText(station.getAddress());

        if (station.isActive()) {

            bikes.setText(String.valueOf(station.getAvailableBikes()));
            stands.setText(String.valueOf(station.getAvailableBikeStands()));

            directions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    stationLatLng = new LatLng(station.getPosition().getLat(), station.getPosition().getLng());
                    routeDirections = ROUTE_STATION;
                    requestAddressDirections();
                }
            });

            if (station.getAvailableBikes() == 0) {
                reminder.setVisibility(View.VISIBLE);
                if (stationDatabase.isReminderCheck()) {
                    reminder.setChecked(true);
                    reminder.setBackground(getResources().getDrawable(R.drawable.ic_notifications_active_golden_24dp, null));
                }

                reminder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            reminder.setBackground(getResources().getDrawable(R.drawable.ic_notifications_active_golden_24dp, null));

                            Notification notification = new NotificationCompat.Builder(getContext(), CHANNEL_ID)
                                    .setSmallIcon(R.drawable.ic_notification)
                                    .setContentTitle("¡Ya hay bicis disponibles!")
                                    .setContentText("En " + station.getAddress() + " ya hay bicis disponibles")
                                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                                    .setCategory(NotificationCompat.CATEGORY_REMINDER)
                                    .build();
                            notificationManagerCompat = NotificationManagerCompat.from(getContext());
                            notificationManagerCompat.notify(1, notification);
                        } else {
                            reminder.setBackground(getResources().getDrawable(R.drawable.ic_notifications_none_golden_24dp, null));
                        }
                    }
                });
            }
        } else {
            layoutInfo.setVisibility(View.GONE);
            layoutClosed.setVisibility(View.VISIBLE);
            layoutDirections.setVisibility(View.GONE);
            reminder.setVisibility(View.INVISIBLE);
        }

        long now = new Date().getTime();
        String dateString = DateFormat.format("HH:mm", new Date(now - station.getLastUpdate())).toString();
        lastUpdate.setText(dateString);

        if (!station.getBanking()) {
            payment.setVisibility(View.INVISIBLE);
        }

        if (stationDatabase.isFavouriteCheck()) {
            favourite.setChecked(true);
            favourite.setBackground(getResources().getDrawable(R.drawable.ic_favorite_magenta_24dp, null));
        }

        favourite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    favourite.setBackground(getResources().getDrawable(R.drawable.ic_favorite_magenta_24dp, null));
                } else {
                    favourite.setBackground(getResources().getDrawable(R.drawable.ic_favorite_border_magenta_24dp, null));
                }
            }
        });

    }

    private void showProgressDialog() {
        progressDialog.show();
    }

    private void hideProgressDialog() {
        progressDialog.dismiss();
        fabDirections.setEnabled(true);
    }

    @Override
    public void responseStationDatabase(final List<StationGUI> list) {
        if (list != null) {
            initDialogStation(dialog, dialogView, station, list.get(0));
            dialog.setContentView(dialogView);
            LatLng position = new LatLng(station.getPosition().getLat() + POSITION_MARKER_SHEET, station.getPosition().getLng());
            moveCamera(position, CAMERA_ZOOM_STREET);
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    StationsAsyncTask stationsAsyncTask = new StationsAsyncTask(getContext(), MapFragment.this);
                    stationsAsyncTask.execute(StationsAsyncTask.INSERT_STATION, list.get(0).getNumber(),
                            favourite.isChecked() ? ValenbikeSQLiteOpenHelper.ENABLED_FAVREMIND : ValenbikeSQLiteOpenHelper.DISABLED_FAVREMIND,
                            reminder.isChecked() ? ValenbikeSQLiteOpenHelper.ENABLED_FAVREMIND : ValenbikeSQLiteOpenHelper.DISABLED_FAVREMIND);
                }
            });
            dialog.show();
        }
        requestDone();
    }

    @Override
    public void responseJourneyDatabase(List<Journey> list) {
        if (list != null && list.size() == 1) {
            idJourneyDatabase = list.get(0).getId();
        }
    }

    public void putBinIconRed() {
        if (insertedRoute) {
            checkBoxAddToHistory.setChecked(true);
            checkBoxAddToHistory.setBackground(getResources().getDrawable(R.drawable.ic_delete_red_24dp, null));
        }
    }

    private class MyGoogleLocationCallback extends LocationCallback {

        /**
         * This callback is executed whenever a new location update is received.
         */
        @Override
        public void onLocationResult(LocationResult locationResult) {
            // Update the user interface
            updateUI(locationResult.getLocations().get(0));
        }

        @Override
        public void onLocationAvailability(LocationAvailability locationAvailability) {
            super.onLocationAvailability(locationAvailability);
            map.setMyLocationEnabled(locationAvailability.isLocationAvailable());
            locationActive = locationAvailability.isLocationAvailable();
            if (locationAvailability.isLocationAvailable()) {
                fabLocation.show();
            } else {
                fabLocation.hide();
            }
        }
    }
}