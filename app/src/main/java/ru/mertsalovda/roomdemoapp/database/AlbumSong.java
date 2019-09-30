package ru.mertsalovda.roomdemoapp.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

@Entity(foreignKeys = {
        @ForeignKey(entity = Album.class, parentColumns = "id", childColumns = "album_id"),
        @ForeignKey(entity = Song.class, parentColumns = "id", childColumns = "song_id")})
public class AlbumSong {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "album_id")
    private int albumId;

    @ColumnInfo(name = "song_id")
    private int songId;

    public AlbumSong() {
    }

    public AlbumSong(int albumId, int songId) {
        this.albumId = albumId;
        this.songId = songId;
    }

    public AlbumSong(int id, int albumId, int songId) {
        this.id = id;
        this.albumId = albumId;
        this.songId = songId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }

    @Override
    public String toString() {
        return "AlbumSong{" +
                " id=" + id +
                ", albumId=" + albumId +
                ", songId=" + songId +
                '}';
    }
}
