package disca.dadm.valenbike.tasks;

import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import disca.dadm.valenbike.database.StationDao;
import disca.dadm.valenbike.database.StationDb;
import disca.dadm.valenbike.database.ValenbikeDatabase;
import disca.dadm.valenbike.fragments.StationsFragment;
import disca.dadm.valenbike.models.StationGUI;

public class UpdateDbAsyncTask extends AsyncTask<List<StationDb>, Void, Void> {

    //private final WeakReference<StationsFragment> fragmentWr;

    private Context context;
    private List<StationDb> stations;

    public UpdateDbAsyncTask(Context context) {
        this.context = context;
        this.stations = new ArrayList<>();
    }


    @Override
    protected Void doInBackground (List<StationDb>... params) {
        stations = params[0];
        try {
            StationDao stationsDao = ValenbikeDatabase.getInstance(context).stationDao();

            for (int i = 0; i < stations.size(); i++) {
                if (stationsDao.getStationByNumber(stations.get(i).getNumber()) != null) {
                    if (!stations.get(i).getNotifyBikes() && !stations.get(i).isFavourite()) {
                        stationsDao.deleteStation(stations.get(i));
                    } else {
                        stationsDao.updateStation(stations.get(i));
                    }
                } else {
                    stationsDao.addStation(stations.get(i));
                }
            }
        }
        catch (SQLiteException e) {
            e.printStackTrace();
        }

        return null;
    }
}