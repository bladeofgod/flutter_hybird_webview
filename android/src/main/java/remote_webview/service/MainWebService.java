package remote_webview.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import remote_webview.service.binders.MainMethodChannelBinder;

/**
 * working on main process.
 * connect with {@link RemoteWebService}
 */

public class MainWebService extends Service {

    private Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }
    
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MainServicePresenter.MainBinderPoolImpl(mContext);
    }
}
