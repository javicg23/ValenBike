//package disca.dadm.valenbike.database;
//
//import android.content.Context;
//
//import androidx.room.Database;
//import androidx.room.Room;
//import androidx.room.RoomDatabase;
//import androidx.room.TypeConverter;
//import androidx.room.TypeConverters;
//
//@Database(entities = {Journey.class, Stations.class}, version = 1, exportSchema = false)
//@TypeConverters({Converters.class})
//public abstract class ValenbikeDatabase extends RoomDatabase {
//
//    //Patron Singleton
//    private static ValenbikeDatabase valenbikeDatabase;
//
//    public synchronized static ValenbikeDatabase getInstance(Context context) {
//        if (valenbikeDatabase == null) {
//            valenbikeDatabase = Room
//                    .databaseBuilder(context, ValenbikeDatabase.class, "valenbikes")
//                    .build();
//        }
//
//        return valenbikeDatabase;
//    }
//
//    public abstract JourneyDao journeyDao();
//    public abstract StationDao stationDao();
//}