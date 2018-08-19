package com.patlejch.messageschedule.data;

import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;

import com.patlejch.messageschedule.app.MyApplication;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ContactsDataSource {

    private MyApplication app;

    @Inject
    public ContactsDataSource(@NonNull MyApplication app) {
        this.app = app;
    }

    public ArrayList<Message.Recipient> getContacts() throws RuntimeException {

        ArrayList<Message.Recipient> recipients = new ArrayList<>();

        Cursor cursor = app.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        if (cursor == null) {
            return recipients;
        }

        while (cursor.moveToNext()) {

            String name = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String number = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            Message.Recipient contact = new Message.Recipient();
            contact.name = name;
            contact.number = number;

            boolean add = true;
            for (Message.Recipient recipient : recipients) {
                if (contact.name.equals(recipient.name)) {
                    add = false;
                    break;
                }
            }

            if (add) {
                recipients.add(contact);
            }

        }

        cursor.close();
        return recipients;

    }

}
