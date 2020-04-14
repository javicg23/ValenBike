package disca.dadm.valenbike.interfaces;

import java.util.List;

import disca.dadm.valenbike.models.Station;

public interface OnGeocoderTaskCompleted {
    void receivedAddressGPS(String address);
    void receivedAddressMarker(String address);
    void receivedAddressStation(String address);
}
