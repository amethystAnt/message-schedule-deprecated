package com.patlejch.messageschedule.binding;

import android.databinding.BindingAdapter;

import com.patlejch.messageschedule.view.widgets.DatePickerButton;

public class DatePickerButtonBinding {

    @BindingAdapter("app:day")
    public static void setDay(DatePickerButton button, int day) {
        button.setDay(day);
    }

    @BindingAdapter("app:month")
    public static void setMonth(DatePickerButton button, int month) {
        button.setMonth(month);
    }

    @BindingAdapter("app:year")
    public static void setYear(DatePickerButton button, int year) {
        button.setYear(year);
    }

}
