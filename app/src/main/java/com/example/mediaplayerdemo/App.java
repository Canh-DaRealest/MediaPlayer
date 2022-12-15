package com.example.mediaplayerdemo;

import android.app.Application;

public class App extends Application {
    private static App instance;
    private Storage storage;

    public static App getInstance() {
        return instance;
    }



    public Storage getStorage() {
        return storage;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        storage = new Storage();

    }

}
