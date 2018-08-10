package com.patlejch.messageschedule.dagger.components;

import com.patlejch.messageschedule.dagger.scopes.FragmentScope;
import com.patlejch.messageschedule.view.MessageEditorViewModel;

import dagger.Component;

@Component(dependencies = SingletonComponent.class)
@FragmentScope
public interface MessageEditorComponent {
    void inject(MessageEditorViewModel viewModel);
}
