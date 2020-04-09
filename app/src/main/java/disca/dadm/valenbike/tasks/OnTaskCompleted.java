package disca.dadm.valenbike.tasks;

import java.util.List;

import disca.dadm.valenbike.models.Station;

public interface OnTaskCompleted {
    void receivedAllStations(List<Station> stations);
    void receivedStation(Station station);
}
