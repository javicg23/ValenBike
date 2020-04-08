package disca.dadm.valenbike.tasks;

import java.util.List;

import disca.dadm.valenbike.models.Station;

public interface OnTaskCompleted {
    void onTaskCompleted(List<Station> stations);
}
