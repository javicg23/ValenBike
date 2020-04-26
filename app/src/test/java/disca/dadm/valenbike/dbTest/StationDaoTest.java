package disca.dadm.valenbike.dbTest;


import android.app.Application;
import android.content.ContentProvider;
import android.content.Context;
import android.content.pm.ApplicationInfo;

import androidx.room.Room;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import disca.dadm.valenbike.TestUtil;
import disca.dadm.valenbike.database.StationDao;
import disca.dadm.valenbike.database.StationDb;
import disca.dadm.valenbike.database.ValenbikeDatabase;
import disca.dadm.valenbike.models.Station;

public class StationDaoTest {

    private StationDao stationDao;
    private ValenbikeDatabase database;

    @Before
    public void initDb() {
//        Context context = this.getApplicationContext();
//        database = Room.inMemoryDatabaseBuilder(context, ValenbikeDatabase.class).build();
//        stationDao = database.stationDao();
    }

    @After
    public void closeDb() throws IOException {
        database.close();
    }

    @Test
    public void insertStationAndFindByNumber() throws Exception {
        StationDb stationDb = TestUtil.createStationDb(0);
        stationDao.addStation(stationDb);
        StationDb byNumber = stationDao.getStationByNumber(stationDb.getNumber());

        assert(byNumber.getNumber() == stationDb.getNumber());
    }
}
