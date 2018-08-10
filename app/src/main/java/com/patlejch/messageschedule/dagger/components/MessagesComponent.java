package com.patlejch.messageschedule.dagger.components;

import com.patlejch.messageschedule.dagger.modules.MessagesModule;
import com.patlejch.messageschedule.dagger.scopes.FragmentScope;
import com.patlejch.messageschedule.view.MessagesViewModel;

import dagger.Component;

@Component(modules = MessagesModule.class, dependencies = SingletonComponent.class)
@FragmentScope
public interface MessagesComponent {
    void inject(MessagesViewModel viewModel);
}
