package disca.dadm.valenbike.interfaces;

import java.util.List;

import disca.dadm.valenbike.models.Journey;

public interface OnJourneyTaskCompleted {
    void responseJourneyDatabase(List<Journey> list);
}
