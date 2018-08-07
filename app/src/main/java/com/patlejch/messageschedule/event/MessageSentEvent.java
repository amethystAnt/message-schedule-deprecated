package com.patlejch.messageschedule.event;

import com.patlejch.messageschedule.data.Message;

public class MessageSentEvent {

    public Message message;

    public MessageSentEvent(Message message) {
        this.message = message;
    }

}
