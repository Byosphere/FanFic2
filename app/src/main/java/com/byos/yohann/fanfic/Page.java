package com.byos.yohann.fanfic;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Yohann on 17/02/2016.
 */
public class Page implements Parcelable {

    private String text;
    private int id;
    private int storyId;

    public Page(int id, String text, int storyId) {
        this.text = text;
        this.id = id;
        this.storyId = storyId;
    }

    public int getId() {
        return id;
    }

    public int getStoryId() {
        return storyId;
    }

    public String getText() {
        return text;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(id);
        dest.writeString(text);
        dest.writeInt(storyId);

    }

    public static final Creator<Page> CREATOR = new Creator<Page>()
    {
        @Override
        public Page createFromParcel(Parcel source)
        {
            return new Page(source);
        }

        @Override
        public Page[] newArray(int size)
        {
            return new Page[size];
        }
    };

    public Page(Parcel in) {

        this.id = in.readInt();
        this.text = in.readString();
        this.storyId = in.readInt();
    }
}
