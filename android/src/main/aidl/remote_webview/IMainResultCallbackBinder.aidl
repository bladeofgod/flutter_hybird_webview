// IMainResultCallbackBinder.aidl
package remote_webview;

// Declare any non-default types here with import statements

interface IMainResultCallbackBinder {

    void remoteSuccess(long id, String result);

    void remoteError(long id, String var1, String var2, String info);

    void remoteNotImplemented(long id);

}