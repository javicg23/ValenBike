package disca.dadm.valenbike.database;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;

@Database(entities = {Journey.class, StationDb.class}, version = 2, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class ValenbikeDatabase extends RoomDatabase {

    // Singleton
    private static ValenbikeDatabase valenbikeDatabase;

    public synchronized static ValenbikeDatabase getInstance(final Context context) {
        if (valenbikeDatabase == null) {
            valenbikeDatabase = Room
                    .databaseBuilder(context, ValenbikeDatabase.class, "valenbikes")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return valenbikeDatabase;
    }

    public abstract JourneyDao journeyDao();
    public abstract StationDao stationDao();

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(valenbikeDatabase).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private JourneyDao journeyDao;
        SimpleDateFormat dateFormat;

        private PopulateDbAsyncTask(ValenbikeDatabase db) {
            journeyDao = db.journeyDao();
            dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        }

        @Override
        protected Void doInBackground(Void...param) {
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
}