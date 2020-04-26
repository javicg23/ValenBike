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
                    .build();
        }
        return valenbikeDatabase;
    }

    public abstract JourneyDao journeyDao();
    public abstract StationDao stationDao();
}