package com.patlejch.messageschedule.app;

import android.app.Application;

import com.patlejch.messageschedule.alarm.ScheduleFileObserver;

public class MyApplication extends Application {

    private static MyApplication INSTANCE = null;

    private ScheduleFileObserver scheduleObserver;

    public static MyApplication getInstance() {
        return INSTANCE;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;

        scheduleObserver = new ScheduleFileObserver();
        scheduleObserver.startWatching();

    }

}
