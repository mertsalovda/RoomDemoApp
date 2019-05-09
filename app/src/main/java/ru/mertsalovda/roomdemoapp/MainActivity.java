package ru.mertsalovda.roomdemoapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ru.mertsalovda.roomdemoapp.database.Album;
import ru.mertsalovda.roomdemoapp.database.MusicDao;

public class MainActivity extends AppCompatActivity {

    // 1. Добавить БД Room ++

    // 2. Вставить данные/Извлечь данные ++

    // 3. Добавить ContentProvider над Room

    private Button btnAdd, btnGet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final MusicDao musicDao = ((AppDelegate) getApplicationContext())
                .getMusicDatabase()
                .getMusicDao();

        btnAdd = findViewById(R.id.add);
        btnGet = findViewById(R.id.get);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicDao.insertAlbums(createAlbums());
            }
        });

        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast(musicDao.getAlbums());
            }
        });

    }

    private void showToast(List<Album> albums) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0, size = albums.size(); i < size; i++){
            builder.append(albums.get(i).toString()).append("\n");
        }

        Toast.makeText(this, builder.toString(), Toast.LENGTH_LONG).show();
    }

    private List<Album> createAlbums() {
        List<Album> albums = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            albums.add(new Album(i, "albom " + i,
                    "release " + System.currentTimeMillis()));
        }

        return albums;
    }
}
