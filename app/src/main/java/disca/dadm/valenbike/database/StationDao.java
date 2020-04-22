package disca.dadm.valenbike.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import disca.dadm.valenbike.database.Station;

@Dao
public interface StationDao {

    @Query("SELECT * FROM station")
    List<Station> getStations();

    @Query("SELECT * FROM station WHERE station.is_favourite ='true'")
    List<Station> getFavouriteStations();

    @Query("SELECT * FROM station WHERE station.notifyBikes = 'true'")
    List<Station> getNotifiedBikeStations();

    @Query("SELECT * FROM station WHERE station.notifyGaps = 'true'")
    List<Station> getNotifiedGapStations();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long addStation(Station station);

    @Update
    void updateStation(Station station);

    @Delete
    void deleteStation(Station station);
}
