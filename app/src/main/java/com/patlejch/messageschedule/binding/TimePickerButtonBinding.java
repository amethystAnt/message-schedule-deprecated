package com.patlejch.messageschedule.binding;

import android.databinding.BindingAdapter;

import com.patlejch.messageschedule.view.widgets.TimePickerButton;

public class TimePickerButtonBinding {

    @BindingAdapter("app:hour")
    public static void setHour(TimePickerButton button, int hour) {
        button.setHour(hour);
    }

    @BindingAdapter("app:minute")
    public static void setMinute(TimePickerButton button, int minute) {
        button.setMinute(minute);
    }

}
