package remote_webview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import remote_webview.service.RemoteServicePresenter;
import remote_webview.service.manager.RemoteViewModuleManager;
import remote_webview.utils.LogUtil;
import remote_webview.view.RemoteWebViewController;
import remote_webview.view.WebViewSurfaceProducer;

public class RemoteWebViewPlugin implements FlutterPlugin, MethodChannel.MethodCallHandler {

    private static final String CHANNEL_NAME = "remote_webview_plugin";

    private RemoteWebViewController remoteWebViewController;
    private Context mAppContext;
    private Activity mActivity;
    private MethodChannel mMethodChannel;

    public RemoteWebViewPlugin(Activity mActivity) {
        this.mActivity = mActivity;
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

    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPlugin.FlutterPluginBinding flutterPluginBinding) {
        remoteWebViewController.disposeAll(new MethodCall("onDetachedFromEngine", null)
                , idleResult);
        RemoteViewModuleManager.getInstance().onDetachedFromEngine(flutterPluginBinding);
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
}
