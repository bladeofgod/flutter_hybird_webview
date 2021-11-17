package remote_webview.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

import remote_webview.interfaces.IMockMethodResult;

public class MethodModel implements Parcelable {

    /**
     * surface's id,and represent a remote-view, e.g web-view.
     * must be unique.
     */
    private long id;

    //invoked method's name
    private String methodName;
    
    //invoked time-stamp, usually use it to mark a invoke-method.
    private long invokeTimeStamp;

    private HashMap arguments = new HashMap<>();

    /**
     * 0 means didn't care result , and will not register a callback in hub.
     * @see IMockMethodResult
     * @see remote_webview.service.hub.BinderCommunicateHub
     */
    private byte needCallback;

    public MethodModel(long id, String methodName, HashMap arguments, long invokeTimeStamp, byte needCallback) {
        this.id = id;
        this.methodName = methodName;
        this.arguments = arguments;
        this.invokeTimeStamp = invokeTimeStamp;
        this.needCallback = needCallback;
    }

    protected MethodModel(Parcel in) {
        id = in.readLong();
        methodName = in.readString();
        arguments = in.readHashMap(HashMap.class.getClassLoader());
        invokeTimeStamp = in.readLong();
        needCallback = in.readByte();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(methodName);
        dest.writeMap(arguments);
        dest.writeLong(invokeTimeStamp);
        dest.writeByte(needCallback);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MethodModel> CREATOR = new Creator<MethodModel>() {
        @Override
        public MethodModel createFromParcel(Parcel in) {
            return new MethodModel(in);
        }

        @Override
        public MethodModel[] newArray(int size) {
            return new MethodModel[size];
        }
    };

    public long getId() {
        return id;
    }

    public String getMethodName() {
        return methodName;
    }

    public HashMap getArguments() {
        return arguments;
    }
    
    public long getInvokeTimeStamp() {return invokeTimeStamp;}

    public void setId(long id) {
        this.id = id;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public void setArguments(HashMap arguments) {
        this.arguments = arguments;
    }

    public void setNeedCallback(byte needCallback) {
        this.needCallback = needCallback;
    }

    public byte getNeedCallback() {
        return needCallback;
    }

    @Override
    public String toString() {
        return "MethodModel{" +
                "invokeTimeStamp=" + invokeTimeStamp +
                "id=" + id +
                ", methodName='" + methodName + '\'' +
                ", arguments=" + arguments +
                '}';
    }
}
