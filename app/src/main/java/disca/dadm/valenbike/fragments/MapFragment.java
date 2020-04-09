package disca.dadm.valenbike.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.maps.android.clustering.ClusterManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import disca.dadm.valenbike.R;
import disca.dadm.valenbike.models.ClusterStation;
import disca.dadm.valenbike.tasks.OnTaskCompleted;
import disca.dadm.valenbike.models.Station;
import disca.dadm.valenbike.tasks.PetitionAsyncTask;
import disca.dadm.valenbike.utils.MarkerClusterRenderer;

import static disca.dadm.valenbike.utils.Tools.showSnackBar;

public class MapFragment extends Fragment implements OnMapReadyCallback, OnTaskCompleted {

    private final float CAMERA_ZOOM_STREET = 17;
    private final double POSITION_MARKER_SHEET = - 0.001;
    private LatLngBounds LIMIT_MAP = new LatLngBounds(
            new LatLng(39.354547, -0.574788),new LatLng(39.583997, -0.169773));

    private View rootView;
    private GoogleMap map;
    private SearchView searchView;
    private FloatingActionButton fabMapType, fabDirections, fabLocation;
    // Create a LatLngBounds that includes the ValenBisi locations with an edge.
    private Marker markerSearch;
    private Location lastLocation;
    private int mapType = GoogleMap.MAP_TYPE_NORMAL;
    private FusedLocationProviderClient locationProviderClient;
    private MyGoogleLocationCallback callback;
    private LocationSource.OnLocationChangedListener locationChangedListener;
    private LocationRequest request;
    private PopupMenu popup;
    private ProgressDialog loadingDialog;
    private boolean requestInProgress = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_map, container, false);

        searchView = rootView.findViewById(R.id.searchBar);
        fabMapType = rootView.findViewById(R.id.fabMapType);
        fabDirections = rootView.findViewById(R.id.fabDirections);
        fabLocation = rootView.findViewById(R.id.fabLocation);

        initMapAndLocation();
        searchViewListener();
        fabsListeners();
        createPopupMenu();
        createLoadingDialog();

        return rootView;
    }

    private void createLoadingDialog() {
        loadingDialog = new ProgressDialog(getActivity());
        loadingDialog.setMessage(getString(R.string.dialog_loading_information));
        loadingDialog.setCancelable(false);
        loadingDialog.setInverseBackgroundForced(false);
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
                showSnackBar(rootView,"pulsado como llegar");
            }
        });

        fabLocation.hide();
        fabLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastLocation != null) {
                    if (LIMIT_MAP.southwest.longitude < lastLocation.getLongitude() && lastLocation.getLongitude() < LIMIT_MAP.northeast.longitude &&
                            LIMIT_MAP.southwest.latitude < lastLocation.getLatitude() && lastLocation.getLatitude() < LIMIT_MAP.northeast.latitude) {
                        LatLng position = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
                        moveCamera(position, CAMERA_ZOOM_STREET);
                    } else {
                        showSnackBar(rootView, getString(R.string.map_location_out_bounds));
                    }
                }
            }
        });
    }

    private void initMapAndLocation() {
        //Configure Mapview and sync to google map.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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
            showSnackBar(rootView, getString(R.string.permissions_not_granted));
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
                setMarkerSearch(latLng);
            }
        });
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.getTitle() == null){
                    LatLng position = marker.getPosition();
                    float zoom = map.getCameraPosition().zoom + 2;
                    moveCamera(position, zoom);
                } else if (marker.getTitle().equals("SEARCH")){
                    showSnackBar(rootView,"pulsado search");
                } else if (!requestInProgress){
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

    private void moveCamera(LatLng position, float zoom) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(position, zoom));
    }

    private void setMarkerSearch(LatLng latLng) {
        if (markerSearch != null) {
            markerSearch.remove();
        }
        markerSearch = map.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)).title("SEARCH"));
    }
    /**
     * Updates the user interface to display the new latitude and longitude.
     * It will also start an asynchronous task to translate those coordinates into a human readable address.
     */
    private void updateUI(Location location) {
        lastLocation = location;
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

    private void searchViewListener() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return querySubmit(query);
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private boolean querySubmit(String query) {
        String location = query;
        location = location.toLowerCase();

        List<Address> addressList = null;

        if (!location.equals("")){
            Geocoder geocoder = new Geocoder(getActivity());
            boolean insideArea = false;

            for (int i = 0; i < 2 && !insideArea; i++) {
                try {
                    addressList = geocoder.getFromLocationName(location,1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (addressList != null && addressList.size() != 0) {
                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                    if (LIMIT_MAP.southwest.longitude < latLng.longitude && latLng.longitude < LIMIT_MAP.northeast.longitude &&
                            LIMIT_MAP.southwest.latitude < latLng.latitude && latLng.latitude < LIMIT_MAP.northeast.latitude) {

                        setMarkerSearch(latLng);
                        moveCamera(latLng, CAMERA_ZOOM_STREET);
                        insideArea = true;
                    }
                }

                if (!location.contains("valencia")) {
                    location = "valencia " + location;
                } else {
                    location = location.replace("valencia", "");
                }
            }

            if (!insideArea) {
                showSnackBar(rootView,getString(R.string.map_search_out_bounds));
            }
        }
        return false;
    }

    private void requestStations(int number) {
        requestInProgress = true;
        loadingDialog.show();
        PetitionAsyncTask petitionAsyncTask = new PetitionAsyncTask(this);
        petitionAsyncTask.execute(number);
    }

    private void requestDone() {
        loadingDialog.hide();
        requestInProgress = false;
    }

    @Override
    public void receivedAllStations(List<Station> stations) {
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
        TextView tv = dialogView.findViewById(R.id.textViewSheet);
        tv.setText(station.getAddress());
        dialog.setContentView(dialogView);
        LatLng position = new LatLng(station.getPosition().getLat() + POSITION_MARKER_SHEET, station.getPosition().getLng());
        moveCamera(position, CAMERA_ZOOM_STREET);
        dialog.show();
        requestDone();
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
            if (locationAvailability.isLocationAvailable()){
                fabLocation.show();
            } else {
                fabLocation.hide();
            }
        }
    }
}
