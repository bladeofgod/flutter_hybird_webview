package remote_webview.interfaces;

import android.os.Bundle;

import androidx.annotation.BinderThread;


/**
 * Align to Main process binder, actual it's action-mocker of real binder.
 *
 * @see remote_webview.service.binders.MainProcessBinder
 */
public interface IMainProcessBinderAction {

    @BinderThread
    void showSoftInput(long viewId);

    @BinderThread
    void hideSoftInput(long viewId);

    @BinderThread
    Bundle getSavedInstance();

    @BinderThread
    void setSavedInstance(Bundle status);
}
