package disca.dadm.valenbike.models;

import org.json.JSONArray;

public class Route {
    public static final int MODE_BIKE = 0;
    public static final int MODE_WALK = 1;

    private int mode;
    private String address;
    private String duration;
    private String distance;
    private JSONArray indications;
    private boolean expanded;
    private boolean arrowDown;

    public Route(int mode, String address, String duration, String distance, JSONArray indications) {
        this.mode = mode;
        this.address = address;
        this.duration = duration;
        this.distance = distance;
        this.indications = indications;
        this.expanded = false;
        this.arrowDown = false;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public JSONArray getIndications() {
        return indications;
    }

    public void setIndications(JSONArray indications) {
        this.indications = indications;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public boolean isArrowDown() {
        return arrowDown;
    }

    public void setArrowDown(boolean arrowDown) {
        this.arrowDown = arrowDown;
    }

}
