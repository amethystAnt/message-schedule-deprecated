package com.patlejch.messageschedule.view;

import android.databinding.BaseObservable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

public class ViewModelHolder<ViewModel extends BaseObservable> extends Fragment {

    private ViewModel viewModel;

    public ViewModelHolder() { }

    public static <ViewModel extends BaseObservable>ViewModelHolder createContainer(@NonNull ViewModel viewModel) {
        ViewModelHolder<ViewModel> holder = new ViewModelHolder<>();
        holder.setViewModel(viewModel);
        return holder;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void setViewModel(ViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public ViewModel getViewModel() {
        return viewModel;
    }

}
