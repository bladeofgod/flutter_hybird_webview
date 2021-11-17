package remote_webview.mock;

import android.os.RemoteException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;

import java.util.HashMap;

import remote_webview.model.MethodModel;
import remote_webview.service.MainServicePresenter;

public class MockMethodChannel {
    /**
     * surface's id,and represent a remote-view.
     */
    private long id;

    public MockMethodChannel(long id) {
        this.id = id;
    }

    /**
     * Order from view in a private channel to flutter side.
     * e.g web's js call will by this method and through binder to flutter.
     * @param method name of method.
     * @param arguments the arguments, in JS-call its {@link HashMap<String,String>}
     */
    @UiThread
    public void invokeMethod(@NonNull String method, @Nullable HashMap arguments) throws RemoteException {
        final MethodModel methodModel = new MethodModel(id, method, arguments, System.currentTimeMillis(), (byte) 0);
        MainServicePresenter.getInstance().getMainChannelBinder().invokeMethod(methodModel);
    }
    
}
