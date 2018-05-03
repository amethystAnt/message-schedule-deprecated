package com.patlejch.messageschedule.view.widgets;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

import com.patlejch.messageschedule.R;

public class TimePickerButton extends AppCompatButton {

    private static final String TIME_DIALOG_TAG = "TIME_DIALOG_TAG";

    private int hour;
    private int minute;

    private TimeChangedCallback callback;
    private FragmentManager fragmentManager;

    public TimePickerButton(Context context) {
        super(context);
        init();
    }

    public TimePickerButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TimePickerButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        OnClickListener onClickListener = new OnClickListener(this, null);
        super.setOnClickListener(onClickListener);
        hour = 0;
        minute = 0;
        updateText();

    }

    @Override
    public void setOnClickListener(View.OnClickListener onClickListener) {
        super.setOnClickListener(new OnClickListener(this, onClickListener));
    }

    public void setTimeChangedCallback(TimeChangedCallback callback) {
        this.callback = callback;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (enabled) {
            setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        } else {
            setTextColor(ContextCompat.getColor(getContext(), R.color.material_grey_700));
        }
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public FragmentManager getFragmentManager() {
        return fragmentManager;
    }

    private void updateText() {

        String text;
        if (hour < 10) {
            text = "0";
            text = text.concat(Integer.toString(hour));
        } else {
            text = Integer.toString(hour);
        }

        text = text.concat(":");

        if (minute < 10) {
            text = text.concat("0");
            text = text.concat(Integer.toString(minute));
        } else {
            text = text.concat(Integer.toString(minute));
        }

        setText(text);

    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setHour(int hour) {
        this.hour = hour;
        onTimeChanged();
    }

    public void setMinute(int minute) {
        this.minute = minute;
        onTimeChanged();
    }

    private void onTimeChanged() {
        updateText();
        if (callback != null) {
            callback.onTimeChanged(minute, hour);
        }
    }

    public interface TimeChangedCallback {
        void onTimeChanged(int minute, int hour);
    }

    private class OnClickListener implements View.OnClickListener {

        private TimePickerButton button;
        private View.OnClickListener other;

        OnClickListener(TimePickerButton b, View.OnClickListener other) {
            button = b;
            this.other = other;
        }

        @Override
        public void onClick(View v) {

            TimePickerFragment timePickerFragment = new TimePickerFragment();
            timePickerFragment.setButton(button);
            timePickerFragment.show(button.getFragmentManager(), TIME_DIALOG_TAG);

            if (other != null) {
                other.onClick(v);
            }

        }
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {


        private TimePickerButton button;

        public void setButton(TimePickerButton button) {
            this.button = button;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            TimePickerDialog dialog = new TimePickerDialog(getActivity(), this, 0, 0, true);
            dialog.updateTime(button.getHour(), button.getMinute());

            return dialog;

        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            if (button != null) {
                button.setMinute(minute);
                button.setHour(hourOfDay);
            }
        }

    }

}
