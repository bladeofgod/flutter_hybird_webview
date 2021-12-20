package remote_webview.input_hook.hook;

import android.content.Context;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import remote_webview.input_hook.compat.IInputMethodManagerCompat;
import remote_webview.input_hook.compat.SystemServiceRegistryCompat;
import remote_webview.input_hook.util.ReflectUtil;
import remote_webview.utils.LogUtil;


public class InputMethodManagerHook extends Hook implements InvocationHandler {

    private static final String TAG = "InputMethodManagerHook";

    private MethodInvokeListener methodInvokeListener;

    public InputMethodManagerHook(Context context) {
        super(context);
    }

    public void setMethodInvokeListener(MethodInvokeListener methodInvokeListener) {
        this.methodInvokeListener = methodInvokeListener;
    }

    public InputMethodManagerProxy immP;

    Object proxyInputMethodInterface;

    public Object getProxyInputMethodInterface() {
        return proxyInputMethodInterface;
    }

    public static class InputMethodManagerProxy implements InvocationHandler{

        final InputMethodManager originObj;

        public InputMethodManagerProxy(InputMethodManager originObj) {
            this.originObj = originObj;
        }

        public InputMethodManager getImm() {
            return (InputMethodManager) Proxy.newProxyInstance(InputMethodManager.class.getClassLoader(),
                    new Class[]{InputMethodManager.class}, this);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            LogUtil.logMsg(InputMethodManagerHook.TAG, "immp invoke ", method.getName());
            return method.invoke(originObj, args);
        }
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
            //todo test code
            try {
                Class clz = InputMethodManager.class.getClass();
                Constructor[] ccss = clz.getDeclaredConstructors();
                //Constructor immC = clz.getDeclaredConstructor(Class.forName("com.android.internal.view.IInputMethodManager"), int.class, Looper.class);
                Constructor immC = ccss[0];
                boolean isAccessible = immC.isAccessible();
//                if(! isAccessible) {
//                    immC.setAccessible(true);
//                }
                InputMethodManager imm = (InputMethodManager) immC.
                        newInstance(mOriginObj, 0, Looper.getMainLooper());
                immP = new InputMethodManagerProxy(imm);

            }catch (Exception e) {
                e.printStackTrace();
            }

            //todo test code
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
