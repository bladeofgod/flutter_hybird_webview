package remote_webview.service.hub;

import android.util.SparseArray;
import android.util.SparseLongArray;

import androidx.annotation.Nullable;

import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.HashMap;
import java.util.Objects;

import remote_webview.interfaces.IMockMethodHandler;
import remote_webview.interfaces.IMockMethodResult;
import remote_webview.mock.MockMethodCall;
import remote_webview.model.MethodModel;
import remote_webview.utils.StringUtil;

abstract public class BinderCommunicateHub {

    /**
     * register webView's method handler.
     * will remove when web-view disposed.
     *
     * @key surface's id.
     * @value IMockMethodHandler
     *
     */
    protected final HashMap<Long,IMockMethodHandler> methodHandlerSlot = new HashMap<>();

    /**
     * cache a {@link IMockMethodResult} when called {@link #invokeMethodById} temporary.
     * remove after {@link IMockMethodResult} called.
     */
    protected final HashMap<Long,ResultCallbackHandler> methodResultCallbackSlog = new HashMap<>();


    /**
     *
     * @param id    surface's id, to mark texture on main-process and web-view on child-process.
     * @param handler     control web-view.
     */
    public void plugInMethodHandler(long id, IMockMethodHandler handler) {
        methodHandlerSlot.put(id,handler);
    }


    /**
     *
     * @param id surface's id
     */
    public void plugOutMethodHandler(long id) {
        methodHandlerSlot.remove(id);
    }

    private void cacheMethodResultCallback(long id,ResultCallbackHandler result) {
        methodResultCallbackSlog.put(id, result);
    }
    
    private void removeMethodResultCallbackById(long id) {
        methodResultCallbackSlog.remove(id);
    }

    /**
     * 
     * @param id #{@linkplain #invokeMethod} remote_invoke's timeStamp, to associate 
     *           a  result callback .
     */
    private void cacheResultCallback(long id) {
        cacheMethodResultCallback(id, new ResultCallbackHandler(id));
    }


    /**
     * IBinder invoke.
     * @param model
     */
    public void invokeMethod(MethodModel model) {
        final long handlerId = model.getInvokeTimeStamp();
        final MockMethodCall methodCall = new MockMethodCall(model.getMethodName(),model.getArguments());
        cacheResultCallback(handlerId);
        try {
            invokeMethodById(handlerId,methodCall);
        }catch (Exception e) {
            Objects.requireNonNull(methodResultCallbackSlog.get(handlerId)).error("",
                    "Invoke Method Exception" + this.getClass().getSimpleName(), new HashMap() );
            removeMethodResultCallbackById(handlerId);
            e.printStackTrace();
        }
    }


    /**
     * invoke web-view
     * @param id : invoke timeStamp as an id for linked  {@linkplain IMockMethodResult}
     * @param call
     */
    private void invokeMethodById(final long id, MockMethodCall call) throws NullPointerException {
        Objects.requireNonNull(methodHandlerSlot.get(id)).onMethodCall(call, new IMockMethodResult() {
            @Override
            public void success(@Nullable HashMap var1) {
                Objects.requireNonNull(methodResultCallbackSlog.get(id)).success(var1);
                removeMethodResultCallbackById(id);
            }

            @Override
            public void error(String var1, @Nullable String var2, @Nullable HashMap var3) {
                Objects.requireNonNull(methodResultCallbackSlog.get(id)).error(var1, var2, var3);
                removeMethodResultCallbackById(id);
            }

            @Override
            public void notImplemented() {

            }
        });
    }


}










