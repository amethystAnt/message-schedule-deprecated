package com.patlejch.messageschedule.dagger.modules;

import com.patlejch.messageschedule.data.MessageDataSource;

import dagger.Module;
import dagger.Provides;

@Module
public class MessagesModule {

    private MessageDataSource.MessagesListType type;

    public MessagesModule(MessageDataSource.MessagesListType type) {
        this.type = type;
    }

    @Provides
    public MessageDataSource.MessagesListType provideType() {
        return type;
    }

}
