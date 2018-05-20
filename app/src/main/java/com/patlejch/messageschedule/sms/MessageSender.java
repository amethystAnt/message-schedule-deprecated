package com.patlejch.messageschedule.sms;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.telephony.SmsManager;

import com.patlejch.messageschedule.R;
import com.patlejch.messageschedule.alarm.SendAlarmManager;
import com.patlejch.messageschedule.app.MyApplication;
import com.patlejch.messageschedule.data.Message;
import com.patlejch.messageschedule.data.MessageDataSource;
import com.patlejch.messageschedule.utils.Utils;
import com.patlejch.messageschedule.view.MainActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import static com.patlejch.messageschedule.data.MessageDataSource.MessagesListType.LIST_HISTORY;
import static com.patlejch.messageschedule.data.MessageDataSource.MessagesListType.LIST_SCHEDULE;

public class MessageSender {

    private static final String INTENT_FILTER_MESSAGE_SENT = "INTENT_FILTER_MESSAGE_SENT";
    private static final String TAG_NOTIFICATION_ERROR = "TAG_NOTIFICATION_ERROR";

    public static void sendMessages(@NonNull final Context context) {

        final MessageDataSource messageDataSource = MessageDataSource.getInstance();
        final File scheduleFile = messageDataSource.getMessagesDatabaseFile(LIST_SCHEDULE);
        final File historyFile = messageDataSource.getMessagesDatabaseFile(LIST_HISTORY);

        messageDataSource.fetchList(scheduleFile, new MessageDataSource.MessageListFetchCallback() {
            @Override
            public void onMessageListFetched(ArrayList<Message> messages) {

                //the calendar representing current time must be created AFTER the message's calendar
                //for this reason we're not keeping a calendar object, but instead calling Calendar.getInstance() on this line
                Calendar timeNow = Calendar.getInstance();

                SmsManager smsManager = SmsManager.getDefault();
                for (final Message message: messages) {

                    if (message.time.after(timeNow)) {
                        continue;
                    }

                    message.success = 0;
                    message.fails = 0;

                    messageDataSource.removeFromList(scheduleFile, message.key, false,
                            new MessageDataSource.AddReplaceRemoveMessageCallback() {
                                @Override
                                public void onSuccess() {
                                    SendAlarmManager.createAlarm(context);
                                }

                                @Override
                                public void onError() {
                                    notifyError();
                                }
                            });

                    BroadcastReceiver sentReceiver = new BroadcastReceiver() {
                        @Override
                        public void onReceive(final Context context, Intent intent) {

                            if (getResultCode() != Activity.RESULT_OK) {
                                message.fails++;
                            } else {
                                message.success++;
                            }

                            messageDataSource.addOrReplaceInList(historyFile, message, new MessageDataSource.AddReplaceRemoveMessageCallback() {
                                @Override
                                public void onSuccess() { }

                                @Override
                                public void onError() {
                                    notifyError();
                                }
                            });

                            notifyResult(message, context);

                        }
                    };

                    context.getApplicationContext().registerReceiver(sentReceiver,
                            new IntentFilter(INTENT_FILTER_MESSAGE_SENT));

                    try {

                        String text = Utils.readFile(message.textFile);
                        for (Message.Recipient recipient : message.recipients) {

                            ArrayList<String> messageParts = smsManager.divideMessage(text);
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                                    0, new Intent(INTENT_FILTER_MESSAGE_SENT),
                                    PendingIntent.FLAG_UPDATE_CURRENT);
                            ArrayList<PendingIntent> pendingIntents = new ArrayList<>();
                            for (String s : messageParts) {
                                pendingIntents.add(pendingIntent);
                            }

                            smsManager.sendMultipartTextMessage(recipient.number, null,
                                    messageParts, pendingIntents, null);

                        }

                    } catch (Exception e) {
                        notifyError();
                    }

                }

            }

            @Override
            public void onError() {
                notifyError();
            }
        });

    }

    private static void notifyResult(@NonNull Message message, @NonNull Context context) {

        Intent mainActivityIntent = new Intent(context.getApplicationContext(),
                MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(
                context.getApplicationContext());
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(mainActivityIntent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(
                0, PendingIntent.FLAG_UPDATE_CURRENT);

        Resources resources = MyApplication.getInstance().getResources();
        Utils.showNotification(resources.getString(R.string.notification_title_sent),
                resources.getString(R.string.notification_text_sent_success) +
                        Integer.toString(message.success) + "\n" +
                        resources.getString(R.string.notification_text_sent_fails) +
                        Integer.toString(message.fails),
                message.key, false, true, pendingIntent);


    }

    private static void notifyError() {

        Resources resources = MyApplication.getInstance().getResources();
        Utils.showNotification(resources.getString(R.string.notification_title_warning),
                resources.getString(R.string.notification_text_error_sending),
                TAG_NOTIFICATION_ERROR, false, false, null);


    }

}
