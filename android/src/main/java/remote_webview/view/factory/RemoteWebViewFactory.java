package remote_webview.view.factory;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.util.DisplayMetrics;
import android.view.Surface;

import androidx.annotation.NonNull;

import remote_webview.model.WebViewCreationParamsModel;
import remote_webview.utils.LogUtil;
import remote_webview.view.RemoteAccessibilityEventsDelegate;
import remote_webview.view.WebViewPresentation;

/**
 * @author LiJiaqi
 * @date 2021/12/4
 * Description:
 */
public class RemoteWebViewFactory {
    public static RemoteWebViewFactory singleton;
    static {
        singleton = new RemoteWebViewFactory();
    }
    private RemoteWebViewFactory() {}

    @NonNull
    private Context context;

    final RemoteAccessibilityEventsDelegate remoteAccessibilityEventsDelegate = new RemoteAccessibilityEventsDelegate();


    public void initFactory(Context context) {
        this.context = context;
    }
    
    

    public WebViewPresentation generateWebViewPresentation(WebViewCreationParamsModel creationParams, Surface surface) throws Exception {
        final DisplayMetrics dm = context.getResources().getDisplayMetrics();
//        int sw = dm.widthPixels;
//        int sh = dm.heightPixels;
        int densityDpi = dm.densityDpi;
        DisplayManager displayManager = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
        final VirtualDisplay vd = displayManager.createVirtualDisplay("remote_web_view_" + creationParams.getSurfaceId(),
                creationParams.getPhysicalWidth(), creationParams.getPhysicalHeight(), densityDpi, surface, 0);
        LogUtil.logMsg("RemoteZygoteActivity","VirtualDisplay old display id : "
                + vd.getDisplay().getDisplayId());

        return new WebViewPresentation(context, creationParams, vd.getDisplay(), creationParams.getSurfaceId(),
                remoteAccessibilityEventsDelegate);
    }

}








