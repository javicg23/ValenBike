/*
 * Copyright (c) 2019. David de Andrés and Juan Carlos Ruiz, DISCA - UPV, Development of apps for mobile devices.
 */

package disca.dadm.valenbike.tasks;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import disca.dadm.valenbike.R;
import disca.dadm.valenbike.interfaces.OnRouteTaskCompleted;

// Result as a ArrayList because is the form to pass data to bundle

public class RouteAsyncTask extends AsyncTask<List<LatLng>, Void, String> {

    private OnRouteTaskCompleted listener;
    private Context context;

    public RouteAsyncTask(Context context, OnRouteTaskCompleted listener) {
        this.listener = listener;
        this.context = context;
    }

    /**
     * Gets the route between the two markers and creates a matching Polyline.
     */
    @Override
    protected String doInBackground(List<LatLng>... params) {

        List<LatLng> listWaypoints = params[0];
        String result = null;

        String waypoints = listWaypoints.get(1).latitude + "," + listWaypoints.get(1).longitude;
        if (listWaypoints.size() == 4) {
            waypoints += "|" + listWaypoints.get(2).latitude + "," + listWaypoints.get(2).longitude;
        }

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https");
        builder.authority("maps.googleapis.com");
        builder.appendPath("maps");
        builder.appendPath("api");
        builder.appendPath("directions");
        builder.appendPath("json");
        builder.appendQueryParameter("origin", listWaypoints.get(0).latitude + "," + listWaypoints.get(0).longitude);
        builder.appendQueryParameter("destination", listWaypoints.get(listWaypoints.size() - 1).latitude + "," + listWaypoints.get(listWaypoints.size() - 1).longitude);
        builder.appendQueryParameter("waypoints", waypoints);
        builder.appendQueryParameter("mode","bicycling");
        builder.appendQueryParameter("language","es");
        builder.appendQueryParameter("units","metric");
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

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Return the List of LatLng objects constituting the route to be displayed
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        listener.receivedRoute(result);
    }

}
