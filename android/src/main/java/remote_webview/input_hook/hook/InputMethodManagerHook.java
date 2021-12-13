package remote_webview.input_hook.hook;

import android.content.Context;
import android.os.IBinder;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import remote_webview.input_hook.compat.IInputMethodManagerCompat;
import remote_webview.input_hook.compat.SystemServiceRegistryCompat;
import remote_webview.input_hook.util.ReflectUtil;


public class InputMethodManagerHook extends Hook implements InvocationHandler {

    private static final String TAG = "InputMethodManagerHook";

    private MethodInvokeListener methodInvokeListener;

    public InputMethodManagerHook(Context context) {
        super(context);
    }

    public void setMethodInvokeListener(MethodInvokeListener methodInvokeListener) {
        this.methodInvokeListener = methodInvokeListener;
    }

    Object proxyInputMethodInterface;

    public Object getProxyInputMethodInterface() {
        return proxyInputMethodInterface;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object invoke = null;
        try {
            invoke = method.invoke(mOriginObj,args);
        } catch (Throwable e) {
            Log.w(TAG, "invoke failed!  " + Log.getStackTraceString(e) );
        }
        if (methodInvokeListener != null) {
            methodInvokeListener.onMethod(mOriginObj, method, invoke);
        }
        return invoke;
    }

    @Override
    public void onHook(ClassLoader classLoader) throws Throwable {
        ServiceManagerHook serviceManagerHook = new ServiceManagerHook(mContext, Context.INPUT_METHOD_SERVICE);
        serviceManagerHook.onHook(classLoader);
        Object originBinder = serviceManagerHook.getOriginObj();
        if (originBinder instanceof IBinder) {
            mOriginObj = IInputMethodManagerCompat.asInterface((IBinder) originBinder);
            proxyInputMethodInterface = ReflectUtil.makeProxy(classLoader, mOriginObj.getClass(), this);
            serviceManagerHook.setProxyIInterface(proxyInputMethodInterface);
            clearCachedService();
            //rebuild cache
            mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        }
    }

    private void clearCachedService() throws Throwable {
        Object sInstance = ReflectUtil.getStaticFiled(InputMethodManager.class, "sInstance");
        if (sInstance != null) {
            ReflectUtil.setStaticFiled(InputMethodManager.class, "sInstance", null);
            Object systemFetcher = SystemServiceRegistryCompat.getSystemFetcher(Context.INPUT_METHOD_SERVICE);
            if (systemFetcher != null) {
                ReflectUtil.setFiled(systemFetcher.getClass().getSuperclass(), "mCachedInstance", systemFetcher, null);
            }
        }
    }

    public interface MethodInvokeListener {
        void onMethod(Object obj, Method method, Object result);
    }

}
