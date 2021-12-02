package remote_webview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.DisplayMetrics;
import android.view.Surface;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import io.flutter.embedding.android.FlutterActivity;
import remote_webview.input_hook.InputMethodHolder;
import remote_webview.input_hook.OnInputMethodListener;
import remote_webview.model.WebViewCreationParamsModel;
import remote_webview.service.MainServicePresenter;
import remote_webview.utils.LogUtil;
import remote_webview.view.RemoteAccessibilityEventsDelegate;
import remote_webview.view.WebViewPresentation;


public class RemoteZygoteActivity extends FlutterActivity {

    public static RemoteZygoteActivity zygoteActivity;
    
    public static void startZygoteActivity() {
        if(zygoteActivity != null) return;
        Intent intent = new Intent(MainServicePresenter.getInstance().getContext()
                , RemoteZygoteActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MainServicePresenter
                .getInstance()
                .getContext()
                .startActivity(intent);
    }
    static OnInputMethodListener onInputMethodListener = new OnInputMethodListener() {
        @Override
        public void onShow(boolean result) {
            Toast.makeText(zygoteActivity, "Show input method! " + result, Toast.LENGTH_SHORT).show();
            LogUtil.logMsg(zygoteActivity.toString(), "Show input method! ");
        }

        @Override
        public void onHide(boolean result) {
            Toast.makeText(zygoteActivity, "Hide input method! " + result, Toast.LENGTH_SHORT).show();
        }
    };
    public RemoteZygoteActivity() {
        zygoteActivity = this;
        InputMethodHolder.registerListener(onInputMethodListener);
    }

    final RemoteAccessibilityEventsDelegate remoteAccessibilityEventsDelegate = new RemoteAccessibilityEventsDelegate();

    public static WebViewPresentation generateWebViewPresentation(WebViewCreationParamsModel creationParams, Surface surface) throws Exception {
        final DisplayMetrics dm = zygoteActivity.getResources().getDisplayMetrics();
//        int sw = dm.widthPixels;
//        int sh = dm.heightPixels;
        int densityDpi = dm.densityDpi;
        DisplayManager displayManager = (DisplayManager) zygoteActivity.getSystemService(Context.DISPLAY_SERVICE);
        final VirtualDisplay vd = displayManager.createVirtualDisplay("remote_web_view_" + creationParams.getSurfaceId(),
                creationParams.getPhysicalWidth(), creationParams.getPhysicalHeight(), densityDpi, surface, 0);
        return new WebViewPresentation(zygoteActivity, creationParams, vd.getDisplay(), creationParams.getSurfaceId(),
                zygoteActivity.remoteAccessibilityEventsDelegate);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InputMethodHolder.init(this);
        LogUtil.logMsg("RemoteZygoteActivity", "protected onCreate");
    }

    @Override
    protected void onResume() {
        super.onResume();
        MainServicePresenter.getInstance().initConnectService();
        finish();
    }

    @Override
    protected void onStop() {
        LogUtil.logMsg(this.toString(), "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        LogUtil.logMsg(this.toString(), "onDestroy");
        super.onDestroy();
    }

    @Override
    public void finish() {
        overridePendingTransition(0, 0);
        super.finish();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}
