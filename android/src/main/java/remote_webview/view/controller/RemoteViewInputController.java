package remote_webview.view.controller;

import android.content.Context;
import android.os.RemoteException;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import remote_webview.interfaces.ISoftInputCallback;
import remote_webview.service.RemoteServicePresenter;
import remote_webview.utils.LogUtil;
import remote_webview.view.WebViewSurfaceProducer;

import static android.content.Context.INPUT_METHOD_SERVICE;

abstract public class RemoteViewInputController extends RemoteViewTouchController
    implements ISoftInputCallback {
    private static long NON_CONSUMER = -999;

    public RemoteViewInputController(Context context, FlutterViewAdapter adapter) {
        super(context, adapter);
    }

    //The remote-view's id that who wanna consume key event.
    private long consumeViewId;

    @Override
    public void showSoftInput(long viewId) {
        consumeViewId = viewId;
        //todo show shot keyboard
        toggleSoftInput();
    }

    @Override
    public void hideSoftInput(long viewId) {
        resetInputConsumer();
        //todo
        toggleSoftInput();
    }

    private void toggleSoftInput() {
        InputMethodManager imm =
                (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
        LogUtil.logMsg(getClass().getSimpleName(),"====================");
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void resetInputConsumer() {
        consumeViewId = NON_CONSUMER;
    }

    public boolean hasInputConsumer() {
        return consumeViewId != NON_CONSUMER;
    }


    @CallSuper
    @Override
    public void dispose(@NonNull MethodCall methodCall, @NonNull MethodChannel.Result result) {
        resetInputConsumer();
        super.dispose(methodCall, result);
    }

    @CallSuper
    @Override
    public void disposeAll(@NonNull MethodCall methodCall, @NonNull MethodChannel.Result result) {
        resetInputConsumer();
        super.disposeAll(methodCall, result);
    }


    @Override
    public void dispatchKeyEvent(@NonNull KeyEvent keyEvent) {
        if(WebViewSurfaceProducer.producer.checkViewExists(consumeViewId)) {
            try {
                RemoteServicePresenter.getInstance().getRemoteViewFactoryBinder()
                        .dispatchKeyEvent("" + consumeViewId, keyEvent);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            LogUtil.logMsg("RemoteViewInputController","dispatch a key-event to no where !");
        }

    }
}
