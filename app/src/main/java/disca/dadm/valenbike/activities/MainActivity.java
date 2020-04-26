package disca.dadm.valenbike.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Objects;

import disca.dadm.valenbike.R;
import disca.dadm.valenbike.database.ValenbikeDatabase;
import disca.dadm.valenbike.fragments.DirectionsFragment;
import disca.dadm.valenbike.fragments.HistoryFragment;
import disca.dadm.valenbike.fragments.InformationFragment;
import disca.dadm.valenbike.fragments.MapFragment;
import disca.dadm.valenbike.fragments.StationsFragment;
import disca.dadm.valenbike.interfaces.DataPassListener;
import disca.dadm.valenbike.tasks.StationsDbAsyncTask;
import disca.dadm.valenbike.utils.Tools;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, DataPassListener {

    public ValenbikeDatabase database;
    private static final String TAG_MAP = "map";
    private static final String TAG_STATIONS = "stations";
    private static final String TAG_HISTORY = "history";
    private static final String TAG_INFORMATION = "information";
    private static final String TAG_DIRECTIONS = "directions";
    public static final String CHANNEL_ID = "channel";

    private BottomNavigationView navigationView;
    private String currentFragment;
    private Fragment.SavedState savedState;
    private AlertDialog.Builder alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Sets the listener to be notified when any element of the BottomNavigationView is clicked
        navigationView = findViewById(R.id.bottomView);
        navigationView.setOnNavigationItemSelectedListener(this);

        database = ValenbikeDatabase.getInstance(getApplicationContext());
        if (database != null) {
            Log.d("DEBUG", "SE HA CREADO LA BASE DE DATOS");
        }


        // Display the Stations title on the ActionBar
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.app_name);
        alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.alert_title);
        alert.setMessage(R.string.alert_message);
        alert.setCancelable(false);
        alert.setPositiveButton(R.string.alert_try_again, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showDialogStartMap();
            }
        });
        alert.setNegativeButton(R.string.alert_close, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAndRemoveTask();
            }
        });
        showDialogStartMap();

        // Creating notification channel
        createNotificationChannel();
    }

    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        if (fragment instanceof MapFragment) {
            MapFragment mapFragment = (MapFragment) fragment;
            mapFragment.setDataPassListener(this);
        } else if (fragment instanceof DirectionsFragment) {
            DirectionsFragment directionsFragment = (DirectionsFragment) fragment;
            directionsFragment.setDataPassListener(this);
        } else if (fragment instanceof StationsFragment) {
            StationsFragment stationsFragment = (StationsFragment) fragment;
            stationsFragment.setDataPassListener(this);
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
        if (!currentFragment.equals(tag)) {
            // Save state of map fragment
            if (currentFragment.equals(TAG_MAP)) {
                savedState = getSupportFragmentManager().saveFragmentInstanceState(Objects.requireNonNull(getSupportFragmentManager().findFragmentByTag(currentFragment)));
            }
            // Replace the existing Fragment by the new one
            changeFragment(fragment, tag);
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            backDirectionsButton();
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
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
        if (currentFragment.equals(TAG_MAP)) {
            savedState = getSupportFragmentManager().saveFragmentInstanceState(Objects.requireNonNull(getSupportFragmentManager().findFragmentByTag(currentFragment)));
        }

        changeTitleAndMenuCheck();

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

        changeTitleAndMenuCheck();

        Bundle args = new Bundle();
        args.putStringArrayList(MapFragment.ROUTES_RESPONSES, responses);
        fragment.setArguments(args);
        changeFragment(fragment, TAG_MAP);
        restoreMapFragmentFromDirections();
    }

    @Override
    public void passStationToMap(int id) {

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_MAP);

        if (fragment == null) {
            fragment = MapFragment.newInstance();
        }

        fragment.setInitialSavedState(savedState);

        changeTitleAndMenuCheck();

        Bundle args = new Bundle();
        args.putInt(StationsFragment.STATION_RESPONSE, id);
        fragment.setArguments(args);
        changeFragment(fragment, TAG_MAP);
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
        if (!currentFragment.equals(TAG_MAP)) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_MAP);

            if (fragment == null) {
                fragment = MapFragment.newInstance();
            }
            fragment.setInitialSavedState(savedState);

            changeTitleAndMenuCheck();

            changeFragment(fragment, TAG_MAP);
            restoreMapFragmentFromDirections();
        } else {
            moveTaskToBack(true);
        }
    }

    private void showDialogStartMap() {
        if (!Tools.isNetworkConnected(this)) {
            alert.create().show();
        } else {
            // Starts the app with MapFragment
            changeFragment(MapFragment.newInstance(), TAG_MAP);
        }
    }

    private void changeTitleAndMenuCheck() {
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.app_name);
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, getString(R.string.channel_name), NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(getString(R.string.channel_description));

            NotificationManager manager = getSystemService(NotificationManager.class);
            Objects.requireNonNull(manager).createNotificationChannel(channel);
        }
    }
}