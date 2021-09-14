package remote_webview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.util.DisplayMetrics;
import android.view.Surface;

import remote_webview.service.MainServicePresenter;
import remote_webview.view.WebViewPresentation;


public class RemoteZygoteActivity extends Activity {

    public static RemoteZygoteActivity zygoteActivity;
    
    public static void startZygoteActivity() {
        Intent intent = new Intent(MainServicePresenter.getInstance().getContext()
                , RemoteZygoteActivity.class);
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
        final VirtualDisplay vd = displayManager.createVirtualDisplay("remote_web_view" + id,
                sw, sh, densityDpi, surface, 0);
        //todo method channel
        return new WebViewPresentation(zygoteActivity, vd.getDisplay(), null, id);
    }


    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}
