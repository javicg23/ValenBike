package disca.dadm.valenbike.interfaces;

import java.util.List;

import disca.dadm.valenbike.models.StationGUI;

public interface OnStationTaskCompleted {
    void responseStationDatabase(List<StationGUI> list);
}
