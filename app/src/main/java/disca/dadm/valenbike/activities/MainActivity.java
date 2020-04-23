package disca.dadm.valenbike.activities;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Objects;

import disca.dadm.valenbike.R;
import disca.dadm.valenbike.fragments.DirectionsFragment;
import disca.dadm.valenbike.fragments.HistoryFragment;
import disca.dadm.valenbike.fragments.InformationFragment;
import disca.dadm.valenbike.fragments.MapFragment;
import disca.dadm.valenbike.fragments.StationsFragment;
import disca.dadm.valenbike.interfaces.DataPassListener;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, DataPassListener {

    private static final String TAG_MAP = "map";
    private static final String TAG_STATIONS = "stations";
    private static final String TAG_HISTORY = "history";
    private static final String TAG_INFORMATION = "information";
    private static final String TAG_DIRECTIONS = "directions";

    private BottomNavigationView navigationView;
    String currentFragment;
    Fragment.SavedState savedState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Sets the listener to be notified when any element of the BottomNavigationView is clicked
        navigationView = findViewById(R.id.bottomView);
        navigationView.setOnNavigationItemSelectedListener(this);

        // Display the Stations title on the ActionBar
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.app_name);

        // Starts the app with MapFragment
        changeFragment(MapFragment.newInstance(), TAG_MAP);
    }

    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        if (fragment instanceof MapFragment) {
            MapFragment mapFragment = (MapFragment) fragment;
            mapFragment.setDataPassListener(this);
        } else if (fragment instanceof DirectionsFragment) {
            DirectionsFragment directionsFragment = (DirectionsFragment) fragment;
            directionsFragment.setDataPassListener(this);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        String tag = null;

        // Determine the action to take place according to the Id of the action selected
        switch (item.getItemId()) {

            // Display MapFragment
            case R.id.navigation_map:
                // Get or create a new MapFragment
                tag = TAG_MAP;
                // Restore state of map fragment before adding it to the manager
                // otherwise it will already be created and no stated will be restored
                final Fragment fr = MapFragment.newInstance();
                fr.setInitialSavedState(savedState);
                fragment = fr;

                // Display the Stations title on the ActionBar
                Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.app_name);
                break;

            // Display StationsFragment
            case R.id.navigation_stations:
                // Get or create a new StationsFragment
                tag = TAG_STATIONS;
                fragment = getSupportFragmentManager().findFragmentByTag(tag);
                if (fragment == null) {
                    fragment = StationsFragment.newInstance();
                }
                // Display the Stations title on the ActionBar
                Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.bottom_menu_stations);
                break;

            // Display HistoryFragment
            case R.id.navigation_history:
                // Get or create a new HistoryFragment
                tag = TAG_HISTORY;
                fragment = getSupportFragmentManager().findFragmentByTag(tag);
                if (fragment == null) {
                    fragment = HistoryFragment.newInstance();
                }
                // Display the HistoryFragment title on the ActionBar
                Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.bottom_menu_history);
                break;

            // Display InformationFragment
            case R.id.navigation_information:
                // Get or create a new InformationFragment
                tag = TAG_INFORMATION;
                fragment = getSupportFragmentManager().findFragmentByTag(tag);
                if (fragment == null) {
                    fragment = InformationFragment.newInstance();
                }
                // Display the InformationFragment title on the ActionBar
                Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.bottom_menu_information);
                break;
        }

        // Save state of map fragment
        if (currentFragment == TAG_MAP) {
            savedState = getSupportFragmentManager().saveFragmentInstanceState(getSupportFragmentManager().findFragmentByTag(currentFragment));
        }
        // Replace the existing Fragment by the new one
        changeFragment(fragment, tag);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            backDirectionsButton();
        }
        return true;
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            backDirectionsButton();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public void passLocationToDirection(LatLng sourcePosition, String sourceAddress, LatLng destinationPosition, String destinationAddress) {

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_DIRECTIONS);

        if (fragment == null) {
            fragment = DirectionsFragment.newInstance();
        }
        // Save state of map fragment
        if (currentFragment == TAG_MAP) {
            savedState = getSupportFragmentManager().saveFragmentInstanceState(Objects.requireNonNull(getSupportFragmentManager().findFragmentByTag(currentFragment)));
        }

        Bundle args = new Bundle();
        args.putParcelable(DirectionsFragment.SOURCE_POSITION, sourcePosition);
        args.putString(DirectionsFragment.SOURCE_ADDRESS, sourceAddress);
        args.putParcelable(DirectionsFragment.DESTINATION_POSITION, destinationPosition);
        args.putString(DirectionsFragment.DESTINATION_ADDRESS, destinationAddress);
        fragment.setArguments(args);

        changeFragment(fragment, TAG_DIRECTIONS);
    }

    @Override
    public void passRouteToMap(ArrayList<String> responses) {

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_MAP);

        if (fragment == null) {
            fragment = MapFragment.newInstance();
        }

        fragment.setInitialSavedState(savedState);
        Bundle args = new Bundle();
        args.putStringArrayList(MapFragment.ROUTES_RESPONSES, responses);
        fragment.setArguments(args);
        changeFragment(fragment, TAG_MAP);
        restoreMapFragmentFromDirections();
    }

    private void changeFragment(Fragment fragment, String tag) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, fragment, tag)
                .commit();
        currentFragment = tag;
    }

    private void restoreMapFragmentFromDirections() {
        navigationView.setVisibility(View.VISIBLE);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
    }

    private void backDirectionsButton() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_MAP);

        if (fragment == null) {
            fragment = MapFragment.newInstance();
        }
        fragment.setInitialSavedState(savedState);

        changeFragment(fragment, TAG_MAP);
        restoreMapFragmentFromDirections();
    }
}
