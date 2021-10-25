package remote_webview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;

import androidx.annotation.NonNull;

import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import remote_webview.service.RemoteServicePresenter;
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

    @Override
    public void onAttachedToEngine(@NonNull FlutterPlugin.FlutterPluginBinding flutterPluginBinding) {
        mAppContext = flutterPluginBinding.getApplicationContext();
        remoteWebViewController = new RemoteWebViewController(mActivity);
        WebViewSurfaceProducer.producer.holdFlutterBinding(flutterPluginBinding);
        mMethodChannel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), CHANNEL_NAME);
        mMethodChannel.setMethodCallHandler(this);

        RemoteServicePresenter.getInstance().holdContext(flutterPluginBinding.getApplicationContext());
        RemoteServicePresenter.getInstance().initConnectService();



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
                //todo test code
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        LogUtil.logMsg("startActivity"," class : " + mActivity.getClass() );
                        mActivity.startActivity(new Intent(mAppContext, mActivity.getClass()));
                    }
                },1000);

                remoteWebViewController.create(methodCall, result);
                break;
            case "touch":
                //todo dispatch point event to remote
                //RemoteServicePresenter.getInstance().getRemoteViewFactoryBinder().dispatchKeyEvent();
                break;
                
            case "dispose":
                //todo
                break;
        }

    }
}
