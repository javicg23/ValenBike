package disca.dadm.valenbike.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Objects;

import disca.dadm.valenbike.R;

public class InformationFragment extends Fragment {

    public InformationFragment() {
        // Required empty public constructor
    }

    public static InformationFragment newInstance() {
        InformationFragment fragment = new InformationFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_information, container, false);
        ViewPager2 viewPager2 = view.findViewById(R.id.pager2);
        viewPager2.setAdapter(new CustomFragmentStateAdapter(Objects.requireNonNull(InformationFragment.this.getActivity())));

        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText("Funcionamiento");
                        break;
                    case 1:
                        tab.setText("FAQ");
                        break;
                    case 2:
                        tab.setText("Contacto");
                }
            }
        }).attach();
        return view;
    }

    private static class CustomFragmentStateAdapter extends FragmentStateAdapter {
        CustomFragmentStateAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }
        @NonNull
        @Override
        public Fragment createFragment(int position) {
            Fragment result = null;
            switch (position) {
                case 0:
                    result = new FunctioningFragment();
                    break;
                case 1:
                    result = new FaqFragment();
                    break;
                case 2:
                    result = new ContactFragment();
            }
            return Objects.requireNonNull(result);
        }
        @Override
        public int getItemCount() {
            return 3;
        }
    }
}
