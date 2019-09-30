package ru.mertsalovda.roomdemoapp.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.database.Cursor;

import java.util.List;

@Dao // обозначает класс для работы с таблицами
public interface MusicDao {

    // Album------------------------------------------

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAlbums(List<Album> albums);

    @Insert
    void insertAlbum(Album album);

    @Query("select * from album")
    List<Album> getAlbums();

    @Query("select * from album where id = :albumId")
    Album getAlbumById(int albumId);

    @Update()
    void updateAlbum(Album album);

    @Delete
    void deleteAlbum(Album... album);

    // Song-------------------------------------------

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSongs(List<Song> songs);

    @Insert
    void insertSong(Song song);

    @Query("select * from song")
    List<Song> getSongs();

    @Query("select * from song where id = :songId")
    Song getSongById(int songId);

    @Update()
    void updateSong(Song song);

    @Delete
    void deleteSong(Song... song);

    @Query("select * from song inner join albumsong on song.id " +
            "= albumsong.song_id where album_id = :albumId")
    List<Song> getSongsFromAlbum(int albumId);

    // AlbumSong--------------------------------------

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void setLinksAlbumSongs(List<AlbumSong> linksAlbumSongs);

    @Insert
    void insertAlbumSong(AlbumSong albumSong);

    @Query("select * from albumsong")
    List<AlbumSong> getAlbumSong();

    @Query("select * from albumsong where id = :albumSongId")
    AlbumSong getAlbumSongById(int albumSongId);

    @Query("select * from albumsong where album_id = :id")
    List<AlbumSong> getAlbumsFromAlbumSong(int id);

    @Query("select * from albumsong where song_id = :id")
    List<AlbumSong> getSongsFromAlbumSong(int id);

    @Update()
    void updateAlbumSong(AlbumSong albumSong);

    @Delete
    void deleteAlbumSong(AlbumSong... albumSong);

    // Cursors----------------------------------------

    @Query("select * from song")
    Cursor getSongsCursor();

    @Query("select * from album")
    Cursor getAlbumsCursor();

    @Query("select * from albumsong inner join song, album on song.id " +
            "= albumsong.song_id and album.id = albumsong.album_id")
    Cursor getAlbumSongCursor();

    @Query("select * from album where id = :albumId")
    Cursor getAlbumWithIdCursor(int albumId);

    @Query("select * from song where id = :songId")
    Cursor getSongWithIdCursor(int songId);
    // Объединяю 3 таблицы в одну
    @Query("select * from albumsong inner join song, album on song.id " +
            "= albumsong.song_id and album.id = albumsong.album_id where albumsong.id = :albumSongId")
    Cursor getAlbumSongWithIdCursor(int albumSongId);
}
