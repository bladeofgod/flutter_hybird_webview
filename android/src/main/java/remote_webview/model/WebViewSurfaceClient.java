package remote_webview.model;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.os.RemoteException;
import android.util.DisplayMetrics;
import android.view.Surface;
import android.webkit.WebStorage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.view.TextureRegistry;
import remote_webview.interfaces.IMockMethodHandler;
import remote_webview.interfaces.IMockMethodResult;
import remote_webview.mock.MockMethodCall;
import remote_webview.service.RemoteServicePresenter;
import remote_webview.service.hub.MainBinderCommHub;
import remote_webview.utils.LogUtil;

/**
 * The client of remote web view, has a private channel to flutter.
 * accept flutter's control order and send to remote.
 */

public class WebViewSurfaceClient extends ViewSurfaceModel
        implements MethodChannel.MethodCallHandler, IMockMethodHandler {

    private static final String CHANNEL_NAME_HEAD = "hybird.flutter/webview_";

    private final MethodChannel methodChannel;

    protected WebViewSurfaceClient(TextureRegistry.SurfaceTextureEntry entry,
                                   Surface surface,
                                   BinaryMessenger binaryMessenger) {
        super(entry, surface);
        methodChannel = new MethodChannel(binaryMessenger,CHANNEL_NAME_HEAD+entry.id());
        methodChannel.setMethodCallHandler(this);
        MainBinderCommHub.getInstance().plugInMethodHandler(entry.id(), this);
    }

    @Override
    public void release() {
        methodChannel.setMethodCallHandler(null);
        MainBinderCommHub.getInstance().plugOutMethodHandler(getId());
        super.release();
    }

    /**
     * send remote order its separate in each function,because some args need repackage to HashMap.
     * Also need dePackage the results.
     * @param methodCall from flutter's call
     * @param result back to flutter
     */
    @Override
    public void onMethodCall(@NonNull MethodCall methodCall, MethodChannel.Result result) {
        switch (methodCall.method) {
            case "loadUrl":
                loadUrl(methodCall, result);
                break;
            case "updateSettings":
                updateSettings(methodCall, result);
                break;
            case "canGoBack":
                canGoBack(methodCall, result);
                break;
            case "canGoForward":
                canGoForward(methodCall, result);
                break;
            case "goBack":
                goBack(methodCall, result);
                break;
            case "goForward":
                goForward(methodCall, result);
                break;
            case "reload":
                reload(methodCall, result);
                break;
            case "currentUrl":
                currentUrl(methodCall, result);
                break;
            case "evaluateJavascript":
                evaluateJavaScript(methodCall, result);
                break;
            case "addJavascriptChannels":
                addJavaScriptChannels(methodCall, result);
                break;
            case "removeJavascriptChannels":
                removeJavaScriptChannels(methodCall, result);
                break;
            case "clearCache":
                clearCache(methodCall, result);
                break;
            case "getTitle":
                getTitle(methodCall, result);
                break;
            case "scrollTo":
                scrollTo(methodCall, result);
                break;
            case "scrollBy":
                scrollBy(methodCall, result);
                break;
            case "getScrollX":
                getScrollX(methodCall, result);
                break;
            case "getScrollY":
                getScrollY(methodCall, result);
                break;
            default:
                result.notImplemented();
        }

    }

    /**
     * Build a common remote-method model.
     * @param methodCall origin method from flutter
     * @param args adjust arguments from flutter
     * @param needCallback   1 : true , 0 : false
     * @return
     */
    private MethodModel buildMethodModel(MethodCall methodCall, HashMap args, int needCallback) {
        long invokeTimeStamp = System.currentTimeMillis();
        return new MethodModel(getId(), methodCall.method, args == null ? new HashMap() : args, invokeTimeStamp, (byte) needCallback);
    }

    /**
     * Flutter's channel method will trigger this method to send to remote-view.
     *
     * If {@linkplain MethodModel} needCallback, can use {@linkplain MainBinderCommHub} to cache
     * a {@linkplain remote_webview.service.hub.MainCallbackHandler}
     *
     * @param model method package
     */
    private void sendRemoteMethodCall(MethodModel model) {
        if(model.getNeedCallback() == 1) {
            MainBinderCommHub.getInstance().cacheMethodResultFlutterCallback(model.getInvokeTimeStamp());
        }
        LogUtil.logMsg(this.toString(), "sendRemoteMethodCall : " + model.toString());
        try {
            RemoteServicePresenter.getInstance().getRemoteChannelBinder().invokeMethod(model);
        } catch (RemoteException e) {
            LogUtil.logMsg("remote call error", model.getMethodName() + "-" + model.getArguments());
            e.printStackTrace();
        }
    }


    @SuppressWarnings("unchecked")
    private void loadUrl(MethodCall methodCall, MethodChannel.Result result) {
        MethodModel model = buildMethodModel(methodCall, (HashMap) methodCall.arguments, 0);
        sendRemoteMethodCall(model);
        result.success(null);
    }

    private void canGoBack(MethodCall methodCall, MethodChannel.Result result) {
        MethodModel model = buildMethodModel(methodCall, null, 1);
        //cache the result.
        MainBinderCommHub.getInstance().cacheResultCallback(model.getInvokeTimeStamp(), result);
        sendRemoteMethodCall(model);
    }

    private void canGoForward(MethodCall methodCall, MethodChannel.Result result) {
        MethodModel model = buildMethodModel(methodCall, null, 1);
        //cache the result.
        MainBinderCommHub.getInstance().cacheResultCallback(model.getInvokeTimeStamp(), result);
        sendRemoteMethodCall(model);
    }

    private void goBack(MethodCall methodCall, MethodChannel.Result result) {
        MethodModel model = buildMethodModel(methodCall, null, 0);
        sendRemoteMethodCall(model);
        result.success(null);
    }

    private void goForward(MethodCall methodCall, MethodChannel.Result result) {
        MethodModel model = buildMethodModel(methodCall, null, 0);
        sendRemoteMethodCall(model);
        result.success(null);
    }

    private void reload(MethodCall methodCall, MethodChannel.Result result) {
        MethodModel model = buildMethodModel(methodCall, null, 0);
        sendRemoteMethodCall(model);
        result.success(null);
    }

    private void currentUrl(MethodCall methodCall, MethodChannel.Result result) {
        MethodModel model = buildMethodModel(methodCall, null, 1);
        //cache the result.
        MainBinderCommHub.getInstance().cacheResultCallback(model.getInvokeTimeStamp(), result);
        sendRemoteMethodCall(model);
    }

    @SuppressWarnings("unchecked")
    private void updateSettings(MethodCall methodCall, MethodChannel.Result result) {
        MethodModel model = buildMethodModel(methodCall, (HashMap) methodCall.arguments, 0);
        sendRemoteMethodCall(model);
        result.success(null);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void evaluateJavaScript(MethodCall methodCall, final MethodChannel.Result result) {
        String jsString = (String) methodCall.arguments;
        if (jsString == null) {
            throw new UnsupportedOperationException("JavaScript string cannot be null");
        }
        HashMap<String, String> ripeArgs= new HashMap<>();
        ripeArgs.put("jsString", jsString);
        MethodModel model = buildMethodModel(methodCall, ripeArgs, 1);
        //cache the result.
        MainBinderCommHub.getInstance().cacheResultCallback(model.getInvokeTimeStamp(), result);
        sendRemoteMethodCall(model);
    }

    @SuppressWarnings("unchecked")
    private void addJavaScriptChannels(MethodCall methodCall, MethodChannel.Result result) {
        List<String> channelNames = (List<String>) methodCall.arguments;
        HashMap<Integer, String> ripeArgs = new HashMap();
        int i = 0;
        for(String name : channelNames) {
            ripeArgs.put(i, name);
            i++;
        }
        MethodModel model = buildMethodModel(methodCall, ripeArgs, 0);
        sendRemoteMethodCall(model);
        result.success(null);
    }

    @SuppressWarnings("unchecked")
    private void removeJavaScriptChannels(MethodCall methodCall, MethodChannel.Result result) {
        List<String> channelNames = (List<String>) methodCall.arguments;
        HashMap<Integer, String> ripeArgs = new HashMap();
        int i = 0;
        for(String name : channelNames) {
            ripeArgs.put(i, name);
            i++;
        }
        MethodModel model = buildMethodModel(methodCall, ripeArgs, 0);
        sendRemoteMethodCall(model);
        result.success(null);
    }

    private void clearCache(MethodCall methodCall, MethodChannel.Result result) {
        MethodModel model = buildMethodModel(methodCall, null, 0);
        sendRemoteMethodCall(model);
        WebStorage.getInstance().deleteAllData();
        result.success(null);
    }

    private void getTitle(MethodCall methodCall, MethodChannel.Result result) {
        MethodModel model = buildMethodModel(methodCall, null, 1);
        MainBinderCommHub.getInstance().cacheResultCallback(model.getInvokeTimeStamp(), result);
        sendRemoteMethodCall(model);
    }

    private void scrollTo(MethodCall methodCall, MethodChannel.Result result) {
        MethodModel model = buildMethodModel(methodCall, (HashMap) methodCall.arguments(), 0);
        sendRemoteMethodCall(model);

        result.success(null);
    }

    private void scrollBy(MethodCall methodCall, MethodChannel.Result result) {
        MethodModel model = buildMethodModel(methodCall, (HashMap) methodCall.arguments(), 0);
        sendRemoteMethodCall(model);

        result.success(null);
    }

    private void getScrollX(MethodCall methodCall, MethodChannel.Result result) {
        MethodModel model = buildMethodModel(methodCall, null, 1);
        MainBinderCommHub.getInstance().cacheResultCallback(model.getInvokeTimeStamp(), result);
        sendRemoteMethodCall(model);
    }

    private void getScrollY(MethodCall methodCall, MethodChannel.Result result) {
        MethodModel model = buildMethodModel(methodCall, null, 0);
        MainBinderCommHub.getInstance().cacheResultCallback(model.getInvokeTimeStamp(), result);
        sendRemoteMethodCall(model);
    }

    /**
     * This is mock method, and usually use in remote communicate.
     * In {@link WebViewSurfaceClient}, this called from remote-process 
     * by {@link remote_webview.service.binders.MainMethodChannelBinder}.
     * @see MainBinderCommHub
     * @see IMockMethodHandler
     * 
     * @param methodCall method model with name and argument
     * @param result result call back, must ensure call it after method-call.
     */
    @Override
    public void onMethodCall(@NonNull MockMethodCall methodCall, @NonNull final IMockMethodResult result) {
        if(methodCall.needCallback == 0) {
            methodChannel.invokeMethod(methodCall.method, methodCall.arguments);
        } else {
            //need result back to remote-view
            methodChannel.invokeMethod(methodCall.method, methodCall.arguments, new MethodChannel.Result() {
                @Override
                public void success(@Nullable Object r) {
                    result.success(new HashMap());
                }

                @Override
                public void error(String errorCode, @Nullable String errorMessage, @Nullable Object errorDetails) {
                    result.error(errorCode, errorMessage, new HashMap());
                }

                @Override
                public void notImplemented() {
                    result.notImplemented();
                }
            });
        }

    }

    public static class Builder{
        public static final String TAG = "Builder";

        private final Context mPppContext;

        private final FlutterPlugin.FlutterPluginBinding flutterPluginBinding;

        public Builder(FlutterPlugin.FlutterPluginBinding flutterPluginBinding) {
            this.mPppContext = flutterPluginBinding.getApplicationContext();
            this.flutterPluginBinding = flutterPluginBinding;
        }

        private TextureRegistry.SurfaceTextureEntry entry;

        private SurfaceTexture mSurfaceTexture;

        private Surface mSurface;

        public Builder create(TextureRegistry registry) {
            entry = registry.createSurfaceTexture();
            mSurfaceTexture = entry.surfaceTexture();
            return this;
        }

        /**
         * The surfaceTexture's DefaultBufferSize
         *
         * Use screen's width and height in default.
         */
        private int width;
        private int height;

        /**
         * Set surfaceTexture's DefaultBufferSize
         * @param width
         * @param height
         * @return
         */
        public Builder setSurfaceDefaultBufferSize(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public WebViewSurfaceClient build() {
            LogUtil.logMsg(TAG,"view size : " + width + "  " + height);
            mSurfaceTexture.setDefaultBufferSize(width, height);
            mSurface = new Surface(mSurfaceTexture);
            return new WebViewSurfaceClient(entry, mSurface, flutterPluginBinding.getBinaryMessenger());
        }

        //will cause event miss location
        @Deprecated
        private void userScreenSize() {
            DisplayMetrics dm = mPppContext.getResources().getDisplayMetrics();
            width = dm.widthPixels;
            height = dm.heightPixels;
        }
        
    }
}
