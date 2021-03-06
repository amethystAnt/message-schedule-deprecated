package com.patlejch.messageschedule.sms;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;

import com.patlejch.messageschedule.R;
import com.patlejch.messageschedule.alarm.SendAlarmManager;
import com.patlejch.messageschedule.dagger.components.SingletonComponent;
import com.patlejch.messageschedule.data.Message;
import com.patlejch.messageschedule.data.MessageDataSource;
import com.patlejch.messageschedule.event.MessageSentEvent;
import com.patlejch.messageschedule.event.SendMessageErrorEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import static com.patlejch.messageschedule.data.MessageDataSource.MessagesListType.LIST_HISTORY;
import static com.patlejch.messageschedule.data.MessageDataSource.MessagesListType.LIST_SCHEDULE;

public class MessageSender {

    private static final String INTENT_FILTER_MESSAGE_SENT = "INTENT_FILTER_MESSAGE_SENT";

    public static void sendMessages(@NonNull final SingletonComponent singletonComponent,
                                    @NonNull final SmsManagerTestableWrapper smsManager) {

        final MessageDataSource messageDataSource = singletonComponent.messageDataSource();
        final File scheduleFile = messageDataSource.getMessagesDatabaseFile(LIST_SCHEDULE);
        final File historyFile = messageDataSource.getMessagesDatabaseFile(LIST_HISTORY);

        messageDataSource.fetchList(scheduleFile, new MessageDataSource.MessageListFetchCallback() {
            @Override
            public void onMessageListFetched(ArrayList<Message> messages) {

                //the calendar representing current time must be created AFTER the message's calendar
                //for this reason we're not keeping a calendar object, but instead calling Calendar.getInstance() on this line
                Calendar timeNow = Calendar.getInstance();

                for (final Message message: messages) {

                    if (message.time.after(timeNow)) {
                        continue;
                    }

                    message.success = 0;
                    message.fails = 0;

                    messageDataSource.removeFromList(scheduleFile, message.key,
                            new MessageDataSource.AddReplaceRemoveMessageCallback() {
                                @Override
                                public void onSuccess() {
                                    SendAlarmManager.createAlarm(singletonComponent.application(),
                                            singletonComponent);
                                }

                                @Override
                                public void onError() {
                                    error(singletonComponent);
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
                                    error(singletonComponent);
                                }
                            });

                            EventBus.getDefault().post(new MessageSentEvent(message));

                        }
                    };

                    singletonComponent.application().registerReceiver(sentReceiver,
                            new IntentFilter(INTENT_FILTER_MESSAGE_SENT));

                    try {

                        String text = message.text;
                        for (Message.Recipient recipient : message.recipients) {

                            ArrayList<String> messageParts = smsManager.divideMessage(text);
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(singletonComponent.application(),
                                    0, new Intent(INTENT_FILTER_MESSAGE_SENT),
                                    PendingIntent.FLAG_UPDATE_CURRENT);
                            ArrayList<PendingIntent> pendingIntents = new ArrayList<>();
                            for (String s : messageParts) {
                                pendingIntents.add(pendingIntent);
                            }

                            smsManager.sendMultipartTextMessage(recipient.number, messageParts,
                                    pendingIntents);

                        }

                    } catch (Exception e) {
                        error(singletonComponent);
                    }

                }

            }

            @Override
            public void onError() {
                error(singletonComponent);
            }
        });

    }

    private static void error(@NonNull SingletonComponent component) {
        Resources resources = component.resources();
        EventBus.getDefault().post(new SendMessageErrorEvent(resources.getString(R.string.notification_text_error_sending)));
    }

    public static class SmsManagerTestableWrapper {

        private SmsManager smsManager;

        public SmsManagerTestableWrapper(@NonNull SmsManager smsManager) {
            this.smsManager = smsManager;
        }

        public void sendMultipartTextMessage(@NonNull String number, @NonNull ArrayList<String> parts,
                                        @Nullable ArrayList<PendingIntent> sentIntents) {
            smsManager.sendMultipartTextMessage(number, null, parts, sentIntents, null);
        }

        public ArrayList<String> divideMessage(@NonNull String text) {
            return smsManager.divideMessage(text);
        }

    }

}
