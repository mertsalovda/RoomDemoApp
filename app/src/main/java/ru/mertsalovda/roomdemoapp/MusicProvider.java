package ru.mertsalovda.roomdemoapp;

import android.arch.persistence.room.Room;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import java.util.List;

import ru.mertsalovda.roomdemoapp.database.Album;
import ru.mertsalovda.roomdemoapp.database.AlbumSong;
import ru.mertsalovda.roomdemoapp.database.MusicDao;
import ru.mertsalovda.roomdemoapp.database.MusicDatabase;
import ru.mertsalovda.roomdemoapp.database.Song;

public class MusicProvider extends ContentProvider {

    private static final String TAG = MusicProvider.class.getSimpleName();
    private static final String AUTHORITY = "ru.mertsalovda.roomdemoapp.musicprovider";
    private static final String TABLE_ALBUM = "album";
    private static final String TABLE_SONG = "song";
    private static final String TABLE_ALBUMSONG = "albumsong";

    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    private static final int ALBUM_TABLE_CODE = 100;
    private static final int ALBUM_ROW_CODE = 101;
    private static final int ALBUM_TABLE_CODE_ID = 110;
    private static final int ALBUM_ROW_CODE_ID = 111;

    private static final int SONG_TABLE_CODE = 200;
    private static final int SONG_ROW_CODE = 201;

    private static final int ALBUMSONG_TABLE_CODE = 300;
    private static final int ALBUMSONG_ROW_CODE = 301;

    static {
        URI_MATCHER.addURI(AUTHORITY, TABLE_ALBUM, ALBUM_TABLE_CODE);
        URI_MATCHER.addURI(AUTHORITY, TABLE_ALBUM + "/*", ALBUM_ROW_CODE);

        URI_MATCHER.addURI(AUTHORITY, TABLE_SONG, SONG_TABLE_CODE);
        URI_MATCHER.addURI(AUTHORITY, TABLE_SONG + "/*", SONG_ROW_CODE);

        URI_MATCHER.addURI(AUTHORITY, TABLE_ALBUMSONG, ALBUMSONG_TABLE_CODE);
        URI_MATCHER.addURI(AUTHORITY, TABLE_ALBUMSONG + "/*", ALBUMSONG_ROW_CODE);
    }

    private MusicDao mMusicDao;

    public MusicProvider() {
    }

    @Override
    public boolean onCreate() {
        if (getContext() != null) {
            mMusicDao = Room.databaseBuilder(getContext().getApplicationContext(),
                    MusicDatabase.class,
                    "music_database")
                    .build()
                    .getMusicDao();
            return true;
        }
        return false;
    }

    @Override
    public String getType(Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case ALBUM_TABLE_CODE:
                return "vnd.android.cursor.dir/" + AUTHORITY + "." + TABLE_ALBUM;
            case ALBUM_ROW_CODE:
                return "vnd.android.cursor.item/" + AUTHORITY + "." + TABLE_ALBUM;

            case SONG_TABLE_CODE:
                return "vnd.android.cursor.dir/" + AUTHORITY + "." + TABLE_SONG;
            case SONG_ROW_CODE:
                return "vnd.android.cursor.item/" + AUTHORITY + "." + TABLE_SONG;

            case ALBUMSONG_TABLE_CODE:
                return "vnd.android.cursor.dir/" + AUTHORITY + "." + TABLE_ALBUMSONG;
            case ALBUMSONG_ROW_CODE:
                return "vnd.android.cursor.item/" + AUTHORITY + "." + TABLE_ALBUMSONG;

            default:
                throw new UnsupportedOperationException("not yet implemented");
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        int code = URI_MATCHER.match(uri);
        if (code != ALBUM_ROW_CODE && code != ALBUM_TABLE_CODE
                && code != SONG_ROW_CODE && code != SONG_TABLE_CODE
                && code != ALBUMSONG_ROW_CODE && code != ALBUMSONG_TABLE_CODE) return null;

        Cursor cursor;
        switch (code) {
            case ALBUM_TABLE_CODE:
                cursor = mMusicDao.getAlbumsCursor();
                break;
            case ALBUM_ROW_CODE:
                cursor = mMusicDao.getAlbumWithIdCursor((int) ContentUris.parseId(uri));
                break;
            case SONG_TABLE_CODE:
                cursor = mMusicDao.getSongsCursor();
                break;
            case SONG_ROW_CODE:
                cursor = mMusicDao.getSongWithIdCursor((int) ContentUris.parseId(uri));
                break;
            case ALBUMSONG_TABLE_CODE:
                cursor = mMusicDao.getAlbumSongCursor();
                break;
            case ALBUMSONG_ROW_CODE:
                cursor = mMusicDao.getAlbumSongWithIdCursor((int) ContentUris.parseId(uri));
                break;
            default:
                cursor = null;
                break;
        }
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int code = URI_MATCHER.match(uri);
        if (code != ALBUM_ROW_CODE && code != ALBUM_TABLE_CODE
                && code != SONG_ROW_CODE && code != SONG_TABLE_CODE
                && code != ALBUMSONG_ROW_CODE && code != ALBUMSONG_TABLE_CODE) return 0;
        int id = (int) ContentUris.parseId(uri);
        int deleted = 0;
        List<AlbumSong> albumSongs;
        switch (code) {
            case ALBUM_ROW_CODE:
                Album album = mMusicDao.getAlbumsById(id);
                albumSongs = mMusicDao.getAlbumsFromAlbumSong(id);
                for (AlbumSong as: albumSongs){
                    mMusicDao.deleteAlbumSong(as);
                    deleted++;
                }
                mMusicDao.deleteAlbum(album);
                deleted++;
                break;
            case SONG_ROW_CODE:
                Song song = mMusicDao.getSongById(id);
                albumSongs = mMusicDao.getSongsFromAlbumSong(id);
                for (AlbumSong as: albumSongs){
                    mMusicDao.deleteAlbumSong(as);
                    deleted++;
                }
                mMusicDao.deleteSong(song);
                deleted++;
                break;
            case ALBUMSONG_ROW_CODE:
                AlbumSong albumSong = mMusicDao.getAlbumSongById(id);
                mMusicDao.deleteAlbumSong(albumSong);
                deleted++;
                break;
        }
        return deleted;
    }
}
