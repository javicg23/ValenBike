package disca.dadm.valenbike.interfaces;

import java.util.List;

import disca.dadm.valenbike.models.Station;

public interface OnPetitionTaskCompleted {
    void receivedAllStations(List<Station> stations);

    void receivedStation(Station station);
}
