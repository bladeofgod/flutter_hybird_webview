package com.lijiaqi.remote_webview.service;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.lijiaqi.remote_webview.service.binders.RemoteMethodChannelBinder;

/**
 * working on child process.
 * connect with {@link MethodChannelMainService}
 */

public class MethodChannelRemoteService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new RemoteMethodChannelBinder();
    }
}
