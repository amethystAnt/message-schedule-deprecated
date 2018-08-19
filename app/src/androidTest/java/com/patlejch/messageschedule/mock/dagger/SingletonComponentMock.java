package com.patlejch.messageschedule.mock.dagger;

import com.patlejch.messageschedule.dagger.components.SingletonComponent;
import com.patlejch.messageschedule.dagger.modules.AppModule;
import com.patlejch.messageschedule.data.MessageDataSource;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = {MessageDataSourceModuleMock.class, AppModule.class})
@Singleton
public interface SingletonComponentMock extends SingletonComponent {
    @Override
    MessageDataSource messageDataSource();
}
