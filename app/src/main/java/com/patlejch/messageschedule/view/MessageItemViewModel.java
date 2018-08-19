package com.patlejch.messageschedule.view;

import android.content.res.Resources;
import android.databinding.BaseObservable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.view.View;

import com.patlejch.messageschedule.R;
import com.patlejch.messageschedule.dagger.components.MessageItemComponent;
import com.patlejch.messageschedule.data.Message;
import com.patlejch.messageschedule.utils.Utils;

import java.util.Calendar;

import javax.inject.Inject;

public class MessageItemViewModel extends BaseObservable {

    public final ObservableField<String> to = new ObservableField<>();
    public final ObservableField<String> text = new ObservableField<>();
    public final ObservableField<String> day = new ObservableField<>();
    public final ObservableField<String> month = new ObservableField<>();
    public final ObservableField<String> year = new ObservableField<>();
    public final ObservableField<String> time = new ObservableField<>();
    public final ObservableField<View.OnClickListener> onClick = new ObservableField<>();
    public final ObservableBoolean visible = new ObservableBoolean(true);

    private Message message;
    private Resources resources;

    public MessageItemViewModel(@NonNull MessageItemComponent component) {
        component.inject(this);
        setupFields();
    }

    @Inject
    public void injectMessage(@NonNull Message message) {
        if (this.message == null) {
            this.message = message;
        }
    }

    @Inject
    public void injectOnClickListener(@NonNull View.OnClickListener onClickListener) {
        if (onClick.get() == null) {
            onClick.set(onClickListener);
        }
    }

    @Inject
    public void injectResources(@NonNull Resources resources) {
        if (this.resources == null) {
            this.resources = resources;
        }
    }

    public String getKey() {
        return message.key;
    }

    private void setupFields() {

        String toText = resources.getString(R.string.text_to);
        for (int i = 0; i < message.recipients.size(); i++) {
            Message.Recipient recipient = message.recipients.get(i);
            String s = recipient.number;
            if (recipient.name != null && !recipient.name.isEmpty()) {
                s = recipient.name;
            }

            toText = toText.concat(s);
            if (i != message.recipients.size() - 1) {
                toText = toText.concat("; ");
            }
        }

        to.set(toText);
        String dayText = Integer.toString(message.time.get(Calendar.DAY_OF_MONTH));
        dayText = dayText.length() == 1 ? "0" + dayText : dayText;
        day.set(dayText);
        String monthText = Utils.getMonthName3Letters(message.time.get(Calendar.MONTH), resources);
        month.set(monthText);
        year.set(Integer.toString(message.time.get(Calendar.YEAR)));
        text.set(message.text);

        String hour = Integer.toString(message.time.get(Calendar.HOUR_OF_DAY));
        hour = hour.length() == 1 ? "0" + hour : hour;
        String minute = Integer.toString(message.time.get(Calendar.MINUTE));
        minute = minute.length() == 1 ? "0" + minute : minute;
        String timeText = hour + ":" + minute;
        time.set(timeText);

    }

}
