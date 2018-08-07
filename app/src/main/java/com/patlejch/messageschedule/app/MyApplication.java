package com.patlejch.messageschedule.app;

import android.app.Application;

import com.patlejch.messageschedule.alarm.ScheduleFileObserver;
import com.patlejch.messageschedule.notification.MessageSentNotification;

public class MyApplication extends Application {

    private static MyApplication INSTANCE = null;

    private ScheduleFileObserver scheduleObserver;
    private MessageSentNotification messageSentNotification;

    public static MyApplication getInstance() {
        return INSTANCE;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;

        scheduleObserver = new ScheduleFileObserver();
        scheduleObserver.startWatching();

        messageSentNotification = new MessageSentNotification();

    }

    @Override
    public void onTerminate() {
        messageSentNotification.stop();
        scheduleObserver.stopWatching();
        super.onTerminate();
    }
}
