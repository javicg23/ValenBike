package disca.dadm.valenbike.ui.stations;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class StationsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public StationsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is stations fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}