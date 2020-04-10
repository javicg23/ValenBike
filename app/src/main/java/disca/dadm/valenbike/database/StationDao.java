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
//import disca.dadm.valenbike.models.StationGUI;
//
//@Dao
//public interface StationDao {
//
//    @Query("SELECT * FROM station")
//    List<StationGUI> getStations();
//
//    @Query("SELECT * FROM station WHERE station.is_favourite ='true'")
//    List<StationGUI> getFavouriteStations();
//
//    @Query("SELECT * FROM station WHERE station.notify = 'true'")
//    List<StationGUI> getNotifiedStations();
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    long addStation(StationGUI station);
//
//    @Update
//    void updateStation(StationGUI station);
//
//    @Delete
//    void deleteStation(StationGUI station);
//}
