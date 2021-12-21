package remote_webview.view.factory;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import remote_webview.interfaces.IRemoteView;
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

    /**
     * Remote view model that {@link RemoteWebViewFactory} created, and {@link remote_webview.view.RemoteViewFactoryProcessor}
     * will cache it and manipulate it follow main-process's command.
     */
    public static class RemoteViewModel implements IRemoteView {
        
        private final WebViewPresentation presentation;
        
        private final VirtualDisplay vd;

        public RemoteViewModel(WebViewPresentation presentation, VirtualDisplay vd) {
            this.presentation = presentation;
            this.vd = vd;
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void init() {
            presentation.create();
            presentation.show();
        }

        @Override
        public void release() {
            presentation.dispose();
            vd.release();
        }

        @Override
        public void dispatchTouchEvent(MotionEvent event) {
            presentation.dispatchTouchEvent(event);
        }

        @Override
        public void dispatchKeyEvent(KeyEvent keyEvent) {
            presentation.dispatchKeyEvent(keyEvent);
        }
    }
    
    

    public RemoteViewModel generateWebViewPresentation(WebViewCreationParamsModel creationParams, Surface surface) throws Exception {
        final DisplayMetrics dm = context.getResources().getDisplayMetrics();
//        int sw = dm.widthPixels;
//        int sh = dm.heightPixels;
        int densityDpi = dm.densityDpi;
        DisplayManager displayManager = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
        final VirtualDisplay vd = displayManager.createVirtualDisplay("remote_web_view_" + creationParams.getSurfaceId(),
                creationParams.getPhysicalWidth(), creationParams.getPhysicalHeight(), densityDpi, surface, 0);

        RemoteViewModel remoteViewModel = new RemoteViewModel(new WebViewPresentation(context, creationParams, vd.getDisplay(), creationParams.getSurfaceId(),
                remoteAccessibilityEventsDelegate), vd);

        return remoteViewModel;
    }

}








