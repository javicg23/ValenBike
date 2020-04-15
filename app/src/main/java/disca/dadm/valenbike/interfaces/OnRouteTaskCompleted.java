package disca.dadm.valenbike.interfaces;

public interface OnRouteTaskCompleted {
    void receivedOriginRoute(String result);
    void receivedBikeRoute(String result);
    void receivedDestinationRoute(String result);
    void receivedWalkingRoute(String result);
}
