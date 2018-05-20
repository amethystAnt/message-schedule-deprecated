package com.patlejch.messageschedule.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.databinding.Observable;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.patlejch.messageschedule.R;
import com.patlejch.messageschedule.app.MyApplication;
import com.patlejch.messageschedule.data.ContactsDataSource;
import com.patlejch.messageschedule.data.Message;
import com.patlejch.messageschedule.data.MessageDataSource;
import com.patlejch.messageschedule.databinding.FragmentMessageEditorBinding;
import com.patlejch.messageschedule.view.widgets.DatePickerButton;
import com.patlejch.messageschedule.view.widgets.TimePickerButton;
import com.pchmn.materialchips.ChipsInput;
import com.pchmn.materialchips.model.Chip;
import com.pchmn.materialchips.model.ChipInterface;

import java.util.ArrayList;
import java.util.List;

public class MessageEditorFragment extends Fragment {

    public static final String ARGUMENT_MESSAGE_ID = "ARGUMENT_MESSAGE_ID";
    public static final String ARGUMENT_MESSAGE_LIST = "ARGUMENT_MESSAGE_LIST";

    private MessageEditorViewModel viewModel;
    private FragmentMessageEditorBinding binding;
    private Observable.OnPropertyChangedCallback toastCallback;
    private Observable.OnPropertyChangedCallback warningsCallback;

    public MessageEditorFragment() { }

    public static MessageEditorFragment newInstance() {
        return new MessageEditorFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (binding == null) {
            binding = FragmentMessageEditorBinding.inflate(inflater);
        }

        ChipsInput chipsInput = binding.getRoot().findViewById(R.id.chips_input_recipients);
        initChipsInput(chipsInput);

        binding.setViewmodel(viewModel);

        TimePickerButton timePickerButton = binding.getRoot().findViewById(R.id.time_picker);
        timePickerButton.setFragmentManager(getFragmentManager());
        timePickerButton.setTimeChangedCallback(new TimePickerButton.TimeChangedCallback() {
            @Override
            public void onTimeChanged(int minute, int hour) {
                if (viewModel != null) {
                    viewModel.minute.set(minute);
                    viewModel.hour.set(hour);
                }
            }
        });
        DatePickerButton datePickerButton = binding.getRoot().findViewById(R.id.date_picker);
        datePickerButton.setFragmentManager(getFragmentManager());
        datePickerButton.setDateChangedCallback(new DatePickerButton.DateChangedCallback() {
            @Override
            public void onDateChanged(int day, int month, int year) {
                if (viewModel != null) {
                    viewModel.day.set(day);
                    viewModel.month.set(month);
                    viewModel.year.set(year);
                }
            }
        });

        return binding.getRoot();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (viewModel != null) {
            Bundle arguments = getArguments();
            String messageKey = null;
            MessageDataSource.MessagesListType type = null;
            if (arguments != null) {
                messageKey = arguments.getString(ARGUMENT_MESSAGE_ID);
                String t = arguments.getString(ARGUMENT_MESSAGE_LIST);
                if (t != null) {
                    type = MessageDataSource.MessagesListType.valueOf(t);
                }
            }
            viewModel.start(messageKey, type);
        }
    }

    @Override
    public void onDestroy() {
        removeCallbacks();
        super.onDestroy();
    }

    private void initChipsInput(@NonNull final ChipsInput chipsInput) {

        ArrayList<Message.Recipient> contacts = new ArrayList<>();
        try {
            contacts = ContactsDataSource.getInstance().getContacts();
        } catch (RuntimeException e) {
            Toast.makeText(getContext(), getString(R.string.error_permission_contacts),
                    Toast.LENGTH_SHORT).show();
        }

        ArrayList<Chip> chips = new ArrayList<>();
        for (Message.Recipient contact : contacts) {
            chips.add(new Chip(contact.number, contact.name, contact.number));
        }

        chipsInput.setFilterableList(chips);
        chipsInput.addChipsListener(new ChipsInput.ChipsListener() {

            private void onChange() {
                if (viewModel != null) {
                    viewModel.chips.clear();
                    viewModel.chips.addAll(chipsInput.getSelectedChipList());
                }
            }

            @Override
            public void onChipAdded(ChipInterface chipInterface, int i) {
                onChange();
            }

            @Override
            public void onChipRemoved(ChipInterface chipInterface, int i) {
                onChange();
            }

            @Override
            public void onTextChanged(CharSequence charSequence) { }
        });

    }

    public void setViewModel(MessageEditorViewModel viewModel) {

        removeCallbacks();
        this.viewModel = viewModel;

        toastCallback = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                String message = ((ObservableField<String>) sender).get();
                if (message == null) {
                    return;
                }
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                ((ObservableField<String>) sender).set(null);
            }
        };

        warningsCallback = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                List<String> warnings = ((ObservableField<List<String>>) sender).get();
                if (warnings == null) {
                    return;
                }
                WarningsDialog dialog = new WarningsDialog();
                dialog.setWarnings(warnings);

                FragmentManager fragmentManager = getFragmentManager();
                if (fragmentManager != null) {
                    dialog.show(fragmentManager, "");
                }

                ((ObservableField<List<String>>) sender).set(null);
            }
        };

        viewModel.toastMessage.addOnPropertyChangedCallback(toastCallback);
        viewModel.warningsList.addOnPropertyChangedCallback(warningsCallback);

        if (binding != null) {
            binding.setViewmodel(viewModel);
        }

    }

    private void removeCallbacks() {
        if (viewModel != null) {
            viewModel.toastMessage.removeOnPropertyChangedCallback(toastCallback);
            viewModel.warningsList.removeOnPropertyChangedCallback(warningsCallback);
        }
    }

    public static class WarningsDialog extends DialogFragment {

        private List<String> warnings;

        public void setWarnings(List<String> warnings) {
            this.warnings = warnings;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            Dialog dialog;
            Activity activity = getActivity();
            AlertDialog.Builder builder = new AlertDialog.Builder(activity == null
                    ? MyApplication.getInstance() : activity);
            builder.setTitle(getResources().getString(R.string.warning_title));

            if (warnings == null) {
                dialog = builder.create();
                return dialog;
            }

            String[] warnings = new String[this.warnings.size()];
            for (int i = 0; i < this.warnings.size(); i++) {
                warnings[i] = this.warnings.get(i);
            }

            builder.setItems(warnings, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dismiss();
                }
            });

            dialog = builder.show();
            return dialog;

        }

    }


}
