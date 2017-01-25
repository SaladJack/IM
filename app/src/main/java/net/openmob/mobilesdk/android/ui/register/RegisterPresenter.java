package net.openmob.mobilesdk.android.ui.register;

/**
 * Created by saladjack on 17/1/25.
 */

public class RegisterPresenter implements RegisterIPresenter {

    private RegisterView view;
    private RegisterIModel model;

    public RegisterPresenter(RegisterView view) {
        this.view = view;
        model = new RegisterModel(this);
    }

    @Override public void register(String userName,String account,String password) {
        model.register(userName,account,password);
    }

    @Override public void registerSuccess() {
        view.onRegisterSuccess();
    }

    @Override public void registerFailed() {
        view.onRegisterFailure();
    }
}
