package disca.dadm.valenbike.fragments;

import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import disca.dadm.valenbike.R;

public class FunctioningFragment extends Fragment {

    private ConstraintLayout layoutExpandFunctioning1, layoutExpandFunctioning2;
    private Button btnFunctioningDropDown1, btnFunctioningDropDown2;
    private CardView cardViewFunctioning1, cardViewFunctioning2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_functioning, container, false);
        /*layoutExpandFunctioning1 = view.findViewById(R.id.layoutExpandFunctioning1);
        cardViewFunctioning1 = view.findViewById(R.id.cardViewFunctioning1);
        btnFunctioningDropDown1 = view.findViewById(R.id.btnFunctioningDropDown1);
        layoutExpandFunctioning2 = view.findViewById(R.id.layoutExpandFunctioning2);
        cardViewFunctioning2 = view.findViewById(R.id.cardViewFunctioning2);
        btnFunctioningDropDown2 = view.findViewById(R.id.btnFunctioningDropDown2);

        btnFunctioningDropDown1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layoutExpandFunctioning1.getVisibility() == View.GONE) {
                    TransitionManager.beginDelayedTransition(cardViewFunctioning1, new AutoTransition());
                    layoutExpandFunctioning1.setVisibility(View.VISIBLE);
                    btnFunctioningDropDown1.setBackgroundResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                } else {
                    TransitionManager.beginDelayedTransition(cardViewFunctioning1, new AutoTransition());
                    layoutExpandFunctioning1.setVisibility(View.GONE);
                    btnFunctioningDropDown1.setBackgroundResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                }
            }
        });

        btnFunctioningDropDown2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layoutExpandFunctioning2.getVisibility() == View.GONE) {
                    TransitionManager.beginDelayedTransition(cardViewFunctioning2, new AutoTransition());
                    layoutExpandFunctioning2.setVisibility(View.VISIBLE);
                    btnFunctioningDropDown2.setBackgroundResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                } else {
                    TransitionManager.beginDelayedTransition(cardViewFunctioning2, new AutoTransition());
                    layoutExpandFunctioning2.setVisibility(View.GONE);
                    btnFunctioningDropDown2.setBackgroundResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                }
            }
        });*/

        return view;
    }
}