package disca.dadm.valenbike;

import java.util.ArrayList;
import java.util.List;

import disca.dadm.valenbike.models.Station;

public class test {

    static List<Station> stations;

    public static List<Station> getStationList() {
        stations = new ArrayList<>();
        Station neew = createStation("Porta de la Mar", "Calle Colón", 15, 25, true, false);
        stations.add(neew);

        return stations;
    }

    private static Station createStation(String name, String address, int numBikes, int numGaps, boolean favourite, boolean notify) {
        return new Station(name,address,numBikes,numGaps,favourite,notify);
    }
}
