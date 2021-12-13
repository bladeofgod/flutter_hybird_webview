package remote_webview.input_hook.hook;

import android.content.Context;


public abstract class Hook {

    Context mContext;
    Object mOriginObj;

    Hook(Context context) {
        mContext = context;
    }

    public Object getOriginObj() {
        return mOriginObj;
    }

    public abstract void onHook(ClassLoader classLoader) throws Throwable;

}
