// IMainProcessBinder.aidl
package remote_webview;

// Declare any non-default types here with import statements

interface IMainProcessBinder {

    void showSoftInput();

    void hideSoftInput();

    Bundle getSavedInstance();

    void setSavedInstance(in Bundle status);
}