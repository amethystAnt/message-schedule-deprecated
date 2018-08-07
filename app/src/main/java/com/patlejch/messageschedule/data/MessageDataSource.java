package com.patlejch.messageschedule.data;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.patlejch.messageschedule.app.MyApplication;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MessageDataSource {

    private static MessageDataSource INSTANCE = null;
    private static final String DB_SCHEDULE = "schedule.db";
    private static final String DB_HISTORY = "history.db";

    public enum MessagesListType {
        LIST_SCHEDULE, LIST_HISTORY
    }

    private MessageDataSource() {}

    public static MessageDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MessageDataSource();
        }
        return INSTANCE;
    }

    public File getMessagesDatabaseFile(@NonNull MessagesListType type) {

        String filename = DB_SCHEDULE;
        if (type == MessagesListType.LIST_HISTORY) {
            filename = DB_HISTORY;
        }

        File file = new File(MyApplication.getInstance().getFilesDir().getAbsolutePath()
                + "/" + filename);
        try {
            file.createNewFile();
        } catch (IOException ignored) { }

        return file;

    }

    public void fetchList(@NonNull File database, MessageListFetchCallback callback) {
        FetchMessageListTask task = new FetchMessageListTask(callback);
        task.execute(database);
    }

    public void addOrReplaceInList(@NonNull File database, @NonNull Message message,
                                   AddReplaceRemoveMessageCallback callback) {
        AddOrReplaceMessageTask task = new AddOrReplaceMessageTask(database, callback);
        task.execute(message);
    }

    public void removeFromList(@NonNull File database, @NonNull String key,
                               AddReplaceRemoveMessageCallback callback) {
        RemoveMessageTask task = new RemoveMessageTask(database, callback);
        task.execute(key);
    }

    public interface MessageListFetchCallback {
        void onMessageListFetched(ArrayList<Message> messages);
        void onError();
    }

    private static class FetchMessageListTask extends AsyncTask<File, Void, ArrayList<Message>> {

        private MessageListFetchCallback preExecCallback;
        private MessageListFetchCallback callback;

        FetchMessageListTask(MessageListFetchCallback callback) {
            preExecCallback = callback;
        }

        @Override
        protected ArrayList<Message> doInBackground(File... files) {

            callback = preExecCallback;

            if (files == null || files[0] == null) {
                return null;
            }

            try {
                return MessageData.loadMessages(files[0]);
            } catch (Exception e) {
                return null;
            }

        }

        @Override
        protected void onPostExecute(ArrayList<Message> result) {
            if (callback == null) {
                return;
            }

            if (result != null) {
                callback.onMessageListFetched(result);
            } else {
                callback.onError();
            }
        }
    }

    public interface AddReplaceRemoveMessageCallback {
        void onSuccess();
        void onError();
    }

    private static class AddOrReplaceMessageTask extends AsyncTask<Message, Void, Exception> {

        AddReplaceRemoveMessageCallback callback;
        AddReplaceRemoveMessageCallback preExecCallback;

        File file;

        AddOrReplaceMessageTask(@NonNull File file, AddReplaceRemoveMessageCallback callback) {
            preExecCallback = callback;
            this.file = file;
        }

        @Override
        protected Exception doInBackground(Message... messages) {
            callback = preExecCallback;

            if (messages == null || messages[0] == null) {
                return new NullPointerException();
            }

            try {
                MessageData.addOrReplaceMessage(file, messages[0]);
            } catch (Exception e) {
                return e;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Exception e) {
            if (callback == null) {
                return;
            }

            if (e != null) {
                callback.onError();
            } else {
                callback.onSuccess();
            }
        }
    }

    private static class RemoveMessageTask extends AsyncTask<String, Void, Exception> {

        AddReplaceRemoveMessageCallback callback;
        AddReplaceRemoveMessageCallback preExecCallback;

        File file;

        RemoveMessageTask(@NonNull File file,
                          AddReplaceRemoveMessageCallback callback) {
            preExecCallback = callback;
            this.file = file;
        }

        @Override
        protected Exception doInBackground(String... strings) {

            callback = preExecCallback;

            if (strings == null || strings[0] == null) {
                return new NullPointerException();
            }

            try {
                MessageData.removeMessage(file, strings[0]);
            } catch (Exception e) {
                return e;
            }

            return null;

        }

        @Override
        protected void onPostExecute(Exception e) {
            if (callback == null) {
                return;
            }

            if (e != null) {
                callback.onError();
            } else {
                callback.onSuccess();
            }
        }
    }


}
