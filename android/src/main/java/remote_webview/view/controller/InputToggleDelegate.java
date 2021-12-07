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

    public void inputServiceCall() {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
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
        //todo test id : 0
        try {
            MainServicePresenter.getInstance().getMainProcessBinder().showSoftInput(0);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private boolean parse(StackTraceElement[] stackTraceElements) {
        for(int i = 0; i < stackTraceElements.length; i++) {
            if(stackTraceElements[i].getClassName().contains(targetClassName)
                    && stackTraceElements[i].getMethodName().contains(targetMethod)) {
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
