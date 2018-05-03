package com.patlejch.messageschedule.data;

import com.patlejch.messageschedule.app.MyApplication;
import com.patlejch.messageschedule.utils.Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class Message {

    public String key;
    public ArrayList<Recipient> recipients;
    public File textFile;
    public Calendar time;
    public int success = 0;
    public int fails = 0;

    public static Message construct(ArrayList<Recipient> recipients,
                                       String text, Calendar time, int success, int fails) throws IOException {

        Message message = new Message();

        File textFile;
        String key;
        do {
            key = Utils.createMessageKey();
            textFile = new File(MyApplication.getInstance().getFilesDir().getAbsolutePath()
                    + "/" + key);
        } while (!textFile.createNewFile());

        BufferedWriter writer = new BufferedWriter(new FileWriter(textFile));
        writer.write(text);
        writer.close();

        message.key = key;
        message.recipients = recipients;
        message.textFile = textFile;
        message.time = time;
        message.success = success;
        message.fails = fails;

        return message;

    }

    public void setText(String text) throws IOException {

        if (textFile == null) {
            textFile = new File(MyApplication.getInstance().getFilesDir().getAbsolutePath()
                    + "/" + key);
            textFile.createNewFile();
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(textFile));
        writer.write(text);
        writer.close();

    }

    public static class Recipient {
        public String number;
        public String name;
    }

}
