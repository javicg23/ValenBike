package disca.dadm.valenbike.models;

public class ParametersGeocoderTask {

    public static int LOCATION_MARKER = 0;
    public static int LOCATION_GPS = 1;
    public static int LOCATION_STATION = 2;

    private int location;
    private double latitude;
    private double longitude;

    public ParametersGeocoderTask(int location, double latitude, double longitude) {
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getLocation() {
        return location;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
