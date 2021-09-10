package remote_webview.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class MethodModel implements Parcelable {

    /**
     * surface's id,and represent a web-view.
     * must be unique.
     */
    private int id;

    //invoked method's name
    private String methodName;
    
    //invoked time-stamp
    private long invokeTimeStamp;

    private HashMap arguments = new HashMap<>();

    public MethodModel(int id, String methodName, HashMap arguments, long invokeTimeStamp) {
        this.id = id;
        this.methodName = methodName;
        this.arguments = arguments;
        this.invokeTimeStamp = invokeTimeStamp;
    }

    protected MethodModel(Parcel in) {
        id = in.readInt();
        methodName = in.readString();
        arguments = in.readHashMap(HashMap.class.getClassLoader());
        invokeTimeStamp = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(methodName);
        dest.writeMap(arguments);
        dest.writeLong(invokeTimeStamp);
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

    public int getId() {
        return id;
    }

    public String getMethodName() {
        return methodName;
    }

    public HashMap getArguments() {
        return arguments;
    }
    
    public long getInvokeTimeStamp() {return invokeTimeStamp;}

    public void setId(int id) {
        this.id = id;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public void setArguments(HashMap arguments) {
        this.arguments = arguments;
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
