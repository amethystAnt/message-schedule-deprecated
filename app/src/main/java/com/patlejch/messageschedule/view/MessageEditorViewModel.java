package com.patlejch.messageschedule.view;

import android.content.res.Resources;
import android.databinding.BaseObservable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.databinding.ObservableList;
import android.support.annotation.Nullable;

import com.patlejch.messageschedule.R;
import com.patlejch.messageschedule.app.MyApplication;
import com.patlejch.messageschedule.data.Message;
import com.patlejch.messageschedule.data.MessageDataSource;
import com.patlejch.messageschedule.utils.Utils;
import com.pchmn.materialchips.model.Chip;
import com.pchmn.materialchips.model.ChipInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MessageEditorViewModel extends BaseObservable {

    public ObservableInt minute = new ObservableInt();
    public ObservableInt hour = new ObservableInt();
    public ObservableInt day = new ObservableInt();
    public ObservableInt month = new ObservableInt();
    public ObservableInt year = new ObservableInt();
    public ObservableList<ChipInterface> chips = new ObservableArrayList<>();
    public ObservableField<String> messageText = new ObservableField<>();
    public ObservableBoolean sendImmediately = new ObservableBoolean(false);
    public ObservableField<String> toastMessage = new ObservableField<>();
    public ObservableField<List<String>> warningsList = new ObservableField<>();

    private MessageDataSource.MessagesListType listType;
    private Message message;
    private MessageEditorNavigator navigator;
    private Resources resources;

    public MessageEditorViewModel() {
        resources = MyApplication.getInstance().getResources();
    }

    public void start(@Nullable final String messageKey, @Nullable MessageDataSource.MessagesListType listType) {

        this.listType = listType;
        Calendar calendar = Calendar.getInstance();
        setupDateAndTime(calendar);

        if (messageKey != null && listType != null) {

            MessageDataSource dataSource = MessageDataSource.getInstance();
            dataSource.fetchList(dataSource.getMessagesDatabaseFile(listType), new MessageDataSource.MessageListFetchCallback() {
                @Override
                public void onMessageListFetched(ArrayList<Message> messages) {
                    for (Message message : messages) {
                        if (message.key.equals(messageKey)) {
                            MessageEditorViewModel.this.message = message;
                            setupDateAndTime(message.time);
                            messageText.set(message.text);
                            setupChips(message.recipients);
                            return;
                        }
                    }
                }

                @Override
                public void onError() {
                    toastMessage.set(resources.getString(R.string.error_fetching));
                }
            });

        }

    }

    private void setupDateAndTime(Calendar calendar) {

        minute.set(calendar.get(Calendar.MINUTE));
        hour.set(calendar.get(Calendar.HOUR_OF_DAY));

        day.set(calendar.get(Calendar.DAY_OF_MONTH));
        month.set(calendar.get(Calendar.MONTH));
        year.set(calendar.get(Calendar.YEAR));

    }

    private void setupChips(ArrayList<Message.Recipient> recipients) {
        for (Message.Recipient recipient : recipients) {
            Chip chip = new Chip(recipient.number, recipient.name, recipient.number);
            chips.add(chip);
        }
    }

    public void saveAndFinish() {

        ArrayList<Message.Recipient> recipients = new ArrayList<>();
        for (ChipInterface chip : chips) {
            Message.Recipient recipient = new Message.Recipient();
            recipient.number = chip.getInfo();
            recipient.name = chip.getLabel();
            recipients.add(recipient);
        }

        Calendar currentTime = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();

        if (!sendImmediately.get()) {
            calendar.set(Calendar.MINUTE, minute.get());
            calendar.set(Calendar.HOUR_OF_DAY, hour.get());
            calendar.set(Calendar.DAY_OF_MONTH, day.get());
            calendar.set(Calendar.MONTH, month.get());
            calendar.set(Calendar.YEAR, year.get());
        }

        List<String> warnings = new ArrayList<>();

        if (currentTime.after(calendar)) {
            warnings.add(resources.getString(R.string.warning_time_in_past));
        }

        if (messageText.get() == null || messageText.get().isEmpty()) {
            warnings.add(resources.getString(R.string.warning_text_empty));
        }

        if (recipients.isEmpty()) {
            warnings.add(resources.getString(R.string.warning_no_recipients));
        }

        if (!warnings.isEmpty()) {
            warningsList.set(warnings);
            return;
        }

        Message message;
        if (this.message != null) {
            message = this.message;
            message.recipients = recipients;
            message.text = messageText.get();
            message.time = calendar;
        } else {
            message = Message.construct(Utils.createMessageKey(), recipients, messageText.get(),
                    calendar, 0, 0);
        }

        MessageDataSource dataSource = MessageDataSource.getInstance();

        if (listType == MessageDataSource.MessagesListType.LIST_HISTORY) {
            dataSource.removeFromList(dataSource.getMessagesDatabaseFile(MessageDataSource.MessagesListType.LIST_HISTORY),
                    message.key, new MessageDataSource.AddReplaceRemoveMessageCallback() {
                @Override
                public void onSuccess() { }

                @Override
                public void onError() {
                    toastMessage.set(resources.getString(R.string.error_saving_message));
                }
            });
        }

        dataSource.addOrReplaceInList(dataSource.getMessagesDatabaseFile(
                MessageDataSource.MessagesListType.LIST_SCHEDULE), message,
                new MessageDataSource.AddReplaceRemoveMessageCallback() {
            @Override
            public void onSuccess() {
                if (navigator != null) {
                    navigator.onFinish();
                }
            }

            @Override
            public void onError() {
                toastMessage.set(resources.getString(R.string.error_saving_message));
            }
        });

    }

    public void setNavigator(MessageEditorNavigator navigator) {
        this.navigator = navigator;
    }

}
