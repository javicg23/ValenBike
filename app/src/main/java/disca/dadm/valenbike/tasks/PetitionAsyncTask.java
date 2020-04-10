package disca.dadm.valenbike.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import disca.dadm.valenbike.R;
import disca.dadm.valenbike.activities.MainActivity;
import disca.dadm.valenbike.fragments.MapFragment;
import disca.dadm.valenbike.models.Station;

public class PetitionAsyncTask extends AsyncTask<Integer, Void, List<Station>> {

    private OnTaskCompleted listener;
    private Context context;
    private ProgressDialog progressDialog;

    public PetitionAsyncTask(Context context, OnTaskCompleted listener) {
        this.listener = listener;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(context.getString(R.string.dialog_loading_information));
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(List<Station> stations) {
        super.onPostExecute(stations);
        progressDialog.dismiss();
        progressDialog = null;

        if (stations.size() > 1) {
            listener.receivedAllStations(stations);
        } else {
            listener.receivedStation(stations.get(0));
        }
    }

    @Override
    protected List<Station> doInBackground(Integer...integers) {
        int numStation = integers[0];
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https");
        builder.authority("api.jcdecaux.com");
        builder.appendPath("vls");
        builder.appendPath("v1");
        builder.appendPath("stations");
        if (numStation != 0) {
            builder.appendPath(Integer.toString(numStation));
        }
        builder.appendQueryParameter("contract","valence");
        /*todo change harcoded api*/
        builder.appendQueryParameter("apiKey", "826d3b8a1da38c94e235e378d86abdb4802e18f5");

        List<Station> stations = null;
        try {
            URL url = new URL(builder.build().toString());
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);

            if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                Gson gson = new Gson();
                if (numStation == 0) {
                    stations = gson.fromJson(reader, new TypeToken<ArrayList<Station>>(){}.getType());
                } else {
                    stations = new ArrayList<>();
                    stations.add(gson.fromJson(reader, Station.class));
                }
                reader.close();
            }
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stations;
    }
}
