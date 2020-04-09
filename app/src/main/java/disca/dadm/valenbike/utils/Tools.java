package disca.dadm.valenbike.utils;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import disca.dadm.valenbike.R;

public class Tools {

    public static void showSnackBar(View view, String msg) {
        Snackbar snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_SHORT);
        snackbar.setAnchorView(R.id.bottomView);
        snackbar.show();
    }
}
