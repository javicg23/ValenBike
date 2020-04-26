package disca.dadm.valenbike.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import disca.dadm.valenbike.database.StationDb;

@Dao
public interface StationDao {

    @Query("SELECT * FROM station")
    List<StationDb> getStations();

    @Query("SELECT * FROM station WHERE station.is_favourite ='true'")
    List<StationDb> getFavouriteStations();

    @Query("SELECT * FROM station WHERE station.notify_bikes = 'true'")
    List<StationDb> getNotifiedBikeStations();

    @Query("SELECT * FROM station WHERE station.number = :number")
    StationDb getStationByNumber(int number);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long addStation(StationDb station);

    @Update
    void updateStation(StationDb station);

    @Delete
    void deleteStation(StationDb station);
}
