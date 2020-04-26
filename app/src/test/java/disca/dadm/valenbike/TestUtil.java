package disca.dadm.valenbike;

import disca.dadm.valenbike.database.StationDb;

public class TestUtil {

    public static StationDb createStationDb(int option) {
        StationDb newStation = new StationDb();

        switch (option) {
            case 0:
                newStation = new StationDb(22, true, true);
                break;
            case 1:
                newStation = new StationDb(23, true, false);
                break;
            case 2:
                newStation = new StationDb(25, false, true);
                break;
            case 3:
                newStation = new StationDb(11, false, false);
            default:
                break;
        }

        return newStation;
    }
}
