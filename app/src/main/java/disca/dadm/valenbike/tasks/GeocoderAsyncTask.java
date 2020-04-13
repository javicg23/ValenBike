package disca.dadm.valenbike.tasks;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

import disca.dadm.valenbike.fragments.MapFragment;
import disca.dadm.valenbike.models.ParametersGeocoderTask;

/**
 * Custom asynchronous task to translate received coordinates into a human readable address.
 */
public class GeocoderAsyncTask extends AsyncTask<ParametersGeocoderTask, Void, Address> {

    private WeakReference<MapFragment> fragment;
    private ParametersGeocoderTask parametersGeocoderTask;

    public GeocoderAsyncTask(MapFragment fragment) {
        this.fragment = new WeakReference<>(fragment);
    }

    /**
     * Update the interface of activity that launched the asynchronous task.
     */
    @Override
    protected void onPostExecute(Address address) {
        String res = "";

        if (fragment.get() != null) {
            // Check that the Geocoder got an address
            if ((address != null) && (address.getMaxAddressLineIndex() != -1)) {

                // Get the whole address (comma separated lines) in a single String
                res += (address.getAddressLine(0));
                for (int i = 1; i <= address.getMaxAddressLineIndex(); i++) {
                    res += ", " + address.getAddressLine(i);
                }
            }
            // Update the user interface
            if (parametersGeocoderTask.isLocation()){
                fragment.get().setAddressLocation(res);
            } else {
                fragment.get().setAddressSearch(res);
            }
        }
    }

    /**
     * Translates coordinates into address in a background thread.
     */
    @Override
    protected Address doInBackground(ParametersGeocoderTask... params) {

        // Hold reference to a Geocoder to translate coordinates into human readable addresses
        Geocoder geocoder;
        // Initialize the Geocoder
        geocoder = new Geocoder(fragment.get().getContext());
        parametersGeocoderTask = params[0];
        try {
            // Gets a maximum of 1 address from the Geocoder
            List<Address> addresses = geocoder.getFromLocation(parametersGeocoderTask.getLatitude(), parametersGeocoderTask.getLongitude(), 1);
            // Check that the Geocoder has obtained at least 1 address
            if ((addresses != null) && (addresses.size() > 0)) {
                return addresses.get(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
