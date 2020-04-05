package disca.dadm.valenbike.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import disca.dadm.valenbike.R;
import disca.dadm.valenbike.adapters.HistoryAdapter;
import disca.dadm.valenbike.models.History;

public class HistoryFragment extends Fragment {

    private RecyclerView recycler;
    private List<History> historyList;

    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        recycler = view.findViewById(R.id.recyclerHistory);
        TextView totalTime = view.findViewById(R.id.totalTime);
        //String text = getTotalTimeUser(); ---BACK---
        totalTime.setText(totalTime.getText() + ": 20");
        TextView totalMoney = view.findViewById(R.id.totalMoney);
        //text = getTotalMoneyUser(); ---BACK---
        totalMoney.setText(totalMoney.getText() + ": 2€");

        initData();
        initRecyclerView();

        return view;
    }

    private void initData() {
        historyList = new ArrayList<History>();
        historyList.add(new History("21/10/2020", "20'","Torrefiel", "UPV", "2km", "15€"));

    }

    private void initRecyclerView() {
        HistoryAdapter historyAdapter = new HistoryAdapter(historyList);
        recycler.setAdapter(historyAdapter);
    }
}
