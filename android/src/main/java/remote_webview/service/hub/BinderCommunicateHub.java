package remote_webview.service.hub;

import android.util.LongSparseArray;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Objects;

import remote_webview.interfaces.IMockMethodHandler;
import remote_webview.interfaces.IMockMethodResult;
import remote_webview.mock.MockMethodCall;
import remote_webview.model.MethodModel;
import remote_webview.utils.HandlerUtil;
import remote_webview.utils.LogUtil;

abstract public class BinderCommunicateHub<C extends BaseCallbackHandler> {

    /**
     * register webView's method handler.
     * will remove when web-view disposed.
     *
     * @key surface's id.
     * @value IMockMethodHandler
     *
     */
    protected final LongSparseArray<IMockMethodHandler> methodHandlerSlot = new LongSparseArray<>();

    /**
     * cache a {@link IMockMethodResult} when called {@link #invokeMethodById} temporary.
     * remove after {@link IMockMethodResult} called.
     */
    protected final LongSparseArray<C> methodResultCallbackSlog = new LongSparseArray<>();

    /**
     * In some case, {@linkplain #invokeMethod(MethodModel)} don't need result callback,
     * and use this callback for view do empty-call.
     * 
     * @see #invokeMethodById(long, MockMethodCall) 
     */
    protected final IMockMethodResult emptyResultCallback = new IMockMethodResult() {
        @Override
        public void success(@Nullable HashMap var1) {
            
        }

        @Override
        public void error(String var1, @Nullable String var2, @Nullable HashMap var3) {

        }

        @Override
        public void notImplemented() {

        }
    };


    /**
     *
     * @param id    surface's id, to mark texture on main-process and web-view on child-process.
     * @param handler     control web-view.
     */
    public void plugInMethodHandler(long id, IMockMethodHandler handler) {
        synchronized (methodHandlerSlot) {
            methodHandlerSlot.put(id,handler);
        }
    }


    /**
     *
     * @param id surface's id
     */
    public void plugOutMethodHandler(long id) {
        synchronized (methodHandlerSlot) {
            methodHandlerSlot.remove(id);
        }
    }
    
    protected void cleanAllMethodHandler() {
        methodHandlerSlot.clear();
    }


    public void cacheMethodResultCallback(long id) {
        cacheMethodResultCallback(id, getCallbackHandler(id));
    }

    /**
     * It's a result-callback , pair of invoke-method.
     * It can only use once.
     * @param id #{@linkplain #invokeMethod} remote_invoke's timeStamp, to associate
     *           a  result callback .
     * @param result
     */
    protected void cacheMethodResultCallback(long id, C result) {
        LogUtil.logMsg(this.toString(),"callback handler cache size : " + methodResultCallbackSlog.size());
        synchronized (methodResultCallbackSlog) {
            methodResultCallbackSlog.put(id, result);
        }
    }

    /**
     * After result callback called, use this method to remove it.
     * @param id #{@linkplain #invokeMethod} remote_invoke's timeStamp, to associate
     *           a  result callback .
     */
    public void removeMethodResultCallbackById(long id) {
        synchronized (methodResultCallbackSlog) {
            methodResultCallbackSlog.remove(id);
        }
    }
    
    protected void cleanAllMethodResultCallback() {
        synchronized (methodResultCallbackSlog) {
            methodResultCallbackSlog.clear();
        }
    }
    
    protected abstract C getCallbackHandler(long id);
    
    public C fetchCallbackHandler(long id) {
        synchronized (methodResultCallbackSlog) {
            return methodResultCallbackSlog.get(id);
        }
    }

    /**
     * Clean invalid {@linkplain #methodResultCallbackSlog}.
     * Normally only few handler cached, so didn't need a new thread to do this.
     *
     * @see BaseCallbackHandler
     */
    protected synchronized void cleanInvalidMethodCallback() {
        try {
            for(int i = 0; i < methodResultCallbackSlog.size(); i++) {
                if(methodResultCallbackSlog.valueAt(i).isInvalid()) {
                    methodResultCallbackSlog.removeAt(i);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    

//    /**
//     * 
//     * @param id #{@linkplain #invokeMethod} remote_invoke's timeStamp, to associate 
//     *           a  result callback .
//     */
//    private void cacheResultCallback(long id) {
//        cacheMethodResultCallback(id,);
//    }


    /**
     * IBinder invoke.
     * Use invoke-timestamp for id to mark a resultCallback,
     * and call {@linkplain IMockMethodResult} after {@linkplain #invokeMethodById},
     * invoke-timestamp its always created at main-process.
     * @param model method model.
     */
    public void invokeMethod(MethodModel model) {
        final long handlerId = model.getInvokeTimeStamp();
        final MockMethodCall methodCall = new MockMethodCall(model.getId(), 
                model.getMethodName(),model.getArguments(), model.getNeedCallback());
        LogUtil.logMsg(getTag(), model.toString());
        if(model.getNeedCallback() == 1) {
            cacheMethodResultCallback(handlerId, getCallbackHandler(handlerId));
        }
        try {
            invokeMethodById(handlerId,methodCall);
        }catch (Exception e) {
            if(model.getNeedCallback() == 1) {
                Objects.requireNonNull(methodResultCallbackSlog.get(handlerId)).error("",
                        "Invoke Method Exception" + this.getClass().getSimpleName(), new HashMap() );
                removeMethodResultCallbackById(handlerId);
            }
            e.printStackTrace();
        }
        cleanInvalidMethodCallback();
    }


    /**
     * Invoke web-view's method.
     * @param id : invoke-method timeStamp as an id for linked  {@linkplain IMockMethodResult}.
     *           @see MethodModel#getInvokeTimeStamp()
     * @param call an invoke with method-name, arguments.
     */
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
                //todo need delete after test
//                Objects.requireNonNull(methodHandlerSlot.get(call.id)).onMethodCall(call, new IMockMethodResult() {
//                    @Override
//                    public void success(@Nullable HashMap var1) {
//
//                        if(call.needCallback == 1) {
//                            //add method name for main process to decode to a flutter's result.
//                            var1.put("methodName", call.method);
//                            Objects.requireNonNull(methodResultCallbackSlog.get(id)).success(var1);
//                            removeMethodResultCallbackById(id);
//                        }
//                    }
//
//                    @Override
//                    public void error(String var1, @Nullable String var2, @Nullable HashMap var3) {
//                        if(call.needCallback == 1) {
//                            Objects.requireNonNull(methodResultCallbackSlog.get(id)).error(var1, var2, var3);
//                            removeMethodResultCallbackById(id);
//                        }
//                    }
//
//                    @Override
//                    public void notImplemented() {
//                        if(call.needCallback == 1) {
//                            removeMethodResultCallbackById(id);
//                        }
//                    }
//                });
            }
        });

    }


    protected abstract String getTag();


}










