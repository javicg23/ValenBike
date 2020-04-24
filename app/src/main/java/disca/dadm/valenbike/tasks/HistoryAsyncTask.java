package disca.dadm.valenbike.tasks;

import android.os.AsyncTask;

import java.lang.ref.WeakReference;
import java.util.List;

import disca.dadm.valenbike.database.ValenbikeDatabase;
import disca.dadm.valenbike.fragments.HistoryFragment;
import disca.dadm.valenbike.database.Journey;

public class HistoryAsyncTask extends AsyncTask<Void, Void, List<Journey>> {
    private WeakReference<HistoryFragment> activity;

    public HistoryAsyncTask(HistoryFragment historyFragment) {
        this.activity = new WeakReference<>(historyFragment);
   }

    @Override
    protected List<Journey> doInBackground(Void... param) {

        List<Journey> list =  ValenbikeDatabase.getInstance(this.activity.get().getContext()).journeyDao().getJourneys();
        return list;
    }

    @Override
    protected void onPostExecute(List<Journey> list) {
        super.onPostExecute(list);
        if (this.activity.get() != null) this.activity.get().getList(list);
    }
}