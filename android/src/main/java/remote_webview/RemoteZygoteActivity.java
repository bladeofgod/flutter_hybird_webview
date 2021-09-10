package remote_webview;

import android.app.Activity;


public class RemoteZygoteActivity extends Activity {

    public static RemoteZygoteActivity zygoteActivity;

    public RemoteZygoteActivity() {
        zygoteActivity = this;
    }

}
