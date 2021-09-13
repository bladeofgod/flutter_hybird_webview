package remote_webview.service.binders;


import android.os.Build;
import android.os.RemoteException;
import android.view.MotionEvent;
import android.view.Surface;

import androidx.annotation.RequiresApi;

import java.util.Map;

import remote_webview.IRemoteViewFactoryBinder;
import remote_webview.utils.StringUtil;
import remote_webview.view.RemoteViewFactoryProcessor;

public class RemoteViewFactoryBinder extends IRemoteViewFactoryBinder.Stub {

    /**
     *
     * Initiate web-view and loadUrl, register callback.
     *
     * @param orders : must contain a id(surface), and initiate params.
     *               e.g autoMediaPlaybackPolicy, userAgent, initialUrl
     * @param surface : the web-view depend on it.
     *
     * @throws RemoteException
     */

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void createWithSurface(String orders, Surface surface) throws RemoteException {
        RemoteViewFactoryProcessor.getInstance().createWithSurface(orders,surface);
    }

    /**
     *
     * @param surfaceId : linked a web-view
     * @param event     : dispatch touch event to the web-view that surfaceId linked.
     * @throws RemoteException
     */
    @Override
    public void dispatchTouchEvent(String surfaceId, MotionEvent event) throws RemoteException {
        RemoteViewFactoryProcessor.getInstance().dispatchTouchEvent(surfaceId, event);
    }
}
