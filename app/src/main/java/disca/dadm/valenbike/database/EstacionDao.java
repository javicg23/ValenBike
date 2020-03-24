package disca.dadm.valenbike.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface EstacionDao {

    @Query("SELECT * FROM estacion")
    List<Estacion> getEstaciones();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long addEstacion(Estacion estacion);

    @Update
    void updateEstacion(Estacion estacion);

    @Delete
    void deleteEstacion(Estacion estacion);
}
