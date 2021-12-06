package remote_webview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import remote_webview.activity.RemoteWebActivity;
import remote_webview.interfaces.IActivityKeyEventCallback;
import remote_webview.interfaces.IWindowTokenExtractor;
import remote_webview.service.RemoteServicePresenter;
import remote_webview.service.manager.RemoteViewModuleManager;
import remote_webview.utils.LogUtil;
import remote_webview.view.RemoteWebViewController;
import remote_webview.view.WebViewSurfaceProducer;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class RemoteWebViewPlugin implements FlutterPlugin, MethodChannel.MethodCallHandler
        , IWindowTokenExtractor, IActivityKeyEventCallback {

    private static final String CHANNEL_NAME = "remote_webview_plugin";

    private RemoteWebViewController remoteWebViewController;
    private Context mAppContext;
    private RemoteWebActivity mActivity;
    private MethodChannel mMethodChannel;

    public RemoteWebViewPlugin(RemoteWebActivity mActivity) {
        this.mActivity = mActivity;
        mActivity.setKeyEventCallback(this);
        RemoteViewModuleManager.getInstance().setTokenExtractor(this);
    }

    //do nothing ,like dolt.
    private final MethodChannel.Result idleResult = new MethodChannel.Result() {
        @Override
        public void success(@Nullable Object result) {

        }

        @Override
        public void error(String errorCode, @Nullable String errorMessage, @Nullable Object errorDetails) {

        }

        @Override
        public void notImplemented() {

        }
    };

    @Override
    public void onAttachedToEngine(@NonNull FlutterPlugin.FlutterPluginBinding flutterPluginBinding) {
        mAppContext = flutterPluginBinding.getApplicationContext();
        remoteWebViewController = new RemoteWebViewController(mActivity);
        WebViewSurfaceProducer.producer.holdFlutterBinding(flutterPluginBinding);
        mMethodChannel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), CHANNEL_NAME);
        mMethodChannel.setMethodCallHandler(this);
        RemoteViewModuleManager.getInstance().linkPluginChannel(mMethodChannel);

        RemoteViewModuleManager.getInstance().onAttachedToEngine(flutterPluginBinding);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                InputMethodManager imm =
//                        (InputMethodManager) mActivity.getSystemService(INPUT_METHOD_SERVICE);
//                View v = mActivity.getWindow().getDecorView();
//                v.requestFocus();
//                LogUtil.logMsg(getClass().getSimpleName(),"====================");
//                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
//                //imm.showSoftInput(v,0);
//            }
//        }, 4000);



    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPlugin.FlutterPluginBinding flutterPluginBinding) {
        remoteWebViewController.disposeAll(new MethodCall("onDetachedFromEngine", null)
                , idleResult);
        RemoteViewModuleManager.getInstance().onDetachedFromEngine(flutterPluginBinding);
        RemoteViewModuleManager.getInstance().setTokenExtractor(null);
    }

    @Override
    public void onMethodCall(@NonNull MethodCall methodCall, @NonNull MethodChannel.Result result) {
        switch (methodCall.method) {
            //todo method
//            case "produceWebView":
//                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        LogUtil.logMsg("startActivity"," class : " + mActivity.getClass() );
//                        mActivity.startActivity(new Intent(mAppContext, mActivity.getClass()));
//                    }
//                },1000);
//                Map<String, Object> params = (Map<String, Object>) methodCall.arguments;
//                result.success(WebViewSurfaceProducer.producer.buildGeneralWebViewSurface(params));
//                break;
            case "create":
                remoteWebViewController.create(methodCall, result);
                break;
            case "touch":
                remoteWebViewController.touch(methodCall, result);
                break;
                
            case "dispose":
                //todo
                remoteWebViewController.dispose(methodCall, result);
                break;
            case "disposeAll":
                //todo
                remoteWebViewController.disposeAll(methodCall, result);
                break;
        }

    }

    @Override
    public IBinder extractorToken() {
        LogUtil.logMsg(getClass().getSimpleName(),"extractorToken window token : "
                + (mActivity.getWindow().getDecorView().getWindowToken() == null) );
        return mActivity.getWindow().getDecorView().getWindowToken();
    }

    @Override
    public void dispatchKeyEvent(KeyEvent event) {
        remoteWebViewController.dispatchKeyEvent(event);
    }

    @Override
    public boolean consumeKeyEvent() {
        return remoteWebViewController.hasInputConsumer();
    }
}
