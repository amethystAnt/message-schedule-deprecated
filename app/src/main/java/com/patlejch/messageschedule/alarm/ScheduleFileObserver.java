package com.patlejch.messageschedule.alarm;

import android.support.annotation.Nullable;

import com.patlejch.messageschedule.app.MyApplication;
import com.patlejch.messageschedule.data.MessageDataSource;
import com.patlejch.messageschedule.fileobserver.FixedFileObserver;

import static android.os.FileObserver.MODIFY;

public class ScheduleFileObserver extends FixedFileObserver {

    public ScheduleFileObserver() {
        super(MessageDataSource.getInstance()
                        .getMessagesDatabaseFile(MessageDataSource.MessagesListType.LIST_SCHEDULE)
                        .getAbsolutePath(),
                MODIFY);
    }

    @Override
    public void onEvent(int event, @Nullable String path) {
        if (event != MODIFY) {
            return;
        }
        SendAlarmManager.createAlarm(MyApplication.getInstance());
    }

}
