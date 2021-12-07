// IMainProcessBinder.aidl
package remote_webview;

// Declare any non-default types here with import statements

interface IMainProcessBinder {

    void showSoftInput(long viewId);

    void hideSoftInput(long viewId);

    Bundle getSavedInstance();

    void setSavedInstance(in Bundle status);
}