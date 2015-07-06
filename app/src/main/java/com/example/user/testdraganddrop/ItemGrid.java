package com.example.user.testdraganddrop;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by User on 7/4/2015.
 */
public class ItemGrid implements Parcelable {

    public int id;

    public String text;

    @Override
    public String toString() {
        return "ItemGrid{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", icon=" + icon +
                '}';
    }

    public int icon;

    public ItemGrid(int id, String text, int icon) {
        this.id = id;
        this.text = text;
        this.icon = icon;
    }

    protected ItemGrid(Parcel in) {
        id = in.readInt();
        text = in.readString();
        icon = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(text);
        dest.writeInt(icon);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ItemGrid> CREATOR = new Parcelable.Creator<ItemGrid>() {
        @Override
        public ItemGrid createFromParcel(Parcel in) {
            return new ItemGrid(in);
        }

        @Override
        public ItemGrid[] newArray(int size) {
            return new ItemGrid[size];
        }
    };
}