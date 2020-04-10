package disca.dadm.valenbike.tasks;

import android.os.AsyncTask;

import java.lang.ref.WeakReference;
import java.util.List;

import disca.dadm.valenbike.activities.MainActivity;
import disca.dadm.valenbike.database.Station;
import disca.dadm.valenbike.database.ValenbikeDatabase;
import disca.dadm.valenbike.fragments.StationsFragment;

public class StationsAsyncTask extends AsyncTask<Void, Void, List<Station>> {

    private final WeakReference<StationsFragment> fragment;

    public StationsAsyncTask(StationsFragment fragment) {
        super();
        this.fragment = new WeakReference<>(fragment);
    }

    @Override
    protected List<Station> doInBackground (Void... params) {
        if(fragment.get() == null) { return null; }

        List<Station> dbStations = ValenbikeDatabase.getInstance(fragment.get().getContext()).stationDao().getStations();
        return dbStations;
    }

    @Override
    protected void onPostExecute(List<Station> params) {
        if (fragment.get() != null) {
            //fragment.get().initData(params);
        }
    }
}