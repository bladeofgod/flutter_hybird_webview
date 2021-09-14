package remote_webview.service.hub;


/**
 * view's sub binder hub.
 * dispatch the order from main-process to the view according id.
 */
public class RemoteBinderCommHub extends BinderCommunicateHub {

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
    
    private RemoteBinderCommHub(){}

}
