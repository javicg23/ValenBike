package disca.dadm.valenbike.databaseSQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import disca.dadm.valenbike.models.Journey;
import disca.dadm.valenbike.models.StationGUI;

public class ValenbikeSQLiteOpenHelper extends SQLiteOpenHelper {

    public static int ENABLED_FAVREMIND = 1;
    public static int DISABLED_FAVREMIND = 0;

    private static ValenbikeSQLiteOpenHelper valenbikeSQLiteOpenHelper;
    private String tableStation = ValenbikeContract.ValenbikeBase.STATION;
    private String stationNumber = ValenbikeContract.ValenbikeBase.STATION_COLUMN_NUMBER;
    private String stationFavourite = ValenbikeContract.ValenbikeBase.STATION_COLUMN_FAVOURITE;
    private String stationReminder = ValenbikeContract.ValenbikeBase.STATION_COLUMN_REMINDER;
    private String tableJourney = ValenbikeContract.ValenbikeBase.JOURNEY;
    private String journeyColumnId = ValenbikeContract.ValenbikeBase.JOURNEY_COLUMN_ID;
    private String journeyColumnOrigin = ValenbikeContract.ValenbikeBase.JOURNEY_COLUMN_ORIGIN;
    private String journeyColumnDestiny = ValenbikeContract.ValenbikeBase.JOURNEY_COLUMN_DESTINY;
    private String journeyColumnDistance = ValenbikeContract.ValenbikeBase.JOURNEY_COLUMN_DISTANCE;
    private String journeyColumnPrice = ValenbikeContract.ValenbikeBase.JOURNEY_COLUMN_PRICE;
    private String journeyColumnDuration = ValenbikeContract.ValenbikeBase.JOURNEY_COLUMN_DURATION;
    private String journeyColumnDate = ValenbikeContract.ValenbikeBase.JOURNEY_COLUMN_DATE;


    private ValenbikeSQLiteOpenHelper(Context context) {
        super(context, ValenbikeContract.ValenbikeBase.DB_NAME, null, 1);
    }

    public synchronized static ValenbikeSQLiteOpenHelper getInstance(Context context) {
        if (valenbikeSQLiteOpenHelper == null) {
            valenbikeSQLiteOpenHelper =  new ValenbikeSQLiteOpenHelper(context);
        }
        return valenbikeSQLiteOpenHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + tableJourney + "("
                + journeyColumnId + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                + journeyColumnOrigin + " TEXT NOT NULL, "
                + journeyColumnDestiny + " TEXT NOT NULL, "
                + journeyColumnDistance + " DOUBLE NOT NULL, "
                + journeyColumnPrice + " DOUBLE NOT NULL, "
                + journeyColumnDuration + " INT NOT NULL, "
                + journeyColumnDate + " TEXT NOT NULL );");

        db.execSQL("CREATE TABLE " + tableStation + "("
                + stationNumber + " INTEGER PRIMARY KEY NOT NULL, "
                + stationFavourite + " INTEGER NOT NULL, "
                + stationReminder + " INTEGER NOT NULL);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public ArrayList<StationGUI> getAllStations() {

        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.query(tableStation, new String[]{stationNumber, stationFavourite, stationReminder}, null, null, null, null, null);

        ArrayList<StationGUI> stations = new ArrayList<>();
        while(cursor.moveToNext()) {
            StationGUI station = new StationGUI(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2));
            stations.add(station);
        }
        cursor.close();
        database.close();
        return stations;
    }

    public StationGUI getStation(int number) {
        StationGUI station = new StationGUI(number, DISABLED_FAVREMIND, DISABLED_FAVREMIND);

        if (existStation(number)) {
            SQLiteDatabase database = getReadableDatabase();

            Cursor cursor = database.query(tableStation, new String[]{stationNumber, stationFavourite, stationReminder}, stationNumber + "=?", new String[]{String.valueOf(number)}, null, null, null);
            while(cursor.moveToNext()) {
                station = new StationGUI(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2));
            }
            cursor.close();
            database.close();
        }
        return station;
    }

    public ArrayList<Journey> getAllJourneys() {

        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.query(tableJourney, new String[]{journeyColumnId, journeyColumnOrigin, journeyColumnDestiny, journeyColumnDistance,
                journeyColumnPrice, journeyColumnDuration, journeyColumnDate}, null, null, null, null, null);

        ArrayList<Journey> journeys = new ArrayList<>();
        while(cursor.moveToNext()) {
            Journey journey = new Journey(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getDouble(3),
                    cursor.getDouble(4), cursor.getInt(5), cursor.getString(6));
            journeys.add(journey);
        }

        cursor.close();
        database.close();
        return journeys;
    }

    public boolean existStation(int number) {

        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.query(tableStation, null, stationNumber + "=?", new String[]{String.valueOf(number)}, null, null, null, null);

        boolean res = cursor.getCount() > 0;
        cursor.close();
        database.close();

        return res;
    }

    public boolean insertStation(int number, int favourite, int reminder) {
        boolean res = false;

        if (!existStation(number)) {

            SQLiteDatabase database = getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(stationNumber, number);
            values.put(stationFavourite, favourite);
            values.put(stationReminder, reminder);
            database.insert(tableStation, null, values);
            res = true;

            database.close();
        } else {
            updateStation(number, favourite, reminder);
        }
        return res;
    }

    public boolean updateStation(int number, int favourite, int reminder) {
        if (favourite == DISABLED_FAVREMIND && reminder == DISABLED_FAVREMIND) {
            removeStation(number);
        } else {
            SQLiteDatabase database = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(stationFavourite, favourite);
            values.put(stationReminder, reminder);
            database.update(tableStation, values, stationNumber + "=?", new String[]{String.valueOf(number)});
            database.close();
        }

        return true;
    }

    public boolean removeStation(int number) {
        boolean res = false;
        if (existStation(number)) {
            SQLiteDatabase database = getWritableDatabase();

            database.delete(tableStation, stationNumber + "=?", new String[] {String.valueOf(number)});
            database.close();
            res = true;
        }
        return res;
    }

    public int insertJourney(String origin, String destiny, double distance, double price, int duration, String date) {
        SQLiteDatabase database = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(journeyColumnOrigin, origin);
        values.put(journeyColumnDestiny, destiny);
        values.put(journeyColumnDistance, distance);
        values.put(journeyColumnPrice, price);
        values.put(journeyColumnDuration, duration);
        values.put(journeyColumnDate, date);
        long id = database.insert(tableJourney, null, values);

        database.close();

        return (int) id;
    }

    public boolean removeJourney(int id) {
        SQLiteDatabase database = getWritableDatabase();

        database.delete(tableJourney, journeyColumnId + "=?", new String[] {String.valueOf(id)});
        database.close();
        return true;
    }

    public boolean removeAllJourneys() {
        SQLiteDatabase database = getWritableDatabase();
        database.delete(tableJourney,null,null);
        database.close();
        return true;
    }
}
