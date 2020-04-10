//package disca.dadm.valenbike.tasks;
//
//import android.os.AsyncTask;
//
//import java.lang.ref.WeakReference;
//import java.util.List;
//
//import disca.dadm.valenbike.activities.MainActivity;
//import disca.dadm.valenbike.database.Stations;
//import disca.dadm.valenbike.database.ValenbikeDatabase;
//import disca.dadm.valenbike.fragments.StationsFragment;
//import disca.dadm.valenbike.models.StationGUI;
//
//public class StationsAsyncTask extends AsyncTask<Void, Void, List<StationGUI>> {
//
//    private final WeakReference<StationsFragment> fragment;
//
//    public StationsAsyncTask(StationsFragment fragment) {
//        super();
//        this.fragment = new WeakReference<>(fragment);
//    }
//
//    @Override
//    protected List<StationGUI> doInBackground (Void... params) {
//        if(fragment.get() == null) { return null; }
//
//        List<StationGUI> dbStations = ValenbikeDatabase.getInstance(fragment.get().getContext()).stationDao().getStations();
//        return dbStations;
//    }
//
//    @Override
//    protected void onPostExecute(List<StationGUI> params) {
//        if (fragment.get() != null) {
//            //fragment.get().initData(params);
//        }
//    }
//}