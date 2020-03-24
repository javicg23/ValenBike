package disca.dadm.valenbike.fragments;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.List;

import disca.dadm.valenbike.R;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap map;
    private SearchView searchView;
    // Create a LatLngBounds that includes the ValenBisi locations with an edge.
    private LatLngBounds LIMIT_MAP = new LatLngBounds(
            new LatLng(39.354547, -0.574788),new LatLng(39.583997, -0.169773));

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        //Configure Mapview and sync to google map.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        searchView = rootView.findViewById(R.id.searchBar);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;

                if (location != null || !location.equals("")){
                    Geocoder geocoder = new Geocoder(getActivity());
                    try {
                        addressList = geocoder.getFromLocationName(location,1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (addressList != null && addressList.size() != 0) {
                        Address address = addressList.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
                        if (LIMIT_MAP.southwest.longitude < latLng.longitude && latLng.longitude  < LIMIT_MAP.northeast.longitude &&
                                LIMIT_MAP.southwest.latitude < latLng.latitude && latLng.latitude < LIMIT_MAP.northeast.latitude) {
                            map.addMarker(new MarkerOptions().position(latLng).title(location));
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom( latLng,13));
                        } else {
                            showSnackBar(rootView,getString(R.string.map_location_out_bounds));
                        }
                    } else {
                        showSnackBar(rootView,getString(R.string.map_location_not_found));
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return rootView;
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
}
