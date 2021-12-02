package remote_webview.input_hook.hook;

import android.content.Context;


abstract class Hook {

    Context mContext;
    Object mOriginObj;

    Hook(Context context) {
        mContext = context;
    }

    Object getOriginObj() {
        return mOriginObj;
    }

    public abstract void onHook(ClassLoader classLoader) throws Throwable;

}
