package disca.dadm.valenbike.tasks;

import android.os.AsyncTask;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import disca.dadm.valenbike.activities.MainActivity;
import disca.dadm.valenbike.database.Journey;
import disca.dadm.valenbike.database.JourneyDao;
import disca.dadm.valenbike.database.ValenbikeDatabase;

public class PrepopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
    private WeakReference<MainActivity> activity;

    public PrepopulateDbAsyncTask(MainActivity activity) {
        this.activity = new WeakReference<>(activity);
    }

    @Override
    protected Void doInBackground(Void... param) {
        JourneyDao journeyDao =  ValenbikeDatabase.getInstance(this.activity.get().getApplicationContext()).journeyDao();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            journeyDao.addJourney(new Journey(12, 3.22, dateFormat.parse("12/04/2020"), 2300.0, "Ramon Llul - Serpis", "Pintor rafael solves"));
            journeyDao.addJourney(new Journey(5, 2.22, dateFormat.parse("22/02/2020"), 800.0, "UPV caminos", "Blasco Ibañez - Yecla"));
            journeyDao.addJourney(new Journey(30, 8.15, dateFormat.parse("03/03/2020"), 6500.0, "Plaza salvador soria", "Ramon Llul - Serpis"));
            journeyDao.addJourney(new Journey(22, 6.76, dateFormat.parse("22/04/2020"), 4500.0, "Puerto Rico - Cuba", "Ángel Villena - Ausias March"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
