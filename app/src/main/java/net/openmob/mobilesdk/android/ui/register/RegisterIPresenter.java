package net.openmob.mobilesdk.android.ui.register;

/**
 * Created by saladjack on 17/1/25.
 */
public interface RegisterIPresenter {
    void register(String userName, String account, String password);
    void registerSuccess();
    void registerFailed();
}
