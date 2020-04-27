package disca.dadm.valenbike.tasks;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import disca.dadm.valenbike.database.ValenbikeSQLiteOpenHelper;
import disca.dadm.valenbike.interfaces.OnStationTaskCompleted;
import disca.dadm.valenbike.models.StationGUI;

public class StationsAsyncTask extends AsyncTask<Integer, Void, List<StationGUI>> {

    public static final int GET_ALL_STATIONS = 0;
    public static final int GET_STATION = 1;
    public static final int EXIST_STATION = 2;
    public static final int INSERT_STATION = 3;
    public static final int UPDATE_STATION = 4;
    public static final int REMOVE_STATION = 5;

    private OnStationTaskCompleted onStationTaskCompleted;
    private Context context;

    public StationsAsyncTask(Context context, OnStationTaskCompleted onStationTaskCompleted) {
        this.context = context;
        this.onStationTaskCompleted = onStationTaskCompleted;
    }

    @Override
    protected List<StationGUI> doInBackground(Integer... param) {
        List<StationGUI> listStations = null;
        switch (param[0]) {
            case GET_ALL_STATIONS:
                listStations = ValenbikeSQLiteOpenHelper.getInstance(context).getAllStations();
                break;
            case GET_STATION:
                StationGUI station = ValenbikeSQLiteOpenHelper.getInstance(context).getStation(param[1]);
                listStations = new ArrayList<>();
                listStations.add(station);
                break;
            case EXIST_STATION:
                ValenbikeSQLiteOpenHelper.getInstance(context).existStation(param[1]);
                break;
            case INSERT_STATION:
                ValenbikeSQLiteOpenHelper.getInstance(context).insertStation(param[1], param[2], param[3]);
                break;
            case UPDATE_STATION:
                ValenbikeSQLiteOpenHelper.getInstance(context).updateStation(param[1], param[2], param[3]);
                break;
            case REMOVE_STATION:
                ValenbikeSQLiteOpenHelper.getInstance(context).removeStation(param[1]);
                break;
        }
        return listStations;
    }

    @Override
    protected void onPostExecute(List<StationGUI> list) {
        super.onPostExecute(list);
        onStationTaskCompleted.responseStationDatabase(list);
    }
}