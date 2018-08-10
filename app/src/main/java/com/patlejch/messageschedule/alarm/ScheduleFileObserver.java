package com.patlejch.messageschedule.alarm;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.patlejch.messageschedule.app.MyApplication;
import com.patlejch.messageschedule.data.MessageDataSource;
import com.patlejch.messageschedule.fileobserver.FixedFileObserver;

import javax.inject.Inject;

import static android.os.FileObserver.MODIFY;

public class ScheduleFileObserver extends FixedFileObserver {

    private MyApplication app;

    @Inject
    public ScheduleFileObserver(@NonNull MessageDataSource dataSource,
                                @NonNull MyApplication app) {
        super(dataSource.getMessagesDatabaseFile(MessageDataSource.MessagesListType.LIST_SCHEDULE)
                        .getAbsolutePath(),
                MODIFY);
        this.app = app;
    }

    @Override
    public void onEvent(int event, @Nullable String path) {
        if (event != MODIFY) {
            return;
        }
        SendAlarmManager.createAlarm(app, app.getSingletonComponent());
    }

}
