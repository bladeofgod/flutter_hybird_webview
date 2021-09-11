package remote_webview.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import remote_webview.service.binders.MainMethodChannelBinder;

/**
 * working on main process.
 * connect with {@link RemoteWebService}
 */

public class MainWebService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MainMethodChannelBinder();
    }
}
