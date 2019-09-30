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

    public static final String KEY_DATA_ID = "KEY_DATA_ID";
    public static final String KEY_DATA_2 = "KEY_DATA_2";
    public static final String KEY_DATA_3 = "KEY_DATA_3";

    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    private static final int ALBUM_TABLE_CODE = 100;
    private static final int ALBUM_ROW_CODE = 101;

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

    @Override // Возвращаю uri="", если запись с ID уже существует и вставка не была выполнена.
    public Uri insert(Uri uri, ContentValues values) {
        int code = URI_MATCHER.match(uri);
        if (code != ALBUM_ROW_CODE && code != ALBUM_TABLE_CODE
                && code != SONG_ROW_CODE && code != SONG_TABLE_CODE
                && code != ALBUMSONG_ROW_CODE && code != ALBUMSONG_TABLE_CODE) return null;
        int _ID;
        String name;
        switch (code) {
            case ALBUM_TABLE_CODE:
                _ID = values.getAsInteger(KEY_DATA_ID); // Достаю ID
                if (mMusicDao.getAlbumById(_ID) == null) { // Проверка отсутсвия записи по ID
                    name = values.getAsString(KEY_DATA_2);
                    String release = values.getAsString(KEY_DATA_3);
                    mMusicDao.insertAlbum(new Album(_ID, name, release)); // вставка
                } else {
                    uri = Uri.parse("");
                }
                break;
            case SONG_TABLE_CODE:
                _ID = values.getAsInteger(KEY_DATA_ID);
                if (mMusicDao.getSongById(_ID) == null) {
                    name = values.getAsString(KEY_DATA_2);
                    String duration = values.getAsString(KEY_DATA_3);
                    mMusicDao.insertSong(new Song(_ID, name, duration));
                } else {
                    uri = Uri.parse("");
                }
                break;
            case ALBUMSONG_TABLE_CODE:
                int album_id = values.getAsInteger(KEY_DATA_2);
                int song_id = values.getAsInteger(KEY_DATA_3);
                if (albumAndSongIsNotNull(album_id, song_id)) { // Не вставляю запись, если нет соответствиющих Album и Song (связывать нечего)
                    mMusicDao.insertAlbumSong(new AlbumSong(album_id, song_id));
                } else {
                    uri = Uri.parse("");
                }
                break;
        }
        return uri;
    }

    private boolean albumAndSongIsNotNull(int album_id, int song_id) {
        boolean result = false;
        if (mMusicDao.getAlbumById(album_id) != null
                && mMusicDao.getSongById(song_id) != null)
            result = true;
        return result;
    }

    @Override   // Если запись не обновлена возращаю -1. Люлое положительное число означает успех или 0.
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int updated = -1;
        int code = URI_MATCHER.match(uri);
        if (code != ALBUM_ROW_CODE && code != ALBUM_TABLE_CODE
                && code != SONG_ROW_CODE && code != SONG_TABLE_CODE
                && code != ALBUMSONG_ROW_CODE && code != ALBUMSONG_TABLE_CODE) return 0;
        int _ID = (int) ContentUris.parseId(uri);
        String name;
        switch (code) {
            case ALBUM_ROW_CODE:
                if (mMusicDao.getAlbumById(_ID) != null) { // Запись должна существовать
                    name = values.getAsString(KEY_DATA_2);
                    String release = values.getAsString(KEY_DATA_3);
                    mMusicDao.updateAlbum(new Album(_ID, name, release)); // Обновление
                    updated++;
                }
                break;
            case SONG_ROW_CODE:
                if (mMusicDao.getSongById(_ID) != null) {
                    name = values.getAsString(KEY_DATA_2);
                    String duration = values.getAsString(KEY_DATA_3);
                    mMusicDao.updateSong(new Song(_ID, name, duration));
                    updated++;
                }
                break;
            case ALBUMSONG_ROW_CODE:
                if (values.getAsString(KEY_DATA_2) != null && values.getAsString(KEY_DATA_3) != null) {
                    int album_id = values.getAsInteger(KEY_DATA_2);
                    int song_id = values.getAsInteger(KEY_DATA_3);
                    if (mMusicDao.getAlbumSongById(_ID) != null
                            && albumAndSongIsNotNull(album_id, song_id)) {
                        mMusicDao.updateAlbumSong(new AlbumSong(_ID, album_id, song_id));
                        updated++;
                    }
                } else { // Если KEY_DATA_2 и KEY_DATA_3 == null удаляю запись с ID (связь записей больше не существует)
                    if (mMusicDao.getAlbumSongById(_ID) != null) {
                        AlbumSong albumSong = new AlbumSong();
                        albumSong.setId(_ID);
                        mMusicDao.deleteAlbumSong(albumSong);
                        updated++;
                    }
                }
                break;
        }
        return updated;
    }

    @Override //Если запись не удалена возращаю -1. Возвращаю колличество удалённых записей
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int code = URI_MATCHER.match(uri);
        if (code != ALBUM_ROW_CODE && code != ALBUM_TABLE_CODE
                && code != SONG_ROW_CODE && code != SONG_TABLE_CODE
                && code != ALBUMSONG_ROW_CODE && code != ALBUMSONG_TABLE_CODE) return 0;
        int id = (int) ContentUris.parseId(uri);
        int deleted = -1;
        List<AlbumSong> albumSongs;
        switch (code) {
            case ALBUM_ROW_CODE:
                Album album = mMusicDao.getAlbumById(id);
                if (album != null) {
                    deleted =0;
                    albumSongs = mMusicDao.getAlbumsFromAlbumSong(id); // нахожу все связанные записи в AlbumSong и даляю их иначе ошибка
                    for (AlbumSong as : albumSongs) {
                        mMusicDao.deleteAlbumSong(as);
                        deleted++;
                    }
                    mMusicDao.deleteAlbum(album);
                    deleted++;
                }
                break;
            case SONG_ROW_CODE:
                Song song = mMusicDao.getSongById(id);
                if (song != null) {
                    deleted =0;
                    albumSongs = mMusicDao.getSongsFromAlbumSong(id);
                    for (AlbumSong as : albumSongs) {
                        mMusicDao.deleteAlbumSong(as);
                        deleted++;
                    }
                    mMusicDao.deleteSong(song);
                    deleted++;
                }
                break;
            case ALBUMSONG_ROW_CODE:
                AlbumSong albumSong = mMusicDao.getAlbumSongById(id);
                if (albumSong != null) {
                    deleted =0;
                    mMusicDao.deleteAlbumSong(albumSong);
                    deleted++;
                }
                break;
        }
        return deleted;
    }
}
