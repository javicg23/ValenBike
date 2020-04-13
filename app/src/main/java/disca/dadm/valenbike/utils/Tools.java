package disca.dadm.valenbike.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import disca.dadm.valenbike.R;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class Tools {

    public static void showSnackBar(View view, boolean aboveNav, String msg) {
        Snackbar snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_SHORT);
        if (aboveNav) {
            snackbar.setAnchorView(R.id.bottomView);
        }
        snackbar.show();
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = null;
        if (manager != null) {
            info = manager.getActiveNetworkInfo();
        }
        return info != null && info.isConnected();
    }
}
