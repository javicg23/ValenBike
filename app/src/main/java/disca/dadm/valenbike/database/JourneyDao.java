package disca.dadm.valenbike.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface JourneyDao {

    @Query("SELECT * FROM journey")
    List<Journey> getJourneys();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long addJourney(Journey journey);

    @Update
    void updateJourney(Journey journey);

    @Delete
    void deleteJourney(Journey journey);
}