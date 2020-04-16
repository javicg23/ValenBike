package disca.dadm.valenbike.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import disca.dadm.valenbike.R;
import disca.dadm.valenbike.models.Station;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class Tools {

    public static void showSnackBar(View view, boolean aboveNav, String msg) {
        Snackbar snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_SHORT);
        if (aboveNav) {
            snackbar.setAnchorView(R.id.bottomView);
        }
        snackbar.show();
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = null;
        if (manager != null) {
            info = manager.getActiveNetworkInfo();
        }
        return info != null && info.isConnected();
    }

    /* todo remove all this method*/
    public static List<Station> getStations() {

        List<Station> stations = new ArrayList<>();
        stations.add(new Station(102, "valence", "", "Ramon Llul - Serpis",
                new Station.Position(39.47580112282824, -0.346811013895548), false,
                false, 20, 20, 0, "OPEN", 1585552200));

        stations.add(new Station(96, "valence", "", "Blasco Ibañez - Yecla",
                new Station.Position(39.47352211466433, -0.348305017144382), false,
                false, 20, 0, 20, "OPEN", 1585552200));

        stations.add(new Station(114, "valence", "", "UPV informatica",
                new Station.Position(39.481772142992675, -0.346682016720988), false,
                false, 20, 0, 20, "OPEN", 1585552200));

        stations.add(new Station(113, "valence", "", "UPV caminos",
                new Station.Position(39.48124414217441, -0.343702007510602), false,
                false, 20, 10, 10, "OPEN", 1585552200));

        stations.add(new Station(258, "valence", "", "Pintor rafael solves",
                new Station.Position(39.43979598762512, -0.38922812163005), false,
                false, 20, 0, 20, "CLOSED", 1585552200));

        stations.add(new Station(257, "valence", "", "Plaza salvador soria",
                new Station.Position(39.44521900594571, -0.389195124464717), false,
                false, 20, 10, 10, "OPEN", 1585552200));
        return stations;
    }
}
