package com.patlejch.messageschedule.view;

import com.patlejch.messageschedule.data.MessageDataSource;

public interface MessagesNavigator {

    void onMessageEdit(String key, MessageDataSource.MessagesListType listType);

}
