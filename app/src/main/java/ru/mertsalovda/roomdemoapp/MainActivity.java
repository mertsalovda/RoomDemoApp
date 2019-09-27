package ru.mertsalovda.roomdemoapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.mertsalovda.roomdemoapp.database.Album;
import ru.mertsalovda.roomdemoapp.database.AlbumSong;
import ru.mertsalovda.roomdemoapp.database.MusicDao;
import ru.mertsalovda.roomdemoapp.database.Song;

public class MainActivity extends AppCompatActivity {

    // 1. Добавить БД Room ++

    // 2. Вставить данные/Извлечь данные ++

    // 3. Добавить ContentProvider над Room

    private Button btnAdd, btnGet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // получаем экземпляр базы данных
        final MusicDao musicDao = ((AppDelegate) getApplicationContext())
                .getMusicDatabase()
                .getMusicDao();

        btnAdd = findViewById(R.id.add);
        btnGet = findViewById(R.id.get);


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Album> albums = createAlbums();
                List<Song> songs = createSongs();
                List<AlbumSong> albumSongList = createAlbumSong(albums, songs);
                musicDao.insertAlbums(albums);
                musicDao.insertSongs(songs);
                musicDao.setLinksAlbumSongs(albumSongList);
            }
        });

        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast(musicDao.getAlbums(), musicDao.getSongs(), musicDao.getAlbumSong());
            }
        });

    }

    private List<AlbumSong> createAlbumSong(List<Album> albums, List<Song> songs) {
        List<AlbumSong> albumSongList = new ArrayList<>();
        int album_id = 0;
        // Распределение по две песни в альбом
        for (int id = 0; id < songs.size(); id++) {
            if (id % 2 == 0 && id != 0)
                album_id++;
            albumSongList.add(new AlbumSong(id+1, albums.get(album_id).getId(), songs.get(id).getId()));
        }
        return albumSongList;
    }

    private List<Album> createAlbums() {
        List<Album> albums = new ArrayList<>();
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        Date date = new Date();
        String fDate = df.format(date);
        for (int id = 0; id < 3; id++) {
            albums.add(new Album(id, "album " + id,
                    "release " + fDate));
        }
        return albums;
    }

    private List<Song> createSongs() {
        List<Song> songs = new ArrayList<>();
        for (int id = 0; id < 6; id++) {
            songs.add(new Song(id, "song " + id,
                    "duration 03:00"));
        }
        return songs;
    }

    private void showToast(List<Album> albums, List<Song> songs, List<AlbumSong> albumSong) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0, size = albums.size(); i < size; i++) {
            builder.append(albums.get(i).toString()).append("\n");
        }
        for (int i = 0, size = songs.size(); i < size; i++) {
            builder.append(songs.get(i).toString()).append("\n");
        }
        for (int i = 0, size = albumSong.size(); i < size; i++) {
            builder.append(albumSong.get(i).toString()).append("\n");
        }

        Toast.makeText(this, builder.toString(), Toast.LENGTH_LONG).show();
    }
}
