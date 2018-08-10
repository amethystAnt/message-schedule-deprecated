package com.patlejch.messageschedule.dagger.components;

import android.content.res.Resources;

import com.patlejch.messageschedule.app.MyApplication;
import com.patlejch.messageschedule.dagger.modules.AppModule;
import com.patlejch.messageschedule.data.ContactsDataSource;
import com.patlejch.messageschedule.data.MessageDataSource;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = AppModule.class)
@Singleton
public interface SingletonComponent {
    MessageDataSource messageDataSource();
    ContactsDataSource contactsDataSource();
    MyApplication application();
    Resources resources();
}
