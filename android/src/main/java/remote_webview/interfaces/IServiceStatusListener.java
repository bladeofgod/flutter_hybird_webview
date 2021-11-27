package remote_webview.interfaces;

/**
 * @author LiJiaqi
 * @date 2021/11/27
 * Description: {@link remote_webview.service.manager.RemoteViewModuleManager}
 */
interface IServiceStatusListener {

    void serviceConnectedCallback();

    void serviceDisConnectedCallback();
}
