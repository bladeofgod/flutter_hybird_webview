package remote_webview.service.hub;

import android.util.LongSparseArray;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Objects;

import io.flutter.plugin.common.MethodChannel;
import remote_webview.garbage_collect.MainGarbageCollector;
import remote_webview.interfaces.IGarbageCleanListener;
import remote_webview.interfaces.IMockMethodResult;
import remote_webview.mock.MockMethodCall;
import remote_webview.service.decoder.PackageHandler;
import remote_webview.service.decoder.WebViewPackageHandler;
import remote_webview.utils.HandlerUtil;
import remote_webview.utils.LogUtil;

public class MainBinderCommHub extends BinderCommunicateHub<MainCallbackHandler> implements IGarbageCleanListener {
    
    private static volatile MainBinderCommHub singleton;
    
    public static MainBinderCommHub getInstance() {
        if(singleton == null) {
            synchronized (MainBinderCommHub.class) {
                if(singleton == null) {
                    singleton = new MainBinderCommHub();
                }
            }
        }
        return singleton;
    }

    private MainBinderCommHub() {
        decoder = new WebViewPackageHandler();
        MainGarbageCollector.getInstance().registerCollectListener(this);
    }

    private PackageHandler decoder;

    public void setDecoder(PackageHandler decoder) {
        this.decoder = decoder;
    }

    public PackageHandler getDecoder() {
        return decoder;
    }

    /**
     * For somehow the {@linkplain MainBinderCommHub#resultCallbackCache} did not find
     * a result by id, so it will call this to avoid a null pointer exception.
     */
    private final MethodChannel.Result defaultResultCallback = new MethodChannel.Result() {
        @Override
        public void success(@Nullable Object o) {
            //todo maybe can log error.
        }

        @Override
        public void error(String s, @Nullable String s1, @Nullable Object o) {

        }

        @Override
        public void notImplemented() {

        }
    };

    //Cache the flutter-methodCall's resultCallback
    private final LongSparseArray<MethodChannel.Result> resultCallbackCache
            = new LongSparseArray<MethodChannel.Result>();


    public MethodChannel.Result getFlutterResult(long id) {
        return resultCallbackCache.get(id, defaultResultCallback);
    }


    /**
     * Cache a flutter result-callback.
     * When you call a method to remote-view and wait a result back to flutter side, than you need
     * to cache {@link MethodChannel.Result}
     * @param id invoke timestamp.
     */
    public void cacheResultCallback(long id, MethodChannel.Result result) {
        resultCallbackCache.put(id, result);
    }

    /**
     *
     * @param id invoke timestamp.
     */
    protected void removeCacheResultCallback(long id) {
        resultCallbackCache.remove(id);
    }

//    /**
//     * Remove {@link RemoteCallbackHandler} and {@link MethodChannel.Result} by id (invoke timestamp).
//     *
//     * @see remote_webview.model.MethodModel
//     */
//    protected void removeCallback(long id) {
//        //remove flutter's callback
//        removeCacheResultCallback(id);
//        //remove platform-thread's callback
//        removeMethodResultCallbackById(id);
//    }


    /**
     * This is for remote invoke,and back a result to remote by {@link MainCallbackHandler}
     * @param id identifier a method call.
     * @return a handler for send result to remote
     */
    @Override
    protected MainCallbackHandler getCallbackHandler(long id) {
        return new MainCallbackHandler(id);
    }


    /**
     * This is for remote callback,and back a result to flutter by {@link FlutterCallbackHandler}
     * @param id identifier a method call.
     * @return a handler for send result to flutter
     */
    protected FlutterCallbackHandler getFlutterCallbackHandler(long id) {
        return new FlutterCallbackHandler(id);
    }


    public void cacheMethodResultFlutterCallback(long id) {
        cacheMethodResultCallback(id, getFlutterCallbackHandler(id));
    }


    /**
     * In main process, the invoke-method's resultCallback will pass 2 thread
     * that is platform thread and flutter thread, so there must override this method and recall
     * flutter's result callback in it, to ensure the correct association of MethodCall and
     * MethodChannel.Result.
     *
     * @see #resultCallbackCache
     * @see  io.flutter.plugin.common.MethodChannel
     * @see  io.flutter.plugin.common.MethodChannel.Result
     *
     * @param id : invoke-method timeStamp as an id for linked  {@linkplain IMockMethodResult}.
     * @param call : an invoke with method-name, arguments.
     *
     * @see remote_webview.model.MethodModel
     *
     * @throws NullPointerException
     *
     */
    @Override
    protected void invokeMethodById(final long id, final MockMethodCall call) throws NullPointerException {
        HandlerUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(call.needCallback == 1) {
                    Objects.requireNonNull(methodHandlerSlot.get(call.id)).onMethodCall(call,
                            Objects.requireNonNull(methodResultCallbackSlog.get(id)));
                } else {
                    Objects.requireNonNull(methodHandlerSlot.get(call.id)).onMethodCall(call,
                            emptyResultCallback);
                }
            }
        });

    }

    @Override
    public void cleanGarbage(long id) {
        LogUtil.logMsg(this.toString(),"start cleanGarbage id : %s  size %s"
                ,String.valueOf(id),String.valueOf(methodHandlerSlot.size()));
        plugOutMethodHandler(id);
        LogUtil.logMsg(this.toString(),"after cleanGarbage ,methodHandlerSlot size: " + methodHandlerSlot.size());
    }

    @Override
    public void cleanAll() {
        cleanAllMethodHandler();
        cleanAllMethodResultCallback();
    }

    @Override
    protected String getTag() {
        return MainBinderCommHub.class.getSimpleName();
    }
}
