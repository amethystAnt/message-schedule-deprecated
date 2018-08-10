package com.patlejch.messageschedule.data;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.patlejch.messageschedule.dagger.components.SingletonComponent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MessageDataSource {

    private static final String DB_SCHEDULE = "schedule.db";
    private static final String DB_HISTORY = "history.db";

    private MessageDatabaseAdapter databaseAdapter;
    private SingletonComponent singletonComponent;

    public enum MessagesListType {
        LIST_SCHEDULE, LIST_HISTORY
    }

    @Inject
    public MessageDataSource(@NonNull MessageDatabaseAdapter databaseAdapter,
                             @NonNull SingletonComponent component) {
        this.databaseAdapter = databaseAdapter;
        singletonComponent = component;
    }

    public File getMessagesDatabaseFile(@NonNull MessagesListType type) {

        String filename = DB_SCHEDULE;
        if (type == MessagesListType.LIST_HISTORY) {
            filename = DB_HISTORY;
        }

        File file = new File(singletonComponent.application().getFilesDir().getAbsolutePath()
                + "/" + filename);
        try {
            file.createNewFile();
        } catch (IOException ignored) { }

        return file;

    }

    public void fetchList(@NonNull File database, MessageListFetchCallback callback) {
        FetchMessageListTask task = new FetchMessageListTask(databaseAdapter, callback);
        task.execute(database);
    }

    public void addOrReplaceInList(@NonNull File database, @NonNull Message message,
                                   AddReplaceRemoveMessageCallback callback) {
        AddOrReplaceMessageTask task = new AddOrReplaceMessageTask(database, databaseAdapter, callback);
        task.execute(message);
    }

    public void removeFromList(@NonNull File database, @NonNull String key,
                               AddReplaceRemoveMessageCallback callback) {
        RemoveMessageTask task = new RemoveMessageTask(database, databaseAdapter, callback);
        task.execute(key);
    }

    public interface MessageListFetchCallback {
        void onMessageListFetched(ArrayList<Message> messages);
        void onError();
    }

    private static class FetchMessageListTask extends AsyncTask<File, Void, ArrayList<Message>> {

        private MessageListFetchCallback preExecCallback;
        private MessageListFetchCallback callback;

        MessageDatabaseAdapter databaseAdapter;

        FetchMessageListTask(@NonNull MessageDatabaseAdapter databaseAdapter,
                             MessageListFetchCallback callback) {
            preExecCallback = callback;
            this.databaseAdapter = databaseAdapter;
        }

        @Override
        protected ArrayList<Message> doInBackground(File... files) {

            callback = preExecCallback;

            if (files == null || files[0] == null) {
                return null;
            }

            try {
                return databaseAdapter.loadMessages(files[0]);
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
        MessageDatabaseAdapter databaseAdapter;

        AddOrReplaceMessageTask(@NonNull File file, @NonNull MessageDatabaseAdapter databaseAdapter,
                                AddReplaceRemoveMessageCallback callback) {
            preExecCallback = callback;
            this.file = file;
            this.databaseAdapter = databaseAdapter;
        }

        @Override
        protected Exception doInBackground(Message... messages) {
            callback = preExecCallback;

            if (messages == null || messages[0] == null) {
                return new NullPointerException();
            }

            try {
                databaseAdapter.addOrReplaceMessage(file, messages[0]);
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
        MessageDatabaseAdapter databaseAdapter;

        RemoveMessageTask(@NonNull File file, @NonNull MessageDatabaseAdapter databaseAdapter,
                          AddReplaceRemoveMessageCallback callback) {
            preExecCallback = callback;
            this.file = file;
            this.databaseAdapter = databaseAdapter;
        }

        @Override
        protected Exception doInBackground(String... strings) {

            callback = preExecCallback;

            if (strings == null || strings[0] == null) {
                return new NullPointerException();
            }

            try {
                databaseAdapter.removeMessage(file, strings[0]);
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
