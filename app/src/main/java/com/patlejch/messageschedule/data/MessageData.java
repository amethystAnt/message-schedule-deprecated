package com.patlejch.messageschedule.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MessageData {

    public static final String MESSAGES_TABLE_NAME = "messages";
    public static final String MESSAGE_KEY_KEY = "msgkey";
    public static final String MESSAGE_RECIPIENTS_KEY = "recipients_table";
    public static final String MESSAGE_TEXT_KEY = "msgtext";
    public static final String MESSAGE_TIME_KEY = "time";
    public static final String MESSAGE_SUCCESS_KEY = "success";
    public static final String MESSAGE_FAILS_KEY = "fails";
    public static final String RECIPIENT_NUMBER_KEY = "phone_number";
    public static final String RECIPIENT_NAME_KEY = "contact_name";

    public static final String MESSAGES_TABLE = "create table if not exists " + MESSAGES_TABLE_NAME +
            " (" + MESSAGE_KEY_KEY + " text unique, " +
            MESSAGE_RECIPIENTS_KEY + " text not null, " +
            MESSAGE_TEXT_KEY + " text not null, " +
            MESSAGE_TIME_KEY + " varchar(30) not null, " +
            MESSAGE_SUCCESS_KEY + " int, " +
            MESSAGE_FAILS_KEY + " int)";

    public static ArrayList<Message> loadMessages(@NonNull File file)
            throws FileNotFoundException, SQLiteException {

        if (!file.exists()) {
            throw new FileNotFoundException();
        }

        SQLiteDatabase database = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null,
                SQLiteDatabase.OPEN_READWRITE);

        ArrayList<Message> list = new ArrayList<>();

        database.execSQL(MESSAGES_TABLE);
        Cursor cursor = database.query(MESSAGES_TABLE_NAME, null, null, null, null, null, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {

            String key = cursor.getString(cursor.getColumnIndex(MESSAGE_KEY_KEY));

            String recipientsTable = cursor.getString(cursor.getColumnIndex(MESSAGE_RECIPIENTS_KEY));
            database.execSQL(createRecipientsTable(recipientsTable));

            ArrayList<Message.Recipient> recipients = new ArrayList<>();
            try {

                Cursor recipientsCursor = database.query(recipientsTable, null, null, null, null, null, null);

                recipientsCursor.moveToFirst();
                for (int j = 0; j < recipientsCursor.getCount(); j++) {

                    Message.Recipient recipient = new Message.Recipient();
                    recipient.number = recipientsCursor.getString(recipientsCursor.getColumnIndex(RECIPIENT_NUMBER_KEY));
                    recipient.name = recipientsCursor.getString(recipientsCursor.getColumnIndex(RECIPIENT_NAME_KEY));
                    recipients.add(recipient);
                    recipientsCursor.moveToNext();

                }

                recipientsCursor.close();


            } catch (SQLiteException e) {
                continue;
            }

            String text = cursor.getString(cursor.getColumnIndex(MESSAGE_TEXT_KEY));

            String time = cursor.getString(cursor.getColumnIndex(MESSAGE_TIME_KEY));
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, Integer.parseInt(time.substring(0, 4)));
            calendar.set(Calendar.MONTH, Integer.parseInt(time.substring(4, 6)));
            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(time.substring(6, 8)));
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time.substring(8, 10)));
            calendar.set(Calendar.MINUTE, Integer.parseInt(time.substring(10, 12)));

            int success = cursor.getInt(cursor.getColumnIndex(MESSAGE_SUCCESS_KEY));
            int fails = cursor.getInt(cursor.getColumnIndex(MESSAGE_FAILS_KEY));

            list.add(Message.construct(key, recipients, text, calendar, success, fails));
            cursor.moveToNext();

        }

        cursor.close();
        database.close();
        return list;

    }

    public static void addOrReplaceMessage(@NonNull File file, @NonNull Message message)
            throws FileNotFoundException, SQLiteException {

        if (!file.exists()) {
            throw new FileNotFoundException();
        }

        removeMessage(file, message.key);

        SQLiteDatabase database = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null,
                SQLiteDatabase.OPEN_READWRITE);
        database.execSQL(MESSAGES_TABLE);

        String recipientsTable = message.key;
        String query = createRecipientsTable(recipientsTable);
        database.execSQL(query);

        for (Message.Recipient recipient : message.recipients) {

            ContentValues recipientRow = new ContentValues();
            recipientRow.put(RECIPIENT_NUMBER_KEY, recipient.number);
            recipientRow.put(RECIPIENT_NAME_KEY, recipient.name);
            database.insert(recipientsTable, null, recipientRow);

        }

        Calendar calendar = message.time;
        String format = "%02d";
        String time = Integer.toString(calendar.get(Calendar.YEAR)) +
                String.format(Locale.US, format, calendar.get(Calendar.MONTH)) +
                String.format(Locale.US, format, calendar.get(Calendar.DAY_OF_MONTH)) +
                String.format(Locale.US, format, calendar.get(Calendar.HOUR_OF_DAY)) +
                String.format(Locale.US, format, calendar.get(Calendar.MINUTE));

        ContentValues values = new ContentValues();
        values.put(MESSAGE_RECIPIENTS_KEY, recipientsTable);
        values.put(MESSAGE_KEY_KEY, message.key);
        values.put(MESSAGE_TEXT_KEY, message.text);
        values.put(MESSAGE_TIME_KEY, time);
        values.put(MESSAGE_SUCCESS_KEY, message.success);
        values.put(MESSAGE_FAILS_KEY, message.fails);
        database.insert(MESSAGES_TABLE_NAME, null, values);

        database.close();

    }

    public static void removeMessage(@NonNull File file, @NonNull String key)
            throws FileNotFoundException, SQLiteException {

        if (!file.exists()) {
            throw new FileNotFoundException();
        }

        SQLiteDatabase database = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null,
                SQLiteDatabase.OPEN_READWRITE);
        database.execSQL(MESSAGES_TABLE);

        String where = MESSAGE_KEY_KEY + "=\"" + key + "\"";
        String query = "select * from " + MESSAGES_TABLE_NAME +
                " where " + where;
        Cursor cursor = database.rawQuery(query, null);
        cursor.moveToFirst();

        for (int i = 0; i < cursor.getCount(); i++) {

            String recipientsTable = cursor.getString(cursor.getColumnIndex(MESSAGE_RECIPIENTS_KEY));
            query = "drop table if exists " + recipientsTable;
            database.execSQL(query);
            cursor.moveToNext();

        }

        cursor.close();

        database.delete(MESSAGES_TABLE_NAME, where, null);
        database.close();

    }

    private static String createRecipientsTable(String tableName) {
        return "create table if not exists " + tableName + " (" +
                RECIPIENT_NUMBER_KEY + " varchar(20) unique not null, " +
                RECIPIENT_NAME_KEY + " text)";
    }

}
