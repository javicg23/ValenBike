package disca.dadm.valenbike.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import disca.dadm.valenbike.R;
import disca.dadm.valenbike.interfaces.OnGeocoderTaskCompleted;
import disca.dadm.valenbike.models.ClusterStation;
import disca.dadm.valenbike.interfaces.DataPassListener;
import disca.dadm.valenbike.interfaces.OnPetitionTaskCompleted;
import disca.dadm.valenbike.models.ParametersGeocoderTask;
import disca.dadm.valenbike.models.Station;
import disca.dadm.valenbike.tasks.GeocoderAsyncTask;
import disca.dadm.valenbike.tasks.PetitionAsyncTask;
import disca.dadm.valenbike.utils.MarkerClusterRenderer;

import static disca.dadm.valenbike.utils.Tools.getStations;
import static disca.dadm.valenbike.utils.Tools.isNetworkConnected;
import static disca.dadm.valenbike.utils.Tools.showSnackBar;

public class MapFragment extends Fragment implements OnMapReadyCallback, OnPetitionTaskCompleted, OnGeocoderTaskCompleted {

    private final float CAMERA_ZOOM_STREET = 17;
    private final double POSITION_MARKER_SHEET = - 0.001;
    private final String TITLE_SEARCH = "SEARCH";
    // Create a LatLngBounds that includes the ValenBisi locations with an edge.
    public static  final LatLngBounds LIMIT_MAP = new LatLngBounds(
            new LatLng(39.414708, -0.480004), new LatLng(39.529877,  -0.260633));
    public final int ROUTE_MARKER = 0;
    public final int ROUTE_NONE = 1;
    public final int ROUTE_STATION = 2;

    private View rootView;
    private GoogleMap map;
    private int mapType = GoogleMap.MAP_TYPE_NORMAL;
    private FloatingActionButton fabMapType, fabDirections, fabLocation;
    private AutocompleteSupportFragment autocompleteSearch;
    private PopupMenu popup;
    // route and direction
    private Marker markerSearch;
    private LatLng lastLocation, stationLatLng;
    private String addressLastLocation, addressSearch, addressStation;
    private boolean receivedMarkerAddress = true, receivedGPSAddress = true, receivedStationAddress = true;
    private int routeDirections = ROUTE_NONE;
    private boolean locationActive;

    // location attributes
    private FusedLocationProviderClient locationProviderClient;
    private MyGoogleLocationCallback callback;
    private LocationSource.OnLocationChangedListener locationChangedListener;
    private LocationRequest request;

    // request of stations api
    private boolean requestInProgress = false;
    // open direction fragment and pass objects
    private DataPassListener dataPassListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_map, container, false);

        fabMapType = rootView.findViewById(R.id.fabMapType);
        fabDirections = rootView.findViewById(R.id.fabDirections);
        fabLocation = rootView.findViewById(R.id.fabLocation);
        autocompleteSearch = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.autocompleteSearch);

        initSearch();
        fabsListeners();
        createPopupMenu();
        initMapAndLocation();

        return rootView;
    }


    /**
     * Disables the location updates (if enabled).
     */
    @Override
    public void onStop() {
        super.onStop();
        // Check the location framework in use and disable
        disableLocation();
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);
        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try
        {
            dataPassListener = (DataPassListener) context;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(context.toString()+ " must implement DataPassListener");
        }
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

        fabDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                routeDirections = ROUTE_MARKER;
                requestAddressDirections();
            }
        });

        fabLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastLocation != null) {
                    if (checkLimitBounds(lastLocation)) {
                        moveCamera(lastLocation, CAMERA_ZOOM_STREET);
                    } else {
                        showSnackBar(rootView, true, getString(R.string.map_location_out_bounds));
                    }
                }
            }
        });
    }

    private void initMapAndLocation() {
        //Configure Mapview and sync to google map.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        callback = new MyGoogleLocationCallback();
        locationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

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
        if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
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
            showSnackBar(rootView, true, getString(R.string.permissions_not_granted));
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
        requestStations(0);
        // Constrain the camera target to the Valencia bounds.
        map.setLatLngBoundsForCameraTarget(LIMIT_MAP);
        /*TODO  cambiarlo para que sea dinamico, es decir, que dependa de la altura del buscador y los elementos
        *  en vez de meterlo directmanete en numero fijo*/
        map.setPadding(0,170,0,0);
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.getUiSettings().setIndoorLevelPickerEnabled(false);
        map.getUiSettings().setMapToolbarEnabled(false);
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if (checkLimitBounds(latLng)) {
                    setMarkerSearch(latLng);
                } else {
                    showSnackBar(rootView, true, getString(R.string.map_marker_out_bounds));
                }
            }
        });
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.getTitle() == null){
                    // group of stations
                    LatLng position = marker.getPosition();
                    float zoom = map.getCameraPosition().zoom + 2;
                    moveCamera(position, zoom);
                } else if (marker.getTitle().equals(TITLE_SEARCH)){
                    // search marker or long click
                    markerSearch.remove();
                    autocompleteSearch.setText("");
                    markerSearch = null;
                } else if (!requestInProgress){
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

    // depends on the location put the res in addressLastLocation or addressSearch
    private void coordinatesToAddress(int location, LatLng latLng) {
        // Start asynchronous task to translate coordinates into an address
        if (isNetworkConnected(getContext())) {
            (new GeocoderAsyncTask(getContext(), MapFragment.this)).execute(new ParametersGeocoderTask(location, latLng.latitude, latLng.longitude));
        }
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
                locationPos = lastLocation;
                locationAdd = addressLastLocation;
            }
            if (position != null) {
                destinationPos = position;
                destinationAdd = address;
            }
            dataPassListener.passLocationToDirection(routeDirections, locationPos, locationAdd, destinationPos, destinationAdd);
            routeDirections = ROUTE_NONE;
        }
    }

    private void requestAddressDirections() {
        if (routeDirections == ROUTE_MARKER) {
            if (markerSearch != null) {
                receivedMarkerAddress = false;
                coordinatesToAddress(ParametersGeocoderTask.LOCATION_MARKER, markerSearch.getPosition());
            }
        } else {
            receivedStationAddress = false;
            coordinatesToAddress(ParametersGeocoderTask.LOCATION_STATION, stationLatLng);
        }
        if (locationActive && lastLocation != null) {
            receivedGPSAddress = false;
            coordinatesToAddress(ParametersGeocoderTask.LOCATION_GPS, lastLocation);
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
        markerSearch = map.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)).title(TITLE_SEARCH));
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
        if (this.mapType != mapType){
            this.mapType = mapType;
            map.setMapType(mapType);
        }
    }

    private void createPopupMenu() {
        //Creating the instance of PopupMenu
        popup = new PopupMenu(getActivity(), rootView.findViewById(R.id.fabMapType));
        // to inflate the menu resource (defined in XML) into the PopupMenu
        popup.getMenuInflater().inflate(R.menu.map_type, popup.getMenu());
        //popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                item.setChecked(true);
                switch (item.getItemId()){
                    case R.id.map_type_traffic :
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
        Places.initialize(getContext(), getString(R.string.google_maps_key));

        // Specify the types of place data to return, country and limit in the map
        autocompleteSearch.setPlaceFields(Arrays.asList(Place.Field.ADDRESS, Place.Field.NAME, Place.Field.LAT_LNG));
        autocompleteSearch.setCountry("ES");
        autocompleteSearch.setLocationRestriction(RectangularBounds.newInstance(LIMIT_MAP));

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
                showSnackBar(rootView, true, getString(R.string.places_search_error));
            }
        });

    }

    private void requestStations(int number) {
        if (isNetworkConnected(getContext())){
            requestInProgress = true;
            PetitionAsyncTask petitionAsyncTask = new PetitionAsyncTask(getContext(),this);
            petitionAsyncTask.execute(number);
        }
    }

    private void requestDone() {
        requestInProgress = false;
    }

    @Override
    public void receivedAllStations(List<Station> stations) {
        /*todo remove this assignment*/
        stations = getStations();

        // to avoid cluster repetitions
        map.clear();
        // if exist marker added again
        if (markerSearch != null) {
            setMarkerSearch(markerSearch.getPosition());
        }

        ClusterManager<ClusterStation> clusterManager = new ClusterManager<>(getActivity(), map);
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
        View dialogView = getLayoutInflater().inflate(R.layout.bottom_sheet_marker, null);
        BottomSheetDialog dialog = new BottomSheetDialog(getActivity());
        initDialogStation(dialog, dialogView, station);
        dialog.setContentView(dialogView);
        LatLng position = new LatLng(station.getPosition().getLat() + POSITION_MARKER_SHEET, station.getPosition().getLng());
        moveCamera(position, CAMERA_ZOOM_STREET);
        dialog.show();
        requestDone();
    }

    private void initDialogStation(final BottomSheetDialog dialog, final View dialogView, final Station station) {
        TextView numberStation = dialogView.findViewById(R.id.sheetNumberStation);
        TextView address = dialogView.findViewById(R.id.sheetAddress);
        TextView bikes = dialogView.findViewById(R.id.sheetAvailableBikes);
        final TextView stands = dialogView.findViewById(R.id.sheetAvailableStands);
        TextView lastUpdate = dialogView.findViewById(R.id.sheetLastUpdate);
        ImageView payment = dialogView.findViewById(R.id.sheetPayment);
        final CheckBox reminder = dialogView.findViewById(R.id.sheetReminder);
        final CheckBox favourite = dialogView.findViewById(R.id.sheetFavourite);
        Button directions = dialogView.findViewById(R.id.sheetDirections);
        LinearLayout layoutInfo = dialogView.findViewById(R.id.sheetLayoutInfo);
        LinearLayout layoutClosed = dialogView.findViewById(R.id.sheetLayoutClosed);
        LinearLayout layoutDirections = dialogView.findViewById(R.id.sheetLayoutDirections);

        numberStation.setText(String.valueOf(station.getNumber()));
        address.setText(station.getAddress());

        /*todo change this to if(station.isActive()), it's because there are all stations closed*/
        if (!station.isActive()){
            bikes.setText(String.valueOf(station.getAvailableBikes()));
            stands.setText(String.valueOf(station.getAvailableBikeStands()));

            directions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.hide();
                    stationLatLng = new LatLng(station.getPosition().getLat(), station.getPosition().getLng());
                    routeDirections = ROUTE_STATION;
                    requestAddressDirections();
                }
            });

            reminder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked){
                        reminder.setBackground(getResources().getDrawable(R.drawable.ic_notifications_active_golden_24dp, null));
                        Toast.makeText(getActivity(),"reminder true",Toast.LENGTH_SHORT).show();
                    } else {
                        reminder.setBackground(getResources().getDrawable(R.drawable.ic_notifications_none_golden_24dp, null));
                        Toast.makeText(getActivity(),"reminder false",Toast.LENGTH_SHORT).show();
                    }
                }
            });
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

        favourite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    favourite.setBackground(getResources().getDrawable(R.drawable.ic_favorite_magenta_24dp, null));
                    Toast.makeText(getActivity(),"favourite true",Toast.LENGTH_SHORT).show();
                } else {
                    favourite.setBackground(getResources().getDrawable(R.drawable.ic_favorite_border_magenta_24dp, null));
                    Toast.makeText(getActivity(),"favourite false",Toast.LENGTH_SHORT).show();
                }
            }
        });

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
            if (locationAvailability.isLocationAvailable()){
                fabLocation.show();
            } else {
                fabLocation.hide();
            }
        }
    }
}
