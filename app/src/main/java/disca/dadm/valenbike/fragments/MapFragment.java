package disca.dadm.valenbike.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import disca.dadm.valenbike.R;

public class MapFragment extends Fragment implements OnMapReadyCallback{

    private GoogleMap map;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        /*
         * Configure Mapview and sync to google map.
         */
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        //map.onCreate(savedInstanceState);
        mapFragment.getMapAsync(this);
        //mapView.onResume(); // needed to get the map to display immediately

        //your code
        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;


    }
}
