package remote_webview.view.controller;

import android.os.Build;
import android.os.RemoteException;
import android.view.inputmethod.InputMethodManager;

import java.util.List;
import java.util.Stack;
import java.util.function.Function;

import remote_webview.interfaces.IPresentationListener;
import remote_webview.service.MainServicePresenter;
import remote_webview.utils.LogUtil;


/**
 * I try to hook the {@link InputMethodManager} and listen soft-input's state, but failed cause cache.
 * Than i wanna extends {@link InputMethodManager} but also failed because final.
 *
 * I retry to hook {@link InputMethodManager} constructor or static method, and didn't work
 * because system-hide api.
 *
 * TODO So may be try native hook.
 *
 * This is plan B.
 */
public class InputToggleDelegate {
    private static final String TAG = "InputToggleDelegate";

    private String targetClassName = "ImeAdapterImpl";

    private String targetMethod = "updateState";

    /**
     *
     */
    private boolean canTrigger = false;

    private final Debounce debounce = new Debounce();

    private Stack<IPresentationListener> stateListener;

    public void registerPresentationListener(IPresentationListener presentationListener) {
        stateListener.push(presentationListener);
    }

    public void removePresentationListener(IPresentationListener listener) {
        stateListener.remove(listener);
    }

    private IPresentationListener getTopListener() {
        return stateListener.peek();
    }

    public void switchTrigger(boolean state) {
        canTrigger = state;
    }
    
    public boolean isTriggerOn() {
        return canTrigger;
    }

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
        LogUtil.logStackTree(TAG, stackTraceElements);
        boolean hit = parse(stackTraceElements);
        try {
            if(hit) {
                debounce.handle((a)->{
                    if(getTopListener().getPresentationRunningState() == PresentationRunningState.Idle) {
                        requestToggleSoftInput();
                    }
                    return null;
                });

            }
        }catch (Exception e) {
            e.printStackTrace();
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
