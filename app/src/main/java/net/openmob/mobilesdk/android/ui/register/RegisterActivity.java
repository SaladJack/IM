package net.openmob.mobilesdk.android.ui.register;

import android.app.Activity;
import android.os.Bundle;

import net.openmob.mobilesdk.android.R;
import net.openmob.mobilesdk.android.ui.BaseActivity;

/**
 * Created by saladjack on 17/1/25.
 */

public class RegisterActivity extends BaseActivity implements RegisterView{
    RegisterIPresenter presenter;
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new RegisterPresenter(this);

    }

    @Override public void onRegisterSuccess() {
        showToast(R.string.registerSuccess);
    }

    @Override public void onRegisterFailure() {
        showToast(R.string.registerFailure);
    }
}
