package remote_webview;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import remote_webview.service.RemoteServicePresenter;
import remote_webview.utils.LogUtil;
import remote_webview.view.WebViewSurfaceProducer;

public class RemoteWebViewPlugin implements FlutterPlugin, MethodChannel.MethodCallHandler {

    private static final String CHANNEL_NAME = "remote_webview_plugin";

    private Context mAppContext;
    private MethodChannel mMethodChannel;
    
    @Override
    public void onAttachedToEngine(@NonNull FlutterPlugin.FlutterPluginBinding flutterPluginBinding) {
        mAppContext = flutterPluginBinding.getApplicationContext();
        WebViewSurfaceProducer.producer.holdFlutterBinding(flutterPluginBinding);
        mMethodChannel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), CHANNEL_NAME);
        mMethodChannel.setMethodCallHandler(this);

        RemoteServicePresenter.getInstance().holdContext(flutterPluginBinding.getApplicationContext());
        //todo test code
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    int isAlive = RemoteServicePresenter.getInstance().getRemoteProcessBinder().isZygoteActivityAlive();
                    LogUtil.logMsg("RemoteServicePresenter"," is alive "  + isAlive);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        },4000);
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPlugin.FlutterPluginBinding flutterPluginBinding) {

    }

    @Override
    public void onMethodCall(@NonNull MethodCall methodCall, @NonNull MethodChannel.Result result) {
        switch (methodCall.method) {
            case "produceWebView":

                break;
        }

    }
}
