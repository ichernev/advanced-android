package com.example.iskren.a07threading;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by iskren on drugs.
 */
class MyParcelable implements Parcelable {

    String str;
    int i;

    public MyParcelable(Parcel in) {
        str = in.readString();
        i = in.readInt();
    }

    public MyParcelable() {
        str = "Tower";
        i = 5;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(str);
        dest.writeInt(i);
    }

    public static final Creator CREATOR = new Creator() {
        public MyParcelable createFromParcel(Parcel in) {
            return new MyParcelable(in);
        }

        public MyParcelable[] newArray(int size) {
            return new MyParcelable[size];
        }
    };
}
