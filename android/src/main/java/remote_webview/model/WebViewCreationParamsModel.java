package remote_webview.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.List;

public class WebViewCreationParamsModel implements Parcelable {

    long surfaceId;
    
    boolean usesHybridComposition;

    HashMap<String,String> settings;

    List<String> jsNames;

    int autoMediaPlaybackPolicy;

    String userAgent;

    String url;
    
    int physicalWidth;
    
    int physicalHeight;

    //for restore view's status.
    Bundle savedInstance;

    public WebViewCreationParamsModel(long surfaceId, 
                                      boolean usesHybridComposition, 
                                      HashMap<String, String> settings, 
                                      List<String> jsNames, 
                                      int autoMediaPlaybackPolicy, 
                                      String userAgent, 
                                      String url,
                                      int physicalWidth,
                                      int physicalHeight,
                                      Bundle savedInstance) {
        this.surfaceId = surfaceId;
        this.usesHybridComposition = usesHybridComposition;
        this.settings = settings;
        this.jsNames = jsNames;
        this.autoMediaPlaybackPolicy = autoMediaPlaybackPolicy;
        this.userAgent = userAgent;
        this.url = url;
        this.physicalWidth = physicalWidth;
        this.physicalHeight = physicalHeight;
        this.savedInstance = savedInstance;
    }

    public long getSurfaceId() {
        return surfaceId;
    }

    public boolean isUsesHybridComposition() {
        return usesHybridComposition;
    }

    public HashMap<String, String> getSettings() {
        return settings;
    }

    public List<String> getJsNames() {
        return jsNames;
    }

    public int getAutoMediaPlaybackPolicy() {
        return autoMediaPlaybackPolicy;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getUrl() {
        return url;
    }

    public int getPhysicalWidth() {
        return physicalWidth;
    }

    public int getPhysicalHeight() {
        return physicalHeight;
    }

    public Bundle getSavedInstance() {
        return savedInstance;
    }

    protected WebViewCreationParamsModel(Parcel in) {
        surfaceId = in.readLong();
        usesHybridComposition = in.readByte() != 0;
        settings = in.readHashMap(HashMap.class.getClassLoader());
        jsNames = in.createStringArrayList();
        autoMediaPlaybackPolicy = in.readInt();
        userAgent = in.readString();
        url = in.readString();
        physicalWidth = in.readInt();
        physicalHeight = in.readInt();
        savedInstance = in.readBundle();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(surfaceId);
        dest.writeByte((byte) (usesHybridComposition ? 1 : 0));
        dest.writeMap(settings);
        dest.writeStringList(jsNames);
        dest.writeInt(autoMediaPlaybackPolicy);
        dest.writeString(userAgent);
        dest.writeString(url);
        dest.writeInt(physicalWidth);
        dest.writeInt(physicalHeight);
        dest.writeBundle(savedInstance);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<WebViewCreationParamsModel> CREATOR = new Creator<WebViewCreationParamsModel>() {
        @Override
        public WebViewCreationParamsModel createFromParcel(Parcel in) {
            return new WebViewCreationParamsModel(in);
        }

        @Override
        public WebViewCreationParamsModel[] newArray(int size) {
            return new WebViewCreationParamsModel[size];
        }
    };
}
