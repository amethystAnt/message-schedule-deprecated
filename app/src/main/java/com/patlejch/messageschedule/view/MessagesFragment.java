package com.patlejch.messageschedule.view;

import android.databinding.Observable;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.patlejch.messageschedule.R;
import com.patlejch.messageschedule.app.MyApplication;
import com.patlejch.messageschedule.databinding.FragmentMessagesBinding;

public class MessagesFragment extends Fragment {

    private MessagesViewModel viewModel;
    private FragmentMessagesBinding binding;
    private ObservableField.OnPropertyChangedCallback toastCallback;
    private ObservableField.OnPropertyChangedCallback removeMessageCallback;

    public static MessagesFragment newInstance() {
        return new MessagesFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        if (binding == null) {
            binding = FragmentMessagesBinding.inflate(inflater);
        }

        RecyclerView recyclerView = binding.getRoot().findViewById(R.id.recycler_view_messages);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (viewModel != null) {
            binding.setViewmodel(viewModel);
        }

        return binding.getRoot();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (viewModel != null) {
            viewModel.refresh();
        }
    }

    @Override
    public void onDestroy() {
        removeCallbacks();
        super.onDestroy();
    }

    public void setViewModel(@NonNull final MessagesViewModel viewModel) {

        removeCallbacks();
        this.viewModel = viewModel;

        if (binding != null) {
            binding.setViewmodel(viewModel);
        }

        toastCallback = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                String message = ((ObservableField<String>) sender).get();
                if (message != null) {
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    ((ObservableField<String>) sender).set(null);
                }
            }
        };

        viewModel.toastMessage.addOnPropertyChangedCallback(toastCallback);

        removeMessageCallback = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {

                MessagesViewModel.AlertDialogCallbacks callbacks = ((ObservableField<MessagesViewModel.AlertDialogCallbacks>) sender).get();
                if(callbacks == null) {
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity() == null ? MyApplication.getInstance() : getActivity());
                builder.setTitle(R.string.alert_dialog_remove_message_title);
                builder.setMessage(R.string.alert_dialog_remove_message_text);
                builder.setPositiveButton(R.string.alert_dialog_remove_message_positive, callbacks.positive);
                builder.setNegativeButton(R.string.alert_dialog_remove_message_negative, callbacks.negative);
                builder.show();
                ((ObservableField<MessagesViewModel.AlertDialogCallbacks>) sender).set(null);

            }
        };

        viewModel.onClickRemoveDialog.addOnPropertyChangedCallback(removeMessageCallback);
        viewModel.refresh();

    }

    private void removeCallbacks() {

        if (viewModel != null) {
            if (toastCallback != null) {
                viewModel.toastMessage.removeOnPropertyChangedCallback(toastCallback);
            }

            if (removeMessageCallback != null) {
                viewModel.onClickRemoveDialog.removeOnPropertyChangedCallback(removeMessageCallback);
            }
        }

    }

}
