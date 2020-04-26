package disca.dadm.valenbike.tasks;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import disca.dadm.valenbike.databaseSQLite.ValenbikeSQLiteOpenHelper;
import disca.dadm.valenbike.interfaces.OnJourneyTaskCompleted;
import disca.dadm.valenbike.models.Journey;

public class JourneyAsyncTask extends AsyncTask<String, Void, List<Journey>> {

    public static final String GET_ALL_JOURNEYS = "0";
    public static final String INSERT_JOURNEY = "1";
    public static final String REMOVE_JOURNEY = "2";
    public static final String REMOVE_ALL_JOURNEY = "3";

    private OnJourneyTaskCompleted onJourneyTaskCompleted;
    private Context context;

    public JourneyAsyncTask(Context context, OnJourneyTaskCompleted onJourneyTaskCompleted) {
        this.context = context;
        this.onJourneyTaskCompleted = onJourneyTaskCompleted;
    }

    @Override
    protected List<Journey> doInBackground(String... param) {
        List<Journey> listJourneys = null;
        switch(param[0]) {
            case GET_ALL_JOURNEYS:
                listJourneys = ValenbikeSQLiteOpenHelper.getInstance(context).getAllJourneys();
                break;
            case INSERT_JOURNEY:
                ValenbikeSQLiteOpenHelper.getInstance(context).insertJourney(param[1], param[2], Double.parseDouble(param[3]), Double.parseDouble(param[4]), Integer.parseInt(param[5]), param[6]);
                break;
            case REMOVE_JOURNEY:
                ValenbikeSQLiteOpenHelper.getInstance(context).removeJourney(Integer.parseInt(param[1]));
                break;
            case REMOVE_ALL_JOURNEY:
                ValenbikeSQLiteOpenHelper.getInstance(context).removeAllJourneys();
                break;
        }
        return listJourneys;
    }

    @Override
    protected void onPostExecute(List<Journey> list) {
        super.onPostExecute(list);
        onJourneyTaskCompleted.responseJourneyDatabase(list);
    }
}