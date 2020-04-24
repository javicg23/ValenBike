package disca.dadm.valenbike.models;

public class Position {
    private double lat;
    private double lng;

    public Position(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double value) {
        this.lat = value;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double value) {
        this.lng = value;
    }

    public Position getPosition() {
        return this;
    }

    public String toString() {
        return lat + ", " + lng;
    }
}
