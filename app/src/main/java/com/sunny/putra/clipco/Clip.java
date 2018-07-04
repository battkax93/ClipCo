package com.sunny.putra.clipco;

import android.os.Parcel;
import android.os.Parcelable;

public class Clip implements Parcelable {
    public Long id;
    public String clip;
    public String timesamp;

    public Clip (Long id, String clip, String timesamp){
        this.id=id;
        this.clip=clip;
        this.timesamp=timesamp;
    }

    public Clip(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        clip = in.readString();
        timesamp = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(clip);
        dest.writeString(timesamp);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Clip> CREATOR = new Creator<Clip>() {
        @Override
        public Clip createFromParcel(Parcel in) {
            return new Clip(in);
        }

        @Override
        public Clip[] newArray(int size) {
            return new Clip[size];
        }
    };

    @Override
    public String toString() {
        return "Clip{" +
                "id=" + id +
                ", clip='" + clip + '\'' +
                ", timesamp='" + timesamp + '\'' +
                '}';
    }
}
