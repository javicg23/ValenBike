package disca.dadm.valenbike.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
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
import disca.dadm.valenbike.database.StationDb;
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
                new Position(39.47352211466433, -0.348305017144382), true,
                false, 20, 20, 0, "OPEN", 1585552200));

        stations.add(new Station(114, "valence", "", "UPV informatica",
                new Position(39.481772142992675, -0.346682016720988), false,
                false, 20, 20, 0, "OPEN", 1585552200));

        stations.add(new Station(113, "valence", "", "UPV caminos",
                new Position(39.48124414217441, -0.343702007510602), false,
                false, 20, 10, 10, "OPEN", 1585552200));

        stations.add(new Station(258, "valence", "", "Pintor rafael solves",
                new Position(39.43979598762512, -0.38922812163005), false,
                false, 20, 20, 0, "OPEN", 1585552200));

        stations.add(new Station(257, "valence", "", "Plaza salvador soria",
                new Position(39.44521900594571, -0.389195124464717), false,
                false, 20, 10, 10, "OPEN", 1585552200));

        stations.add(new Station(156, "valence", "", "Puerto Rico - Cuba",
                new Position(39.460932063112466, -0.376436094678165), false,
                false, 20, 10, 10, "OPEN", 1585552200));

        stations.add(new Station(187, "valence", "", "Ángel Villena - Ausias March",
                new Position(39.447928021676226, -0.368909065084874), false,
                false, 20, 10, 10, "CLOSED", 1585552200));

        stations.add(new Station(44, "valence", "", "General Urrutia - Av. de la Plata",
                new Position(39.456397052131265, -0.363105052261891), false,
                false, 20, 10, 10, "OPEN", 1585552200));

        stations.add(new Station(29, "valence", "", "Plaza América - Cirilo Amorós - Sorní",
                new Position(39.47008009754055, -0.36539006649012), true,
                false, 20, 10, 10, "CLOSED", 1585552200));

        stations.add(new Station(83, "valence", "", "General Elio - Llano del Real",
                new Position(39.47755312220108, -0.367061075518166), false,
                false, 20, 10, 0, "OPEN", 1585552200));

        stations.add(new Station(92, "valence", "", "Blasco Ibañez - Aragón",
                new Position(39.475828119933816, -0.356059041618514), false,
                false, 20, 10, 10, "OPEN", 1585552200));

        stations.add(new Station(50, "valence", "", "Autopista del Saler - Antonio Ferrandis (C.C. El Saler)",
                new Position(39.453286044933414, -0.352950020141597), true,
                false, 20, 10, 10, "CLOSED", 1585552200));

        stations.add(new Station(273, "valence", "", "Moraira - Alta del Mar",
                new Position(39.450273041140704, -0.33336295982062), false,
                false, 20, 10, 10, "OPEN", 1585552200));

        stations.add(new Station(79, "valence", "", "Aragón - Ernesto Ferrer",
                new Position(39.47274710913562, -0.357333043778256), false,
                false, 20, 10, 10, "CLOSED", 1585552200));

        stations.add(new Station(72, "valence", "", "Ramiro de Maeztu - Peris Brell",
                new Position(39.467225, -0.346537), false,
                false, 20, 10, 10, "OPEN", 1585552200));

        stations.add(new Station(163, "valence", "", "Paseo Neptuno 32-34",
                new Position(39.464405091973795, -0.323491937905993), false,
                false, 20, 10, 10, "CLOSED", 1585552200));

        stations.add(new Station(161, "valence", "", "Mediterráneo - Plaza Cruz de Cañamelar",
                new Position(39.467937101175984, -0.331816964744236), false,
                false, 20, 10, 10, "OPEN", 1585552200));

        stations.add(new Station(169, "valence", "", "Pavía (Instituto Isabel de Villena)",
                new Position(39.478753139905066, -0.324735949373236), true,
                false, 20, 10, 10, "OPEN", 1585552200));

        stations.add(new Station(125, "valence", "", "Masquefa, 42 - 44",
                new Position(39.489661165707126, -0.358709056987903), false,
                false, 20, 10, 10, "CLOSED", 1585552200));

        stations.add(new Station(231, "valence", "", "Alcañiz - Cambrils",
                new Position(39.495040177417366, -0.378712119815713), true,
                false, 20, 10, 10, "OPEN", 1585552200));

        stations.add(new Station(139, "valence", "", "Reus - Alquería de la Estrella",
                new Position(39.4856741444693, -0.382964127549696), true,
                false, 20, 10, 10, "CLOSED", 1585552200));

        stations.add(new Station(143, "valence", "", "Pio XII - Menéndez PIdal (Nuevo Centro)",
                new Position(39.4795051210676, -0.391027148421504), true,
                false, 20, 10, 10, "CLOSED", 1585552200));

        stations.add(new Station(268, "valence", "", "Plaza Luis Cano, 5",
                new Position(39.50141318615774, -0.418584242795808), false,
                false, 20, 10, 10, "OPEN", 1585552200));

        stations.add(new Station(247, "valence", "", "Tres Cruces - Músico Ayllón",
                new Position(39.46737807542721, -0.405664185821979), true,
                false, 20, 10, 10, "OPEN", 1585552200));

        stations.add(new Station(3, "valence", "", "Plaza del Musico López Chavarri",
                new Position(39.4768031153802, -0.38037911504075), true,
                false, 20, 10, 10, "OPEN", 1585552200));

        stations.add(new Station(200, "valence", "", "Av. del Cid - Julián Peña",
                new Position(39.4677050805168, -0.393305148918046), false,
                false, 20, 10, 10, "CLOSED", 1585552200));

        stations.add(new Station(210, "valence", "", "Campos Crespo - Juan de Garay",
                new Position(39.45552103825903, -0.396807152864784), true,
                false, 20, 10, 10, "OPEN", 1585552200));

        stations.add(new Station(17, "valence", "", "Xátiva - Bailén (Estación del Norte)",
                new Position(39.467436084759534, -0.377350100922523), true,
                false, 20, 10, 10, "CLOSED", 1585552200));

        return stations;
    }


    public static List<StationGUI> mergeToStationGui(List<Station> stationList, List<StationDb> dbList) {
        List<StationGUI> stationsGui = new ArrayList<>();

        for (int i = 0; i < stationList.size(); i++) {
            StationGUI newStation = new StationGUI(
                    stationList.get(i).getNumber(),
                    stationList.get(i).getContractName(),
                    stationList.get(i).getName(),
                    stationList.get(i).getAddress(),
                    stationList.get(i).getPosition(),
                    stationList.get(i).getBanking(),
                    stationList.get(i).getBonus(),
                    stationList.get(i).getBikeStands(),
                    stationList.get(i).getAvailableBikeStands(),
                    stationList.get(i).getAvailableBikes(),
                    stationList.get(i).getStatus(),
                    stationList.get(i).getLastUpdate()
            );

            for (int j = 0; j < dbList.size(); j++) {
                if (dbList.get(j).getNumber() == stationList.get(i).getNumber()) {
                    newStation.setFavouriteCheck(dbList.get(j).isFavourite());
                    newStation.setReminderCheck(dbList.get(j).getNotifyBikes());
                }
            }
            stationsGui.add(newStation);
        }
        return stationsGui;
    }
}
