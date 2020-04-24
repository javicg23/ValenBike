package disca.dadm.valenbike.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import disca.dadm.valenbike.R;
import disca.dadm.valenbike.models.ClusterStation;

import static disca.dadm.valenbike.utils.Tools.getMarkerIconFromDrawable;

public class MarkerClusterRenderer extends DefaultClusterRenderer<ClusterStation> {

    private Context context;

    public MarkerClusterRenderer(Context context, GoogleMap map, ClusterManager<ClusterStation> clusterManager) {
        super(context, map, clusterManager);
        this.context = context;
    }

    @Override
    protected void onBeforeClusterItemRendered(ClusterStation item, MarkerOptions markerOptions) {
        if (item.isActive()) {
            if (item.isFreeBikes()) {
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            } else {
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            }
        } else {
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
        }
    }
}
