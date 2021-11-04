package remote_webview.view;

import android.view.View;
import android.view.accessibility.AccessibilityEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.flutter.view.AccessibilityBridge;

public class RemoteAccessibilityEventsDelegate {
    private AccessibilityBridge accessibilityBridge;

    public RemoteAccessibilityEventsDelegate() {
    }

    public boolean requestSendAccessibilityEvent(@NonNull View embeddedView, @NonNull View eventOrigin, @NonNull AccessibilityEvent event) {
        return this.accessibilityBridge == null ? false : this.accessibilityBridge.externalViewRequestSendAccessibilityEvent(embeddedView, eventOrigin, event);
    }

    void setAccessibilityBridge(@Nullable AccessibilityBridge accessibilityBridge) {
        this.accessibilityBridge = accessibilityBridge;
    }
}
