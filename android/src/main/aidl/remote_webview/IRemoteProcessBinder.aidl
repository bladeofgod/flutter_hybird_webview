// IRemoteProcessBinder.aidl
package remote_webview;

// Declare any non-default types here with import statements

interface IRemoteProcessBinder {
    void initZygoteActivity();

    int isZygoteActivityAlive();
}