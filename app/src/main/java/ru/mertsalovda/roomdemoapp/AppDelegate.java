package ru.mertsalovda.roomdemoapp;

import android.app.Application;
import android.arch.persistence.room.Room;

import ru.mertsalovda.roomdemoapp.database.MusicDatabase;

public class AppDelegate extends Application {

    private MusicDatabase mMusicDatabase;

    @Override
    public void onCreate() {
        super.onCreate();

        mMusicDatabase = Room.databaseBuilder(this, MusicDatabase.class,
                "music_database")
                .allowMainThreadQueries()
                .build();
    }

    public MusicDatabase getMusicDatabase() {
        return mMusicDatabase;
    }
}
