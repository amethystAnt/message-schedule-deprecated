package com.patlejch.messageschedule.view.widgets;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.databinding.BindingAdapter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.View;
import android.widget.DatePicker;

import com.patlejch.messageschedule.R;
import com.patlejch.messageschedule.app.MyApplication;
import com.patlejch.messageschedule.utils.Utils;

public class DatePickerButton extends AppCompatButton {

    private static final String DATE_DIALOG_TAG = "DATE_DIALOG_TAG";

    private int day;
    private int month;
    private int year;

    private DateChangedCallback callback;
    private FragmentManager fragmentManager;

    public DatePickerButton(Context context) {
        super(context);
        init();
    }

    public DatePickerButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DatePickerButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        OnClickListener onClickListener = new OnClickListener(this, null);
        super.setOnClickListener(onClickListener);
        day = 0;
        month = 0;
        year = 2000;
        updateText();

    }

    @Override
    public void setOnClickListener(View.OnClickListener onClickListener) {
        super.setOnClickListener(new OnClickListener(this, onClickListener));
    }

    public void setDateChangedCallback(DateChangedCallback callback) {
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
        text = "";
        text = text.concat(Utils.getMonthName3Letters(month, MyApplication.getInstance().getResources()) + " ");
        if (day < 10) {
            text = text.concat("0");
            text = text.concat(Integer.toString(day) + ", ");
        } else {
            text = text.concat(Integer.toString(day) + ", ");
        }

        text = text.concat(Integer.toString(year));
        setText(text);

    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public void setDay(int day) {
        this.day = day;
        onDateChanged();
    }

    public void setMonth(int month) {
        this.month = month;
        onDateChanged();
    }

    public void setYear(int year) {
        this.year = year;
        onDateChanged();
    }

    private void onDateChanged() {
        updateText();
        if (callback != null) {
            callback.onDateChanged(day, month, year);
        }
    }

    private class OnClickListener implements View.OnClickListener {

        private DatePickerButton button;
        private View.OnClickListener other;

        OnClickListener(DatePickerButton b, View.OnClickListener other) {
            button = b;
            this.other = other;
        }

        @Override
        public void onClick(View v) {

            DatePickerFragment datePickerFragment = new DatePickerFragment();
            datePickerFragment.setButton(button);

            if (button.getFragmentManager() == null) {
                return;
            }

            datePickerFragment.show(button.getFragmentManager(), DATE_DIALOG_TAG);

            if (other != null) {
                other.onClick(v);
            }

        }
    }

    public interface DateChangedCallback {
        void onDateChanged(int day, int month, int year);
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {


        private DatePickerButton button;

        public void setButton(DatePickerButton button) {
            this.button = button;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            return new DatePickerDialog(getContext(), this, button.getYear(),
                    button.getMonth(), button.getDay());

        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            if (button != null) {

                button.setYear(year);
                button.setMonth(monthOfYear);
                button.setDay(dayOfMonth);

            }

        }
    }


}
