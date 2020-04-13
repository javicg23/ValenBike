package disca.dadm.valenbike.models;

public class ParametersGeocoderTask {
    private boolean location;
    private double latitude;
    private double longitude;

    public ParametersGeocoderTask(boolean location, double latitude, double longitude) {
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
    }


    public boolean isLocation() {
        return location;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
