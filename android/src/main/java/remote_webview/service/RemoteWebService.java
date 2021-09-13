package remote_webview.service;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

/**
 * working on child process.
 * connect with {@link MainWebService}
 */

public class RemoteWebService extends Service {

    private Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new RemoteServicePresenter.RemoteBinderPoolImpl(mContext);
    }
}
