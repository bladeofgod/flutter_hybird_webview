package remote_webview.service.hub;


import remote_webview.interfaces.IGarbageCleanListener;
import remote_webview.garbage_collect.RemoteGarbageCollector;
import remote_webview.utils.LogUtil;

/**
 * view's private-binder hub.
 * dispatch the order from main-process to the view according id.
 */
public class RemoteBinderCommHub extends BinderCommunicateHub<RemoteCallbackHandler> implements IGarbageCleanListener {

    private static volatile RemoteBinderCommHub singleton;

    public static RemoteBinderCommHub getInstance() {
        if(singleton == null) {
            synchronized (RemoteBinderCommHub.class) {
                if(singleton == null) {
                    singleton = new RemoteBinderCommHub();
                }
            }
        }
        return singleton;
    }
    
    private RemoteBinderCommHub(){
        RemoteGarbageCollector.getInstance().registerCollectListener(this);
    }

    @Override
    public void cleanGarbage(long id) {
        LogUtil.logMsg(this.toString(),"start cleanGarbage id : %s  methodHandlerSlot size %s"
                ,String.valueOf(id),String.valueOf(methodHandlerSlot.size()));
        plugOutMethodHandler(id);
        LogUtil.logMsg(this.toString(),"after cleanGarbage ,methodHandlerSlot size: "
                + methodHandlerSlot.size());
    }

    @Override
    public void cleanAll() {
        cleanAllMethodHandler();
        cleanAllMethodResultCallback();
    }

    @Override
    protected RemoteCallbackHandler getCallbackHandler(long id) {
        return new RemoteCallbackHandler(id);
    }


    @Override
    protected String getTag() {
        return MainBinderCommHub.class.getSimpleName();
    }
}
