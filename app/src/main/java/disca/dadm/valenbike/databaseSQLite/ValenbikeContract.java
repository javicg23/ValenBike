package disca.dadm.valenbike.databaseSQLite;

import android.provider.BaseColumns;

public class ValenbikeContract {

    private ValenbikeContract() {

    }

    public static class ValenbikeBase implements BaseColumns {
        public static final String DB_NAME = "valenbike_database";
        public static final int DB_VERSION = 1;
        public static final String JOURNEY = "historial";
        public static final String JOURNEY_COLUMN_ID = "id";
        public static final String JOURNEY_COLUMN_ORIGIN = "origen";
        public static final String JOURNEY_COLUMN_DESTINY = "destino";
        public static final String JOURNEY_COLUMN_DISTANCE = "distancia";
        public static final String JOURNEY_COLUMN_PRICE = "precio";
        public static final String JOURNEY_COLUMN_DURATION = "duracion";
        public static final String JOURNEY_COLUMN_DATE = "fecha";
        public static final String STATION = "estacion";
        public static final String STATION_COLUMN_NUMBER = "numero";
        public static final String STATION_COLUMN_FAVOURITE = "favorita";
        public static final String STATION_COLUMN_REMINDER = "recordar";
    }
}
