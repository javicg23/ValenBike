package disca.dadm.valenbike.tasks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import disca.dadm.valenbike.R;
import disca.dadm.valenbike.interfaces.OnRouteTaskCompleted;
import disca.dadm.valenbike.models.ParametersRouteTask;

// Result as a ArrayList because is the form to pass data to bundle

public class RouteAsyncTask extends AsyncTask<ParametersRouteTask, Void, String> {

    private OnRouteTaskCompleted listener;
    @SuppressLint("StaticFieldLeak")
    private Context context;
    private int route;

    public RouteAsyncTask(Context context, OnRouteTaskCompleted listener) {
        this.listener = listener;
        this.context = context;
    }

    /**
     * Gets the route between the two markers and creates a matching Polyline.
     */
    @Override
    protected String doInBackground(ParametersRouteTask... params) {
        route = params[0].getRoute();
        LatLng origin = params[0].getOrigin();
        LatLng destination = params[0].getDestination();

        String result = null;

        String mode = "walking";
        if (route == ParametersRouteTask.ROUTE_BIKE) {
            mode = "bicycling";
        }

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https");
        builder.authority("maps.googleapis.com");
        builder.appendPath("maps");
        builder.appendPath("api");
        builder.appendPath("directions");
        builder.appendPath("json");
        builder.appendQueryParameter("origin", origin.latitude + "," + origin.longitude);
        builder.appendQueryParameter("destination", destination.latitude + "," + destination.longitude);
        builder.appendQueryParameter("mode", mode);
        builder.appendQueryParameter("language", "es");
        builder.appendQueryParameter("units", "metric");
        builder.appendQueryParameter("key", context.getResources().getString(R.string.google_maps_directions_key));
        try {
            // Launch the related GET request
            URL url = new URL(builder.build().toString());
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);

            // Check that the request has been successful
            if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                result = response.toString();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Return the List of LatLng objects constituting the route to be displayed
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        switch (route) {
            case ParametersRouteTask.ROUTE_ORIGIN:
                listener.receivedOriginRoute(result);
                break;
            case ParametersRouteTask.ROUTE_BIKE:
                listener.receivedBikeRoute(result);
                break;
            case ParametersRouteTask.ROUTE_DESTINATION:
                listener.receivedDestinationRoute(result);
                break;
        }
    }

}
