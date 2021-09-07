package com.lijiaqi.remote_webview.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class MethodModel implements Parcelable {

    /**
     * surface's id,and represent a web-view.
     * must be unique.
     */
    private int id;

    //invoked method's name
    private String methodName;

    private HashMap arguments = new HashMap<>();

    protected MethodModel(Parcel in) {
        id = in.readInt();
        methodName = in.readString();
        arguments = in.readHashMap(HashMap.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(methodName);
        dest.writeMap(arguments);
        dest.writeInt(id);
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

    @Override
    public String toString() {
        return "MethodModel{" +
                "id=" + id +
                ", methodName='" + methodName + '\'' +
                ", arguments=" + arguments +
                '}';
    }
}
