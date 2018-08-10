package com.patlejch.messageschedule.dagger.modules;

import android.content.res.Resources;
import android.support.annotation.NonNull;

import com.patlejch.messageschedule.app.MyApplication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    @Provides
    @Singleton
    static MyApplication provideApplication() {
        return MyApplication.getInstance();
    }

    @Provides
    @Singleton
    static Resources provideResources(@NonNull MyApplication app) {
        return app.getResources();
    }

}
