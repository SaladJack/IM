package net.openmob.mobilesdk.android.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

/**
 * Created by saladjack on 17/1/25.
 */
public class BaseActivity extends Activity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void showSnackBar(View view, String toast) {
        Snackbar.make(view, toast, Snackbar.LENGTH_SHORT).show();
    }

    public void showSnackBar(View view, @StringRes int toast) {
        Snackbar.make(view, getString(toast), Snackbar.LENGTH_SHORT).show();
    }

    public void showToast(int toastRes) {
        Toast.makeText(this, getString(toastRes), Toast.LENGTH_SHORT).show();
    }
}
