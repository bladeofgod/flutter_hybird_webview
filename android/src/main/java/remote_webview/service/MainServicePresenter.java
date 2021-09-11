package remote_webview.service;

import android.app.Service;
import android.content.Context;


/**
 * Represents  main service binder.
 *
 * communicate with main-process will by this presenter.
 */

public class MainServicePresenter extends ProcessServicePresenter {

    private static volatile MainServicePresenter singleton;

    public static MainServicePresenter getInstance(Context context) {
        if(singleton == null) {
            synchronized (MainServicePresenter.class) {
                if(singleton == null) {
                    singleton = new MainServicePresenter(context);
                }
            }
        }
        return singleton;
    }

    //todo
    
    private MainServicePresenter(Context context) {
        super(context);
    }

    @Override
    public Class<Service> getServiceClass() {
        return null;
    }
}
