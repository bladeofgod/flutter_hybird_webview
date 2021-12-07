package remote_webview.view;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.RemoteException;
import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.NonNull;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.flutter.embedding.android.MotionEventTracker;
import io.flutter.embedding.engine.systemchannels.PlatformViewsChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.platform.PlatformViewsController;
import remote_webview.garbage_collect.MainGarbageCollector;
import remote_webview.service.RemoteServicePresenter;
import remote_webview.utils.LogUtil;
import remote_webview.view.controller.RemoteViewInputController;
import remote_webview.view.controller.RemoteViewTouchController;

/**
 * Control the remote view.
 * Design reference {@linkplain PlatformViewsController} and {@link PlatformViewsChannel}
 */

public class RemoteWebViewController extends RemoteViewInputController {
    

    public RemoteWebViewController(Activity context) {
        super(context, new FlutterViewAdapter());
    }

    @Override
    public void create(@NonNull MethodCall methodCall, @NonNull MethodChannel.Result result) {
        final Map<String, Object> params = (Map<String, Object>) methodCall.arguments;
        long surfaceId = WebViewSurfaceProducer.producer.buildGeneralWebViewSurface(params);
        result.success(surfaceId);
    }

    @Override
    public void dispose(@NonNull MethodCall methodCall, @NonNull MethodChannel.Result result) {
        super.dispose(methodCall, result);
        long viewId = getViewId(methodCall);
        LogUtil.logMsg(this.toString(),"Call dispose");
        //1.clean main
        if(viewId == -1) {
            LogUtil.logMsg("Dispose","view not found!");
        } else {
            MainGarbageCollector.getInstance().notifyClean(viewId);
        }
        //2.clean remote
        try {
            RemoteServicePresenter.getInstance()
                    .getRemoteViewFactoryBinder()
                    .dispose(viewId);
        } catch (RemoteException e) {
            result.error("-2001","dispose view by id failed.",e.getMessage());
            e.printStackTrace();
        }

    }

    @Override
    public void disposeAll(@NonNull MethodCall methodCall, @NonNull MethodChannel.Result result) {
        super.disposeAll(methodCall, result);
        MainGarbageCollector.getInstance().notifyCleanAll();
        try {
            RemoteServicePresenter.getInstance()
                    .getRemoteViewFactoryBinder()
                    .disposeAll();
        } catch (RemoteException e) {
            result.error("-2002","dispose all view failed.",e.getMessage());
            e.printStackTrace();
        }
    }
}














