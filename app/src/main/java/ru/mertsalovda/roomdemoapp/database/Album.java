package ru.mertsalovda.roomdemoapp.database;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Album {

    @PrimaryKey
    @ColumnInfo(name = "id")
    private int mId;

    @ColumnInfo(name = "name")
    private String mName;

    @ColumnInfo(name = "release")
    private String mReleaseDate;

    public Album() {
    }

    public Album(int mId, String mName, String mReleaseDate) {
        this.mId = mId;
        this.mName = mName;
        this.mReleaseDate = mReleaseDate;
    }

    public int getId() {
        return mId;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public void setReleaseDate(String mReleaseDate) {
        this.mReleaseDate = mReleaseDate;
    }

    @Override
    public String toString() {
        return "Album{" +
                "Id=" + mId +
                ", Name='" + mName +
                ", ReleaseDate='" + mReleaseDate + "}";
    }
}
