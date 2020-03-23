package disca.dadm.valenbike.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import disca.dadm.valenbike.R;
import disca.dadm.valenbike.adapters.InformationAdapter;
import disca.dadm.valenbike.models.Information;

public class FunctioningFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Information> informationList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_functioning, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewFunctioning);

        initData();
        initRecyclerView();

        return view;
    }

    private void initData() {
        informationList = new ArrayList<>();
        informationList.add(new Information(getResources().getString(R.string.cardview_functioning_title1), getResources().getString(R.string.cardview_functioning_body1)));
        informationList.add(new Information(getResources().getString(R.string.cardview_functioning_title2), getResources().getString(R.string.cardview_functioning_body2)));
    }

    private void initRecyclerView() {
        InformationAdapter informationAdapter = new InformationAdapter(informationList);
        recyclerView.setAdapter(informationAdapter);
    }
}