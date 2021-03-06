package disca.dadm.valenbike.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class ClusterStation implements ClusterItem {

    private int number;
    private LatLng position;
    private boolean freeBikes;
    private boolean active;


    public ClusterStation(int number, LatLng position, boolean freeBikes, boolean active) {
        this.number = number;
        this.position = position;
        this.freeBikes = freeBikes;
        this.active = active;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    @Override
    public String getTitle() {
        return Integer.toString(number);
    }

    @Override
    public String getSnippet() {
        return "";
    }

    public boolean isFreeBikes() {
        return freeBikes;
    }

    public boolean isActive() {
        return active;
    }

}
