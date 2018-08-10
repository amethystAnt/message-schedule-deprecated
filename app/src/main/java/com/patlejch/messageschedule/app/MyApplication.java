package com.patlejch.messageschedule.app;

import android.app.Application;

import com.patlejch.messageschedule.alarm.ScheduleFileObserver;
import com.patlejch.messageschedule.dagger.components.DaggerScheduleObserverComponent;
import com.patlejch.messageschedule.dagger.components.DaggerSingletonComponent;
import com.patlejch.messageschedule.dagger.components.SingletonComponent;
import com.patlejch.messageschedule.notification.MessageSentNotification;

public class MyApplication extends Application {

    private static MyApplication INSTANCE = null;

    private ScheduleFileObserver scheduleObserver;
    private MessageSentNotification messageSentNotification;
    private SingletonComponent singletonComponent;

    public static MyApplication getInstance() {
        return INSTANCE;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;

        singletonComponent = DaggerSingletonComponent.create();
        scheduleObserver = DaggerScheduleObserverComponent.builder()
                .singletonComponent(singletonComponent)
                .build()
                .observer();
        scheduleObserver.startWatching();

        messageSentNotification = new MessageSentNotification(singletonComponent);


    }

    @Override
    public void onTerminate() {
        messageSentNotification.stop();
        scheduleObserver.stopWatching();
        super.onTerminate();
    }

    public SingletonComponent getSingletonComponent() {
        return singletonComponent;
    }

}
