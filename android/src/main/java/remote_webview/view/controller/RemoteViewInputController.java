package remote_webview.view.controller;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.RemoteException;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import remote_webview.interfaces.ISoftInputCallback;
import remote_webview.service.RemoteServicePresenter;
import remote_webview.service.manager.RemoteViewModuleManager;
import remote_webview.utils.LogUtil;
import remote_webview.view.WebViewSurfaceProducer;

import static android.content.Context.INPUT_METHOD_SERVICE;

abstract public class RemoteViewInputController extends RemoteViewTouchController
    implements ISoftInputCallback {

    private static final String TAG = "RemoteViewInputController";

    private static long NON_CONSUMER = -999;

    private Activity mActivity;

    private boolean isSoftInputShow = false;

    //The remote-view's id that who wanna consume key event.
    private long consumeViewId;

    public RemoteViewInputController(Activity context, FlutterViewAdapter adapter) {
        super(context, adapter);
        mActivity = context;
        setListenerToRootView();
    }

    private MethodChannel.Result topViewIdCallback = new MethodChannel.Result() {
        @Override
        public void success(@Nullable Object o) {
            try {
                consumeViewId = Long.valueOf((String)o);
            }catch (Exception e) {
                resetInputConsumer();
                e.printStackTrace();
            }
        }

        @Override
        public void error(String s, @Nullable String s1, @Nullable Object o) {
            LogUtil.logMsg(TAG,s,"  ",s1);
        }

        @Override
        public void notImplemented() {

        }
    };


    /**
     * Fetch the top view's id from flutter side.
     */
    private void updateTopViewId() {
        try {
            RemoteViewModuleManager.getInstance()
                    .getFlutterPluginProxy()
                    .checkTopViewId(topViewIdCallback);
        }catch (Exception e) {
            resetInputConsumer();
            e.printStackTrace();
        }
    }

    @Override
    public void showSoftInput() {
        updateTopViewId();
        //although toggleSoftInput() maybe run before topViewIdCallback,
        //but keyEvent dispatch is after it.
        toggleSoftInput();
    }

    @Override
    public void hideSoftInput() {
        resetInputConsumer();
        if(isSoftInputShow) {
            toggleSoftInput();
        }
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

    private void setListenerToRootView() {
        final View rootView = mActivity.getWindow().getDecorView().findViewById(android.R.id.content);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                boolean mKeyboardUp = isKeyboardShown(rootView);
                if (mKeyboardUp) {
                    //show
                    isSoftInputShow = true;

                } else {
                    //hide
                    isSoftInputShow = false;
                    resetInputConsumer();
                }
            }
        });
    }


    private boolean isKeyboardShown(View rootView) {
        final int softKeyboardHeight = 100;
        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);
        DisplayMetrics dm = rootView.getResources().getDisplayMetrics();
        int heightDiff = rootView.getBottom() - r.bottom;
        return heightDiff > softKeyboardHeight * dm.density;
    }
}
