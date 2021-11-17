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
import remote_webview.service.decoder.WebViewDecoder;
import remote_webview.utils.HandlerUtil;

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
    
    private MainBinderCommHub() {
        MainGarbageCollector.getInstance().registerCollectListener(this);
    }

    /**
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

    /**
     * Remove {@link RemoteCallbackHandler} and {@link MethodChannel.Result} by id (invoke timestamp).
     *
     * @see remote_webview.model.MethodModel
     */
    protected void removeCallback(long id) {
        //remove flutter's callback
        removeCacheResultCallback(id);
        //remove platform-thread's callback
        removeMethodResultCallbackById(id);
    }


    @Override
    protected MainCallbackHandler getCallbackHandler(long id) {
        return new MainCallbackHandler(id);
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
                Objects.requireNonNull(methodHandlerSlot.get(call.id)).onMethodCall(call, new IMockMethodResult() {
                    @Override
                    public void success(@Nullable HashMap var1) {
                        //result to flutter
                        Object flutterResult = WebViewDecoder.decodeToFlutterResult(call.method, var1);
                        resultCallbackCache.get(id, defaultResultCallback).success(flutterResult);
                        
                        Objects.requireNonNull(methodResultCallbackSlog.get(id)).success(var1);
                        removeCallback(id);
                    }

                    @Override
                    public void error(String var1, @Nullable String var2, @Nullable HashMap var3) {
                        resultCallbackCache.get(id, defaultResultCallback).error(var1, var2, var3);

                        Objects.requireNonNull(methodResultCallbackSlog.get(id)).error(var1, var2, var3);
                        removeCallback(id);
                    }

                    @Override
                    public void notImplemented() {
                        resultCallbackCache.get(id, defaultResultCallback).notImplemented();
                        removeCallback(id);
                    }
                });
            }
        });

    }

    @Override
    public void cleanGarbage(long id) {
        plugOutMethodHandler(id);
    }

    @Override
    public void cleanAll() {
        cleanAllMethodHandler();
        cleanAllMethodResultCallback();
    }
}
