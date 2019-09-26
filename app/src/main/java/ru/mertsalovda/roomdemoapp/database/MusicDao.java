package ru.mertsalovda.roomdemoapp.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.database.Cursor;

import java.util.List;

@Dao // обозначает класс для работы с таблицами
public interface MusicDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAlbums(List<Album> albums);

    // добавить песни в таблицу
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSongs(List<Song> songs);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void setLinksAlbumSongs(List<AlbumSong> linksAlbumSongs);

    // получить список всех песен из таблицы

    @Query("select * from song")
    List<Song> getSongs();

    @Delete
    void deleteAlbum(Album album);

    @Query("select * from song inner join albumsong on song.id " +
            "= albumsong.song_id where album_id = :albumId")
    List<Song> getSongsFromAlbum(int albumId);

    @Query("select * from album")
    List<Album> getAlbums();

    @Query("select * from albumsong")
    List<AlbumSong> getAlbumSong();

    // Cursors

    @Query("select * from song")
    Cursor getSongsCursor();

    @Query("select * from album")
    Cursor getAlbumsCursor();

    @Query("select * from album where id = :albumId")
    Cursor getAlbumWithIdCursor(int albumId);

//    @Query("select * from albumsong inner join song, album on song.id " +
//            "= albumsong.song_id and album.id = albumsong.album_id")
    @Query("select * from albumsong")
    Cursor getAlbumSongCursor();
}
