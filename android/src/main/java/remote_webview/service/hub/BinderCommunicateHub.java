package remote_webview.service.hub;

import android.util.SparseArray;
import android.util.SparseLongArray;

import androidx.annotation.Nullable;

import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.HashMap;

import remote_webview.interfaces.IMockMethodHandler;
import remote_webview.interfaces.IMockMethodResult;
import remote_webview.mock.MockMethodCall;
import remote_webview.model.MethodModel;
import remote_webview.utils.StringUtil;

abstract public class BinderCommunicateHub {

    /**
     * Cache webView's method handler.
     *
     * @key surface's id.
     * @value IMockMethodHandler
     *
     */
    private final SparseArray<IMockMethodHandler> methodHandlerSlot = new SparseArray();


    /**
     *
     * @param id    surface's id, to mark texture on main-process and web-view on child-process.
     * @param handler     control web-view.
     */
    public void plugInMethodHandler(int id, IMockMethodHandler handler) {
        methodHandlerSlot.append(id,handler);
    }


    /**
     *
     * @param id surface's id
     */
    public void plugOutMethodHandler(int id) {
        methodHandlerSlot.remove(id);
    }


    public void invokeMethod(MethodModel model) {
        int handlerId = model.getId();
        final MockMethodCall methodCall = new MockMethodCall(model.getMethodName(),model.getArguments());
        invokeMethodById(handlerId,methodCall);
    }

    public void invokeMethodById(int id, MockMethodCall call) {
        methodHandlerSlot.get(id).onMethodCall(call, new IMockMethodResult() {
            @Override
            public void success(@Nullable HashMap var1) {
                //todo
            }

            @Override
            public void error(String var1, @Nullable String var2, @Nullable HashMap var3) {
                //todo
            }

            @Override
            public void notImplemented() {

            }
        });
    }


}










