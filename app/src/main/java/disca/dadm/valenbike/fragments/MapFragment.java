package disca.dadm.valenbike.fragments;

import android.app.DownloadManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.List;
import java.util.Queue;

import disca.dadm.valenbike.R;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap map;
    private SearchView searchView;
    private FloatingActionButton fabMapType, fabDirections, fabLocation;
    // Create a LatLngBounds that includes the ValenBisi locations with an edge.
    private LatLngBounds LIMIT_MAP = new LatLngBounds(
            new LatLng(39.354547, -0.574788),new LatLng(39.583997, -0.169773));
    private Marker markerSearch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        //Configure Mapview and sync to google map.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        searchView = rootView.findViewById(R.id.searchBar);
        fabMapType = rootView.findViewById(R.id.fabMapType);
        fabDirections = rootView.findViewById(R.id.fabDirections);
        fabLocation = rootView.findViewById(R.id.fabLocation);

        searchViewListener(rootView);
        fabsListeners(rootView);

        return rootView;
    }

    private void fabsListeners(final View rootView) {
        fabMapType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSnackBar(rootView,"pulsado mapa tipo");
            }
        });

        fabDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSnackBar(rootView,"pulsado como llegar");
            }
        });

        fabLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSnackBar(rootView,"pulsado localizacion");
            }
        });
    }

    private void searchViewListener(final View rootView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return querySubmit(rootView,query);
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        // Constrain the camera target to the Valencia bounds.
        map.setLatLngBoundsForCameraTarget(LIMIT_MAP);
    }

    private void showSnackBar(View view, String msg) {
        Snackbar snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_SHORT);
        snackbar.setAnchorView(R.id.bottomView);
        snackbar.show();
    }

    private boolean querySubmit(View rootView, String query) {
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

                        if (markerSearch != null) {
                            markerSearch.remove();
                        }
                        markerSearch = map.addMarker(new MarkerOptions().position(latLng).draggable(true));
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
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
                showSnackBar(rootView,getString(R.string.map_location_out_bounds));
            }
        }
        return false;
    }

}
