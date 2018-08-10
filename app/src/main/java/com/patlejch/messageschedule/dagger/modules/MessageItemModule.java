package com.patlejch.messageschedule.dagger.modules;

import android.support.annotation.NonNull;
import android.view.View;

import com.patlejch.messageschedule.dagger.scopes.ItemScope;
import com.patlejch.messageschedule.data.Message;

import dagger.Module;
import dagger.Provides;

@Module
public class MessageItemModule {

    private final Message message;
    private final View.OnClickListener onClickListener;

    public MessageItemModule(@NonNull Message message, @NonNull View.OnClickListener listener) {
        this.message = message;
        this.onClickListener = listener;
    }

    @Provides
    @ItemScope
    public Message provideMessage() {
        return message;
    }

    @Provides
    @ItemScope
    public View.OnClickListener provideOnClickListener() {
        return onClickListener;
    }

}
