package com.patlejch.messageschedule.view;

import android.content.res.Resources;
import android.databinding.BaseObservable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.view.View;

import com.patlejch.messageschedule.R;
import com.patlejch.messageschedule.app.MyApplication;
import com.patlejch.messageschedule.data.Message;
import com.patlejch.messageschedule.utils.Utils;

import java.io.IOException;
import java.util.Calendar;

public class MessageItemViewModel extends BaseObservable {

    public final ObservableField<String> to = new ObservableField<>();
    public final ObservableField<String> text = new ObservableField<>();
    public final ObservableField<String> day = new ObservableField<>();
    public final ObservableField<String> month = new ObservableField<>();
    public final ObservableField<String> year = new ObservableField<>();
    public final ObservableField<String> time = new ObservableField<>();
    public final ObservableField<View.OnClickListener> onClick = new ObservableField<>();
    public final ObservableBoolean visible = new ObservableBoolean(true);

    private final Message message;
    private Resources resources = MyApplication.getInstance().getResources();

    public MessageItemViewModel(Message message, View.OnClickListener onClickListener) {
        this.message = message;
        onClick.set(onClickListener);
        setupFields(message);
    }

    public String getKey() {
        return message.key;
    }

    private void setupFields(Message message) {

        String toText = resources.getText(R.string.text_to).toString();
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
        try {
            text.set(Utils.readFile(message.textFile));
        } catch (IOException e) {
            text.set("");
        }

        String hour = Integer.toString(message.time.get(Calendar.HOUR_OF_DAY));
        hour = hour.length() == 1 ? "0" + hour : hour;
        String minute = Integer.toString(message.time.get(Calendar.MINUTE));
        minute = minute.length() == 1 ? "0" + minute : minute;
        String timeText = hour + ":" + minute;
        time.set(timeText);

    }

}
