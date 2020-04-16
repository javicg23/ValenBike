package disca.dadm.valenbike.models;

import com.google.android.gms.maps.model.LatLng;

public class ParametersRouteTask {

    public static final int ROUTE_ORIGIN = 0;
    public static final int ROUTE_BIKE = 1;
    public static final int ROUTE_DESTINATION = 2;

    private int route;
    private LatLng origin;
    private LatLng destination;

    public ParametersRouteTask(int route, LatLng origin, LatLng destination) {
        this.route = route;
        this.origin = origin;
        this.destination = destination;
    }

    public int getRoute() {
        return route;
    }

    public LatLng getOrigin() {
        return origin;
    }

    public LatLng getDestination() {
        return destination;
    }
}
