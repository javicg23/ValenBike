package disca.dadm.valenbike.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.nio.file.Watchable;
import java.util.Objects;

import disca.dadm.valenbike.R;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static disca.dadm.valenbike.utils.Tools.showSnackBar;

public class DirectionsFragment extends Fragment {
    public static final String ROUTE = "route";
    public static final String SOURCE_POSITION = "source_position";
    public static final String SOURCE_ADDRESS = "source_address";
    public static final String DESTINATION_POSITION = "destination_position";
    public static final String DESTINATION_ADDRESS = "destination_address";

    private View rootView;
    private String locationAddress;
    private int routeDirections;

    private TextInputEditText sourceText, destinationText;
    private ImageButton swapRouteButton;
    private CheckBox locationCheck;
    private FloatingActionButton fabDirections;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle args = getArguments();
        if (args != null) {
            routeDirections = args.getInt(ROUTE);
            locationAddress = args.getString(SOURCE_ADDRESS);
            sourceText.setText(locationAddress);
            destinationText.setText(args.getString(DESTINATION_ADDRESS));

            if (locationAddress.equals("")) {
                locationCheck.setBackground(getResources().getDrawable(R.drawable.ic_my_location_light_grey_24dp, null));
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_directions, container, false);
        // hide bottom navigation and show back button on actionBar
        BottomNavigationView navigationView = getActivity().findViewById(R.id.bottomView);
        navigationView.setVisibility(View.GONE);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sourceText = rootView.findViewById(R.id.directionsSource);
        destinationText = rootView.findViewById(R.id.directionsDestination);
        locationCheck = rootView.findViewById(R.id.directionsLocation);
        swapRouteButton = rootView.findViewById(R.id.directionsSwapRoute);
        fabDirections = rootView.findViewById(R.id.directionsFabNavigation);

        initListeners();

        return rootView;
    }

    private void initListeners() {
        // listener for hide keyword if lose the focus
        rootView.findViewById(R.id.layoutDirections).setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) Objects.requireNonNull(getActivity()).getSystemService(INPUT_METHOD_SERVICE);
                View focus = getActivity().getCurrentFocus();
                if(imm != null && focus != null) {
                    imm.hideSoftInputFromWindow(focus.getWindowToken(), 0);
                }

                return true;
            }
        });

        sourceText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.toString().equals(locationAddress) && !locationAddress.equals("")) {
                    locationCheck.setBackground(getResources().getDrawable(R.drawable.ic_my_location_accent_24dp, null));
                } else {
                    locationCheck.setBackground(getResources().getDrawable(R.drawable.ic_my_location_light_grey_24dp, null));
                }
            }
        });

        locationCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!locationAddress.equals("")) {
                    sourceText.setText(locationAddress);
                    if (isChecked) {
                        locationCheck.setBackground(getResources().getDrawable(R.drawable.ic_my_location_accent_24dp, null));
                    } else {
                        locationCheck.setBackground(getResources().getDrawable(R.drawable.ic_my_location_light_grey_24dp, null));
                    }
                } else {
                    showSnackBar(rootView, false, getString(R.string.directions_no_gps_location));
                }
            }
        });

        swapRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String aux = destinationText.getText().toString();
                destinationText.setText(sourceText.getText().toString());
                sourceText.setText(aux);
            }
        });

        fabDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sourceText.getText().toString().trim().equals("") || destinationText.getText().toString().trim().equals("")){
                    showSnackBar(rootView, false, getString(R.string.directions_no_source_destination));
                } else {
                    showSnackBar(rootView, false, "buscar ruta");
                }
            }
        });

    }


}
