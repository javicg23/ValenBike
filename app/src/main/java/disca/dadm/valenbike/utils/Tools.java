package disca.dadm.valenbike.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import disca.dadm.valenbike.R;
import disca.dadm.valenbike.fragments.MapFragment;
import disca.dadm.valenbike.interfaces.OnGeocoderTaskCompleted;
import disca.dadm.valenbike.models.ParametersGeocoderTask;
import disca.dadm.valenbike.models.Position;
import disca.dadm.valenbike.models.Station;
import disca.dadm.valenbike.models.StationGUI;
import disca.dadm.valenbike.tasks.GeocoderAsyncTask;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class Tools {

    private static AlertDialog.Builder builder;

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = null;
        if (manager != null) {
            info = manager.getActiveNetworkInfo();
        }
        return info != null && info.isConnected();
    }

    public static BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    // depends on the location put the res in addressLastLocation or addressSearch
    public static void coordinatesToAddress(Context context, OnGeocoderTaskCompleted onGeocoderTaskCompleted, int location, LatLng latLng) {
        // Start asynchronous task to translate coordinates into an address
        if (isNetworkConnected(Objects.requireNonNull(context))) {
            (new GeocoderAsyncTask(context, onGeocoderTaskCompleted)).execute(new ParametersGeocoderTask(location, latLng.latitude, latLng.longitude));
        }
    }

    public static AlertDialog.Builder getDialogProgressBar(Context context) {

            builder = new AlertDialog.Builder(context);

            builder.setTitle(context.getString(R.string.dialog_loading_information));

            ProgressBar progressBar = new ProgressBar(context);
            progressBar.setPadding(17, 17, 17, 17);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            progressBar.setLayoutParams(lp);
            builder.setCancelable(false);
            builder.setView(progressBar);

        return builder;
    }

    /* todo remove all this method*/
    public static List<Station> getStations() {

        List<Station> stations = new ArrayList<>();
        stations.add(new Station(102, "valence", "", "Ramon Llul - Serpis",
                new Position(39.47580112282824, -0.346811013895548), false,
                false, 20, 10, 10, "OPEN", 1585552200));

        stations.add(new Station(96, "valence", "", "Blasco Ibañez - Yecla",
                new Position(39.47352211466433, -0.348305017144382), false,
                false, 20, 0, 20, "OPEN", 1585552200));

        stations.add(new Station(114, "valence", "", "UPV informatica",
                new Position(39.481772142992675, -0.346682016720988), false,
                false, 20, 0, 20, "OPEN", 1585552200));

        stations.add(new Station(113, "valence", "", "UPV caminos",
                new Position(39.48124414217441, -0.343702007510602), false,
                false, 20, 10, 10, "OPEN", 1585552200));

        stations.add(new Station(258, "valence", "", "Pintor rafael solves",
                new Position(39.43979598762512, -0.38922812163005), false,
                false, 20, 0, 20, "OPEN", 1585552200));

        stations.add(new Station(257, "valence", "", "Plaza salvador soria",
                new Position(39.44521900594571, -0.389195124464717), false,
                false, 20, 10, 10, "OPEN", 1585552200));
        return stations;
    }

    /* todo remove all this method*/
    public static List<StationGUI> getStationsGui() {

        List<StationGUI> stations = new ArrayList<>();
        stations.add(new StationGUI(102, "valence", "", "Ramon Llul - Serpis",
                new Position(39.47580112282824, -0.346811013895548), false,
                false, 20, 10, 10, "OPEN", 1585552200));

        stations.add(new StationGUI(96, "valence", "", "Blasco Ibañez - Yecla",
                new Position(39.47352211466433, -0.348305017144382), true,
                false, 20, 0, 20, "OPEN", 1585552200));

        stations.add(new StationGUI(114, "valence", "", "UPV informatica",
                new Position(39.481772142992675, -0.346682016720988), false,
                false, 20, 0, 20, "OPEN", 1585552200));

        stations.add(new StationGUI(113, "valence", "", "UPV caminos",
                new Position(39.48124414217441, -0.343702007510602), false,
                false, 20, 10, 10, "OPEN", 1585552200));

        stations.add(new StationGUI(258, "valence", "", "Pintor rafael solves",
                new Position(39.43979598762512, -0.38922812163005), false,
                false, 20, 0, 20, "OPEN", 1585552200));

        stations.add(new StationGUI(257, "valence", "", "Plaza salvador soria",
                new Position(39.44521900594571, -0.389195124464717), false,
                false, 20, 10, 10, "OPEN", 1585552200));
        return stations;
    }
}
