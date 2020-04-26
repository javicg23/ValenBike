package disca.dadm.valenbike.tasks;

import android.os.AsyncTask;

import java.lang.ref.WeakReference;
import java.util.List;

import disca.dadm.valenbike.database.StationDb;
import disca.dadm.valenbike.database.ValenbikeDatabase;
import disca.dadm.valenbike.fragments.StationsFragment;

public class StationsDbAsyncTask  extends AsyncTask<Void, Void, List<StationDb>> {
    private WeakReference<StationsFragment> activity;

    public StationsDbAsyncTask(StationsFragment stationsFragment) {
        this.activity = new WeakReference<>(stationsFragment);
    }

    @Override
    protected List<StationDb> doInBackground(Void... param) {
        List<StationDb> list =  ValenbikeDatabase.getInstance(this.activity.get().getContext()).stationDao().getStations();
        return list;
    }

    @Override
    protected void onPostExecute(List<StationDb> list) {
        super.onPostExecute(list);
        if (this.activity.get() != null) this.activity.get().getListDb(list);
    }
}
