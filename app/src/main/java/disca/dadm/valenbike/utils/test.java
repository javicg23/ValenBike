package disca.dadm.valenbike.utils;

import java.util.ArrayList;
import java.util.List;

import disca.dadm.valenbike.models.Position;
import disca.dadm.valenbike.models.StationGUI;

public class test {

    public static List<StationGUI> getStations() {
        List<StationGUI> stations = new ArrayList<>();
        Position pos =  new Position(39.469872, -0.370844);
        StationGUI station = new StationGUI(1234, "Estación Colón", "Calle Colón", "Calle Colón, 29", pos, true , true, 20, 5, 15, "Open", 444);
        stations.add(station);

        return stations;
    }
}
