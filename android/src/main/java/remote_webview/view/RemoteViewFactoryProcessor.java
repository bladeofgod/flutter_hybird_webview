package remote_webview.view;

import android.os.Build;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;

import androidx.annotation.BinderThread;
import androidx.annotation.RequiresApi;

import java.util.HashMap;
import java.util.Objects;

import remote_webview.garbage_collect.RemoteGarbageCollector;
import remote_webview.interfaces.IGarbageCleanListener;
import remote_webview.model.WebViewCreationParamsModel;
import remote_webview.utils.LogUtil;
import remote_webview.utils.HandlerUtil;
import remote_webview.view.factory.RemoteWebViewFactory;

/**
 * The {@link remote_webview.service.binders.RemoteViewFactoryBinder} processor.
 * receive the order from (main)binder and to dispatch to view.
 *
 * e.g create a real-view, dispatch event.
 *
 */

public class RemoteViewFactoryProcessor implements IGarbageCleanListener {

    private static volatile RemoteViewFactoryProcessor singleton;

    public static RemoteViewFactoryProcessor getInstance() {
        if(singleton == null) {
            synchronized (RemoteViewFactoryProcessor.class) {
                if(singleton == null) {
                    singleton = new RemoteViewFactoryProcessor();
                }
            }
        }
        return singleton;
    }

    private RemoteViewFactoryProcessor() {
        RemoteGarbageCollector.getInstance().registerCollectListener(this);
    }

    private final HashMap<Long, RemoteViewPresentation> viewCache = new HashMap<>();

    @BinderThread
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void createWithSurface(final WebViewCreationParamsModel creationParams, final Surface surface) {
        HandlerUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    final long surfaceId = creationParams.getSurfaceId();
                    LogUtil.logMsg("view factory", " createWithSurface  id " + surfaceId);
                    WebViewPresentation presentation = RemoteWebViewFactory.singleton.generateWebViewPresentation(creationParams,surface);
                    viewCache.put(surfaceId, presentation);
                    HandlerUtil.runOnUiThread(new ViewTrigger(surfaceId));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }

    public void dispatchTouchEvent(String surfaceId, MotionEvent event) {
        LogUtil.logMsg("view factory", " dispatchTouchEvent  id " + surfaceId
                + "  cache size :" + viewCache.size());
        try {
            Objects.requireNonNull(viewCache.get(Long.decode(surfaceId))).dispatchTouchEvent(event);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    public void dispatchKeyEvent(String surfaceId, KeyEvent event) {
        LogUtil.logMsg("view factory", " dispatchKeyEvent  id " + surfaceId);
        try {
            Objects.requireNonNull(viewCache.get(Long.parseLong(surfaceId))).dispatchKeyEvent(event);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    @Override
    public void cleanGarbage(final long id) {
        try {
            HandlerUtil.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Objects.requireNonNull(viewCache.get(id)).dispose();
                }
            });
        }catch (Exception e) {
            e.printStackTrace();
        }
        LogUtil.logMsg(this.toString(), "remove cache view : " +id);
        viewCache.remove(id);
        LogUtil.logMsg(this.toString(),"after cleanGarbage ,viewCache size: "
                + viewCache.size());
    }

    @Override
    public void cleanAll() {
        try {
            for(RemoteViewPresentation cache : viewCache.values()) {
                cache.dispose();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        viewCache.clear();
    }


    class ViewTrigger implements Runnable{
        final long viewId;

        //maybe need object pool
        ViewTrigger(long viewId) {
            this.viewId = viewId;
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void run() {
            try {
                viewCache.get(viewId).create();
                viewCache.get(viewId).show();
            }catch (NullPointerException e) {
                //todo maybe need notify main-process
                e.printStackTrace();
            }
        }
    }




}









