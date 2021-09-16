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

import androidx.annotation.Nullable;

import remote_webview.service.MainServicePresenter;
import remote_webview.utils.LogUtil;
import remote_webview.view.WebViewPresentation;


public class RemoteZygoteActivity extends Activity {

    public static RemoteZygoteActivity zygoteActivity;
    
    public static void startZygoteActivity() {
        Intent intent = new Intent(MainServicePresenter.getInstance().getContext()
                , RemoteZygoteActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MainServicePresenter
                .getInstance()
                .getContext()
                .startActivity(intent);
    }

    public RemoteZygoteActivity() {
        zygoteActivity = this;
    }

    public static WebViewPresentation generateWebViewPresentation(int id, Surface surface) throws Exception {
        final DisplayMetrics dm = zygoteActivity.getResources().getDisplayMetrics();
        int sw = dm.widthPixels;
        int sh = dm.heightPixels;
        int densityDpi = dm.densityDpi;
        DisplayManager displayManager = (DisplayManager) zygoteActivity.getSystemService(Context.DISPLAY_SERVICE);
        final VirtualDisplay vd = displayManager.createVirtualDisplay("remote_web_view_" + id,
                sw, sh, densityDpi, surface, 0);
        //todo method channel
        return new WebViewPresentation(zygoteActivity, vd.getDisplay(), null, id);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.logMsg("RemoteZygoteActivity", "protected onCreate");
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}
