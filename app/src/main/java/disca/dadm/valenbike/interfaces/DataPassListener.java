package disca.dadm.valenbike.interfaces;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public interface DataPassListener {
    void passLocationToDirection(LatLng sourcePosition, String sourceAddress, LatLng destinationPosition, String destinationAddress);

    void passRouteToMap(ArrayList<String> responses);
}
