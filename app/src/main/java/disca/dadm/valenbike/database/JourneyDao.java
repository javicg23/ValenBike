//package disca.dadm.valenbike.database;
//
//import androidx.room.Dao;
//import androidx.room.Delete;
//import androidx.room.Insert;
//import androidx.room.OnConflictStrategy;
//import androidx.room.Query;
//import androidx.room.Update;
//
//import java.util.List;
//
//@Dao
//public interface ViajeDao {
//
//        @Query("SELECT * FROM viaje")
//        List<Estacion> getViajes();
//
//        @Insert(onConflict = OnConflictStrategy.REPLACE)
//        long addViaje(Viaje viaje);
//
//        @Update
//        void updateViaje(Viaje viaje);
//
//        @Delete
//        void deleteViaje(Viaje viaje);
//}
