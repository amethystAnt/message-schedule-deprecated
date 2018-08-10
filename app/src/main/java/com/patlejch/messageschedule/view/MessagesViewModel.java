package com.patlejch.messageschedule.view;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.databinding.BaseObservable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableList;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.patlejch.messageschedule.R;
import com.patlejch.messageschedule.app.MyApplication;
import com.patlejch.messageschedule.dagger.components.DaggerMessageItemComponent;
import com.patlejch.messageschedule.dagger.components.MessageItemComponent;
import com.patlejch.messageschedule.dagger.components.MessagesComponent;
import com.patlejch.messageschedule.dagger.components.SingletonComponent;
import com.patlejch.messageschedule.dagger.modules.MessageItemModule;
import com.patlejch.messageschedule.data.Message;
import com.patlejch.messageschedule.data.MessageDataSource;
import com.patlejch.messageschedule.fileobserver.FixedFileObserver;
import com.patlejch.messageschedule.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static android.os.FileObserver.MODIFY;

public class MessagesViewModel extends BaseObservable {

    public final ObservableField<ScheduleRecyclerAdapter> scheduleAdapter = new ObservableField<>();
    public final ObservableBoolean empty = new ObservableBoolean(false);
    public final ObservableField<String> noMessageText = new ObservableField<>();
    public final ObservableField<Drawable> noMessageImage = new ObservableField<>();
    public final ObservableArrayList<MessageItemViewModel> messageItemViewModels = new ObservableArrayList<>();
    public final ObservableField<ItemTouchHelper.SimpleCallback> touchHelper = new ObservableField<>();
    public final ObservableField<AlertDialogCallbacks> onClickRemoveDialog = new ObservableField<>();
    public final ObservableField<String> toastMessage = new ObservableField<>();

    private MessageDataSource dataSource;
    private MessageDataSource.MessagesListType type;
    private ScheduleFileObserver fileObserver;
    private MessagesNavigator navigator;
    private Resources resources;
    private SingletonComponent singletonComponent;

    public MessagesViewModel(@NonNull MessagesComponent component) {
        component.inject(this);
        fileObserver = new ScheduleFileObserver(dataSource.getMessagesDatabaseFile(type).getAbsolutePath());
        fileObserver.startWatching();
        resources = MyApplication.getInstance().getResources();
        setup();
    }

    @Inject
    public void injectDataSource(@NonNull MessageDataSource dataSource) {
        if (this.dataSource == null) {
            this.dataSource = dataSource;
        }
    }

    @Inject
    public void injectMessagesType(@NonNull MessageDataSource.MessagesListType listType) {
        if (type == null) {
            type = listType;
        }
    }

    @Inject
    public void injectSingletonComponent(@NonNull SingletonComponent component) {
        singletonComponent = component;
    }

    private void setup() {

        refresh();

        noMessageText.set(resources.getString(type == MessageDataSource.MessagesListType.LIST_SCHEDULE
                ? R.string.text_no_messages_schedule : R.string.text_no_messages_history));
        noMessageImage.set(resources.getDrawable(type == MessageDataSource.MessagesListType.LIST_SCHEDULE
                ? R.drawable.ic_query_builder_white_48dp : R.drawable.ic_history_white_48dp));

        scheduleAdapter.set(new ScheduleRecyclerAdapter(MyApplication.getInstance(), new ArrayList<MessageItemViewModel>()));
        messageItemViewModels.addOnListChangedCallback(new ObservableList.OnListChangedCallback<ObservableList<MessageItemViewModel>>() {
            @Override
            public void onChanged(ObservableList<MessageItemViewModel> sender) { onAnyChange(sender); }

            @Override
            public void onItemRangeChanged(ObservableList<MessageItemViewModel> sender, int positionStart, int itemCount) { onAnyChange(sender); }

            @Override
            public void onItemRangeInserted(ObservableList<MessageItemViewModel> sender, int positionStart, int itemCount) { onAnyChange(sender); }

            @Override
            public void onItemRangeMoved(ObservableList<MessageItemViewModel> sender, int fromPosition, int toPosition, int itemCount) { onAnyChange(sender); }

            @Override
            public void onItemRangeRemoved(ObservableList<MessageItemViewModel> sender, int positionStart, int itemCount) { onAnyChange(sender); }

            private void onAnyChange(List<MessageItemViewModel> list) {
                ScheduleRecyclerAdapter adapter = scheduleAdapter.get();
                if (adapter != null) {
                    adapter.setList(list);
                }
            }
        });

        touchHelper.set(new ItemTouchHelper.SimpleCallback(0,
                type == MessageDataSource.MessagesListType.LIST_SCHEDULE
                        ? ItemTouchHelper.RIGHT : ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                ScheduleRecyclerAdapter.MessageViewHolder messageViewHolder =
                        (ScheduleRecyclerAdapter.MessageViewHolder) viewHolder;
                final MessageItemViewModel viewModel = messageViewHolder.binding.getViewmodel();
                if (viewModel == null) {
                    return;
                }

                viewModel.visible.set(false);
                DialogInterface.OnClickListener positive = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        viewModel.visible.set(true);
                        dataSource.removeFromList(dataSource.getMessagesDatabaseFile(type),
                                viewModel.getKey(),
                                new MessageDataSource.AddReplaceRemoveMessageCallback() {
                                    @Override
                                    public void onSuccess() { }

                                    @Override
                                    public void onError() {
                                        toastMessage.set(resources.getString(R.string.error_removing_message));
                                    }
                                });
                    }
                };

                DialogInterface.OnClickListener negative = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        viewModel.visible.set(true);
                        scheduleAdapter.notifyChange();
                    }
                };

                AlertDialogCallbacks alertDialogCallbacks = new AlertDialogCallbacks();
                alertDialogCallbacks.positive = positive;
                alertDialogCallbacks.negative = negative;
                onClickRemoveDialog.set(alertDialogCallbacks);

            }
        });

    }

    public void refresh() {

        dataSource.fetchList(dataSource.getMessagesDatabaseFile(type),
                new MessageDataSource.MessageListFetchCallback() {
            @Override
            public void onMessageListFetched(ArrayList<Message> messages) {

                Utils.sortMessageList(messages);
                empty.set(messages.isEmpty());
                messageItemViewModels.clear();

                for (final Message message : messages) {

                    MessageItemModule module = new MessageItemModule(message, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (navigator != null) {
                                navigator.onMessageEdit(message.key, type);
                            }

                        }
                    });

                    MessageItemComponent component = DaggerMessageItemComponent
                            .builder()
                            .singletonComponent(singletonComponent)
                            .messageItemModule(module)
                            .build();
                    MessageItemViewModel viewModel = new MessageItemViewModel(component);
                    messageItemViewModels.add(viewModel);
                }

            }

            @Override
            public void onError() {
                toastMessage.set(resources.getString(R.string.error_fetching));
            }
        });

    }

    public void setNavigator(MessagesNavigator navigator) {
        this.navigator = navigator;
    }

    private class ScheduleFileObserver extends FixedFileObserver {

        ScheduleFileObserver(@NonNull String path) {
            super(path, MODIFY);
        }

        @Override
        public void onEvent(int event, @Nullable String path) {
            if (event != MODIFY) {
                return;
            }
            refresh();
        }
    }

    public static class AlertDialogCallbacks {
        public DialogInterface.OnClickListener positive;
        public DialogInterface.OnClickListener negative;
    }

}
