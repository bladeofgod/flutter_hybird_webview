package remote_webview.view.controller;

import android.os.Build;
import android.os.RemoteException;

import java.util.function.Function;

import remote_webview.service.MainServicePresenter;
import remote_webview.utils.LogUtil;

public class InputToggleDelegate {
    private static final String TAG = "InputToggleDelegate";

    private String targetClassName = "ImeAdapterImpl";

    private String targetMethod = "updateState";

    private final Debounce debounce = new Debounce();

    /**
     * This is a way for toggle soft input.
     *
     * In the start i though hook {@linkplain android.view.inputmethod.InputMethodManager} to
     * listen softInput status(show/hide), but in some High lvl api, system class is @hide
     * or @guard.
     *
     * As stated above, i chosen(temporary) override
     * {@linkplain android.content.Context#getSystemService(String)}, and listen-parse the invoke
     * to toggle soft-input. But in actual use, the performance is not goods.
     *
     * TODO : MAKE IT BETTER
     *
     * @see android.view.inputmethod.InputMethodManager
     * 
     * @see android.hardware.display.VirtualDisplay
     *
     */
    public void inputServiceCall() {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        //todo dev code
        for(int i = 0; i < stackTraceElements.length; i++) {
            LogUtil.logMsg(TAG,
                    "getClassName   " + stackTraceElements[i].getClassName(),
                    "getFileName   " + stackTraceElements[i].getFileName(),
                    "getMethodName   " + stackTraceElements[i].getMethodName(),
                    "getLineNumber   " + stackTraceElements[i].getLineNumber()
            );
        }
        boolean hit = parse(stackTraceElements);
        if(hit) {
            debounce.handle((a)->{
                requestToggleSoftInput();
                return null;
            });

        }
    }

    private void requestToggleSoftInput() {
        try {
            MainServicePresenter.getInstance().getMainProcessBinder().showSoftInput();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private boolean parse(StackTraceElement[] stackTraceElements) {
        for (StackTraceElement stackTraceElement : stackTraceElements) {
            if (stackTraceElement.getClassName().contains(targetClassName)
                    && stackTraceElement.getMethodName().contains(targetMethod)) {
                return true;
            }
        }
        return false;
    }

    private static class Debounce{
        private final int limit = 1000;

        long lastCall = 0;

        public void handle(Function function) {
            long n = System.currentTimeMillis();
            if(n - lastCall > limit) {
                lastCall = n;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    function.apply(null);
                }
            }
        }
    }

}
