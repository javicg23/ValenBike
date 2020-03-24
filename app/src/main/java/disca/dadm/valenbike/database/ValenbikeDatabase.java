package disca.dadm.valenbike.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Viaje.class, Estacion.class}, version = 1)
public abstract class ValenbikeDatabase extends RoomDatabase {

    //Patron Singleton
    private static ValenbikeDatabase valenbikeDatabase;

    public synchronized static ValenbikeDatabase getInstance(Context context) {
        if (valenbikeDatabase == null) {
            valenbikeDatabase = Room
                    .databaseBuilder(context, ValenbikeDatabase.class, "valenbikes")
                    .build();
        }

        return valenbikeDatabase;
    }

    public abstract ViajeDao viajeDao();
    public abstract EstacionDao estacionDao();
}
