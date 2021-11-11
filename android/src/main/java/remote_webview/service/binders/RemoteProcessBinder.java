package remote_webview.service.binders;

import android.os.RemoteException;

import remote_webview.IRemoteProcessBinder;
import remote_webview.RemoteZygoteActivity;
import remote_webview.utils.LogUtil;

/**
 * {@linkplain RemoteProcessBinder} for communicate to remote-process.
 * 
 * e.g keep {@linkplain RemoteZygoteActivity}, check process-alive.
 * 
 */

public class RemoteProcessBinder extends IRemoteProcessBinder.Stub {
    @Override
    public void initZygoteActivity() throws RemoteException {
        LogUtil.logMsg("RemoteProcessBinder", "initZygoteActivity");
        RemoteZygoteActivity.startZygoteActivity();
    }

    /**
     * 
     * @return 0 is dead, 1 is alive
     * @throws RemoteException
     */
    @Override
    public int isZygoteActivityAlive() throws RemoteException {
        return RemoteZygoteActivity.zygoteActivity == null ? 0 : 1;
    }
}
