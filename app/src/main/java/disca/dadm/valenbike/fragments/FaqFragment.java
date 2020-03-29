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

public class FaqFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Information> informationList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_faq, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewFAQ);

        initData();
        initRecyclerView();

        return view;
    }

    private void initData() {
        informationList = new ArrayList<>();
        informationList.add(new Information(getResources().getString(R.string.cardview_faq_title1), getResources().getString(R.string.cardview_faq_body1)));
        informationList.add(new Information(getResources().getString(R.string.cardview_faq_title2), getResources().getString(R.string.cardview_faq_body2)));
        informationList.add(new Information(getResources().getString(R.string.cardview_faq_title3), getResources().getString(R.string.cardview_faq_body3)));
        informationList.add(new Information(getResources().getString(R.string.cardview_faq_title4), getResources().getString(R.string.cardview_faq_body4)));
        informationList.add(new Information(getResources().getString(R.string.cardview_faq_title5), getResources().getString(R.string.cardview_faq_body5)));
        informationList.add(new Information(getResources().getString(R.string.cardview_faq_title6), getResources().getString(R.string.cardview_faq_body6)));
        informationList.add(new Information(getResources().getString(R.string.cardview_faq_title7), getResources().getString(R.string.cardview_faq_body7)));
        informationList.add(new Information(getResources().getString(R.string.cardview_faq_title8), getResources().getString(R.string.cardview_faq_body8)));
        informationList.add(new Information(getResources().getString(R.string.cardview_faq_title9), getResources().getString(R.string.cardview_faq_body9)));
    }

    private void initRecyclerView() {
        InformationAdapter informationAdapter = new InformationAdapter(informationList);
        recyclerView.setAdapter(informationAdapter);
    }
}
