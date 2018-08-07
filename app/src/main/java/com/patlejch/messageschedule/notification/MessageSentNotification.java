package com.patlejch.messageschedule.notification;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;

import com.patlejch.messageschedule.R;
import com.patlejch.messageschedule.app.MyApplication;
import com.patlejch.messageschedule.data.Message;
import com.patlejch.messageschedule.event.MessageSentEvent;
import com.patlejch.messageschedule.event.SendMessageErrorEvent;
import com.patlejch.messageschedule.utils.Utils;
import com.patlejch.messageschedule.view.MainActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class MessageSentNotification {

    private static final String TAG_NOTIFICATION_ERROR = "TAG_NOTIFICATION_ERROR";

    public MessageSentNotification() {
        start();
    }

    public void start() {
        EventBus.getDefault().register(this);
    }

    public void stop() {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void notifyResult(@NonNull MessageSentEvent event) {

        Message message = event.message;

        Context context = MyApplication.getInstance();
        Intent mainActivityIntent = new Intent(context, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
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

    @Subscribe
    public void notifyError(@NonNull SendMessageErrorEvent event) {

        Resources resources = MyApplication.getInstance().getResources();
        Utils.showNotification(resources.getString(R.string.notification_title_warning), event.error,
                TAG_NOTIFICATION_ERROR, false, false, null);

    }

}
