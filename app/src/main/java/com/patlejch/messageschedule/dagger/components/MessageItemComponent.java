package com.patlejch.messageschedule.dagger.components;

import com.patlejch.messageschedule.dagger.modules.MessageItemModule;
import com.patlejch.messageschedule.dagger.scopes.ItemScope;
import com.patlejch.messageschedule.view.MessageItemViewModel;

import dagger.Component;

@Component(modules = MessageItemModule.class, dependencies = SingletonComponent.class)
@ItemScope
public interface MessageItemComponent {
    void inject(MessageItemViewModel viewModel);
}
