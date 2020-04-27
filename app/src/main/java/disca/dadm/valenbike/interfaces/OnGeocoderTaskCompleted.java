package disca.dadm.valenbike.interfaces;

public interface OnGeocoderTaskCompleted {
    void receivedAddressGPS(String address);

    void receivedAddressMarker(String address);

    void receivedAddressStation(String address);
}
