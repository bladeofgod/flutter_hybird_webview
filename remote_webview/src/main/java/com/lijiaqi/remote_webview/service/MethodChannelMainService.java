package com.lijiaqi.remote_webview.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

/**
 * working on main process.
 * connect with {@link MethodChannelRemoteService}
 */

public class MethodChannelMainService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
