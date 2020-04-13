package disca.dadm.valenbike.interfaces;

import com.google.android.gms.maps.model.LatLng;

public interface DataPassListener{
    void passLocationToDirection(LatLng sourcePosition, String sourceAddress, LatLng destinationPosition, String destinationAddress);
}
