package com.patlejch.messageschedule.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.NonNull;

import com.patlejch.messageschedule.R;
import com.patlejch.messageschedule.app.MyApplication;
import com.patlejch.messageschedule.data.Message;
import com.patlejch.messageschedule.data.MessageDataSource;
import com.patlejch.messageschedule.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;

public class SendAlarmManager {

    private static final String TAG_NOTIFICATION_ERROR_ALARM = "TAG_NOTIFICATION_ERROR_ALARM";

    public static void createAlarm(@NonNull Calendar time, @NonNull Context context) {

        cancelAlarm(context);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, SendAlarmReceiver.class);
        intent.setAction(SendAlarmReceiver.ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= 23) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    time.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(),
                    pendingIntent);
        }

    }

    public static void createAlarm(@NonNull final Context context) {

        MessageDataSource messageDataSource = MessageDataSource.getInstance();
        messageDataSource.fetchList(messageDataSource.getMessagesDatabaseFile(MessageDataSource
                        .MessagesListType.LIST_SCHEDULE),
                new MessageDataSource.MessageListFetchCallback() {
                    @Override
                    public void onMessageListFetched(ArrayList<Message> messages) {

                        if (messages.isEmpty()) {
                            SendAlarmManager.cancelAlarm(context);
                            return;
                        }

                        Utils.sortMessageList(messages);
                        SendAlarmManager.createAlarm(messages.get(0).time, context);

                    }

                    @Override
                    public void onError() {
                        Resources resources = MyApplication.getInstance().getResources();
                        Utils.showNotification(resources.getString(R.string.notification_title_warning),
                                resources.getString(R.string.notification_text_error_alarm),
                                TAG_NOTIFICATION_ERROR_ALARM, false, false, null);

                    }
                });

    }

    public static void cancelAlarm(@NonNull Context context) {

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, SendAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);

    }

}
