package disca.dadm.valenbike.activities;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import disca.dadm.valenbike.R;
import disca.dadm.valenbike.fragments.HistoryFragment;
import disca.dadm.valenbike.fragments.InformationFragment;
import disca.dadm.valenbike.fragments.MapFragment;
import disca.dadm.valenbike.fragments.StationsFragment;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Sets the listener to be notified when any element of the BottomNavigationView is clicked
        ((BottomNavigationView) findViewById(R.id.bottomView)).setOnNavigationItemSelectedListener(this);

        //hide action bar
        getSupportActionBar().hide();

        // Starts the app with MapFragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, new MapFragment())
                .commit();
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
                tag = "Map";
                fragment = getSupportFragmentManager().findFragmentByTag(tag);
                if (fragment == null) {
                    fragment = new MapFragment();
                }
                //hide action bar
                getSupportActionBar().hide();
                break;

            // Display StationsFragment
            case R.id.navigation_stations:
                // Get or create a new StationsFragment
                tag = "Stations";
                fragment = getSupportFragmentManager().findFragmentByTag(tag);
                if (fragment == null) {
                    fragment = new StationsFragment();
                }
                //show action bar
                getSupportActionBar().show();
                // Display the Stations title on the ActionBar
                getSupportActionBar().setTitle(R.string.bottom_menu_stations);
                break;

            // Display HistoryFragment
            case R.id.navigation_history:
                // Get or create a new HistoryFragment
                tag = "History";
                fragment = getSupportFragmentManager().findFragmentByTag(tag);
                if (fragment == null) {
                    fragment = new HistoryFragment();
                }
                //show action bar
                getSupportActionBar().show();
                // Display the HistoryFragment title on the ActionBar
                getSupportActionBar().setTitle(R.string.bottom_menu_history);
                break;

            // Display InformationFragment
            case R.id.navigation_information:
                // Get or create a new InformationFragment
                tag = "Information";
                fragment = getSupportFragmentManager().findFragmentByTag(tag);
                if (fragment == null) {
                    fragment = new InformationFragment();
                }
                //show action bar
                getSupportActionBar().show();
                // Display the InformationFragment title on the ActionBar
                getSupportActionBar().setTitle(R.string.bottom_menu_information);
                break;
        }

        // Replace the existing Fragment by the new one
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, fragment, tag)
                .commit();

        return true;
    }
}
