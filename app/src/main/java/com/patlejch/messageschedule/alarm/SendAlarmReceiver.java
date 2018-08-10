package com.patlejch.messageschedule.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.patlejch.messageschedule.app.MyApplication;
import com.patlejch.messageschedule.sms.MessageSender;

public class SendAlarmReceiver extends BroadcastReceiver {

    public static final String ACTION = "com.patlejch.messageschedule.alarm";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction() == null || !intent.getAction().equalsIgnoreCase(ACTION)) {
            SendAlarmManager.createAlarm(context, MyApplication.getInstance().getSingletonComponent());
            return;
        }

        MessageSender.sendMessages(context, MyApplication.getInstance().getSingletonComponent());

    }

}
