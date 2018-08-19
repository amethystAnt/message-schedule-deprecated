package com.patlejch.messageschedule.sms;

import com.patlejch.messageschedule.dagger.components.SingletonComponent;
import com.patlejch.messageschedule.data.Message;
import com.patlejch.messageschedule.data.MessageDataSource;
import com.patlejch.messageschedule.mock.dagger.DaggerSingletonComponentMock;
import com.patlejch.messageschedule.mock.dagger.MessageDataSourceModuleMock;
import com.patlejch.messageschedule.utils.Utils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

@RunWith(MockitoJUnitRunner.class)
public class MessageSenderTest {

    private final static String NUMBER = "789462321";
    private final static String NAME = "Pedro P.";
    private final static String TEXT = "Hello, man!";

    @Mock
    public MessageDataSource messageDataSource;

    @Mock
    public MessageSender.SmsManagerTestableWrapper smsManager;

    @Mock
    public File mockFile;

    @Before

    public void init() {

        Message.Recipient recipient = new Message.Recipient();
        recipient.number = NUMBER;
        recipient.name = NAME;
        ArrayList<Message.Recipient> recipients = new ArrayList<>();
        recipients.add(recipient);

        Message message = Message.construct(Utils.createMessageKey(), recipients, TEXT,
                Calendar.getInstance(), 0, 0);

        final ArrayList<Message> messages = new ArrayList<>();
        messages.add(message);

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                MessageDataSource.MessageListFetchCallback callback =
                        invocation.getArgumentAt(1, MessageDataSource.MessageListFetchCallback.class);
                callback.onMessageListFetched(messages);
                return null;
            }
        }).when(messageDataSource).fetchList(Mockito.any(File.class),
                Mockito.any(MessageDataSource.MessageListFetchCallback.class));

        Mockito.when(messageDataSource.getMessagesDatabaseFile(Mockito.any(MessageDataSource.MessagesListType.class)))
                .thenReturn(mockFile);

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ArrayList<String> ret = new ArrayList<>();
                String s = (String) invocation.getArguments()[0];
                ret.add(s);
                return ret;
            }
        }).when(smsManager).divideMessage(Mockito.anyString());

    }

    @Test
    public void sendMessages_callsSendWithValidData() throws Exception {

        Mockito.doNothing().when(messageDataSource).removeFromList(Mockito.any(File.class),
                Mockito.anyString(), Mockito.any(MessageDataSource.AddReplaceRemoveMessageCallback.class));

        Mockito.doNothing().when(messageDataSource).addOrReplaceInList(Mockito.any(File.class),
                Mockito.any(Message.class),
                Mockito.any(MessageDataSource.AddReplaceRemoveMessageCallback.class));

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {

                try {
                    String number = (String) invocation.getArguments()[0];
                    Assert.assertTrue(number.equals(NUMBER));
                    ArrayList<String> msgParts = (ArrayList) invocation.getArguments()[1];
                    Assert.assertTrue(msgParts.get(0).equals(TEXT));
                } catch (Exception e) {
                    Assert.fail();
                }

                return null;
            }
        }).when(smsManager).sendMultipartTextMessage(Mockito.anyString(), Mockito.any(ArrayList.class),
                Mockito.any(ArrayList.class));

        MessageDataSourceModuleMock module = new MessageDataSourceModuleMock(messageDataSource);
        SingletonComponent component = DaggerSingletonComponentMock
                .builder()
                .messageDataSourceModuleMock(module)
                .build();

        MessageSender.sendMessages(component, smsManager);

    }

}
