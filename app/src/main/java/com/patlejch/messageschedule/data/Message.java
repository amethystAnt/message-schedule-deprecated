package com.patlejch.messageschedule.data;

import java.util.ArrayList;
import java.util.Calendar;

public class Message {

    public String key;
    public ArrayList<Recipient> recipients;
    public String text;
    public Calendar time;
    public int success = 0;
    public int fails = 0;

    private Message() { }

    public static Message construct(String key, ArrayList<Recipient> recipients,
                                    String text, Calendar time, int success, int fails) {

        Message message = new Message();

        message.key = key;
        message.recipients = recipients;
        message.text = text;
        message.time = time;
        message.success = success;
        message.fails = fails;

        return message;

    }

    public static class Recipient {
        public String number;
        public String name;
    }

}
