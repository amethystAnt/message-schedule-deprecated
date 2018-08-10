package com.patlejch.messageschedule.boot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.patlejch.messageschedule.alarm.SendAlarmManager;
import com.patlejch.messageschedule.app.MyApplication;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == null
                || !intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
            return;
        }
        SendAlarmManager.createAlarm(context, MyApplication.getInstance().getSingletonComponent());
    }

}
