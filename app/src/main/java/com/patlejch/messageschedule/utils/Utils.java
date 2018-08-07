package com.patlejch.messageschedule.utils;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.patlejch.messageschedule.R;
import com.patlejch.messageschedule.app.MyApplication;
import com.patlejch.messageschedule.data.Message;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class Utils {

    private static final String NOTIFICATION_CHANNEL_ID = "notif_ch";
    private static final int NOTIFICATION_ID_DEFAULT = 0;

    public static String readFile(@NonNull File file) throws IOException {

        String text = "";
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = reader.readLine();
        while (line != null) {
            text = text + line;
            line = reader.readLine();
        }

        return text;

    }

    public static void showNotification(@NonNull String title, @NonNull String text,
                                        @NonNull String tag, boolean ongoing, boolean autoCancel,
                                        PendingIntent intent) {

        Context context = MyApplication.getInstance();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(
                Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.ic_notification);
        builder.setContentTitle(title);
        builder.setContentText(text);
        builder.setOngoing(ongoing);
        builder.setAutoCancel(autoCancel);
        builder.setContentIntent(intent);
        builder.setPriority(NotificationCompat.PRIORITY_LOW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "Default channel",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setImportance(NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(NOTIFICATION_CHANNEL_ID);
        }

        if (notificationManager != null) {
            notificationManager.notify(tag, NOTIFICATION_ID_DEFAULT, builder.build());
        }

    }

    public static void cancelNotification(@NonNull String tag) {

        Context context = MyApplication.getInstance();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(
                Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancel(tag, NOTIFICATION_ID_DEFAULT);
        }

    }

    public static void addFragmentToManager(Fragment fragment,
                                            @NonNull FragmentManager manager,
                                            String tag) {
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(fragment, tag);
        transaction.commit();
    }

    public static String getMonthName3Letters(int month, @NonNull Resources resources) {

        switch (month) {
            case Calendar.JANUARY:
                return resources.getString(R.string.month_jan);
            case Calendar.FEBRUARY:
                return resources.getString(R.string.month_feb);
            case Calendar.MARCH:
                return resources.getString(R.string.month_mar);
            case Calendar.APRIL:
                return resources.getString(R.string.month_apr);
            case Calendar.MAY:
                return resources.getString(R.string.month_may);
            case Calendar.JUNE:
                return resources.getString(R.string.month_jun);
            case Calendar.JULY:
                return resources.getString(R.string.month_jul);
            case Calendar.AUGUST:
                return resources.getString(R.string.month_aug);
            case Calendar.SEPTEMBER:
                return resources.getString(R.string.month_sep);
            case Calendar.OCTOBER:
                return resources.getString(R.string.month_oct);
            case Calendar.NOVEMBER:
                return resources.getString(R.string.month_nov);
            case Calendar.DECEMBER:
                return resources.getString(R.string.month_dec);
            default:
                return "";

        }

    }

    public static void sortMessageList(@NonNull ArrayList<Message> messages) {
        Collections.sort(messages, new Comparator<Message>() {
            @Override
            public int compare(Message o1, Message o2) {

                Calendar calendar1 = o1.time;
                Calendar calendar2 = o2.time;

                if (calendar1.before(calendar2)) {
                    return -1;
                } else if (calendar1.after(calendar2)) {
                    return 1;
                }

                return 0;

            }
        });
    }

    public static void askForPermissions(@NonNull Activity activity) {

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(activity, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.READ_CONTACTS,
                            Manifest.permission.SEND_SMS,
                            Manifest.permission.READ_SMS},0);
        }

    }

    public static String createMessageKey() {

        String key = "m" + UUID.randomUUID().toString();
        //remove all special characters, so that the key can be used as a table name
        key = key.replaceAll("[^a-zA-Z0-9]+", "0");
        return key;

    }

}
