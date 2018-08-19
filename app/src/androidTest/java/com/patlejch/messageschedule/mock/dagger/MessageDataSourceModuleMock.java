package com.patlejch.messageschedule.mock.dagger;

import android.support.annotation.NonNull;

import com.patlejch.messageschedule.data.MessageDataSource;

import dagger.Module;
import dagger.Provides;

@Module
public class MessageDataSourceModuleMock {

    public MessageDataSource dataSource;

    public MessageDataSourceModuleMock(@NonNull MessageDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Provides
    public MessageDataSource provideDataSource() {
        return dataSource;
    }

}
