package com.patlejch.messageschedule.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.patlejch.messageschedule.databinding.ItemScheduleBinding;

import java.util.ArrayList;
import java.util.List;

public class ScheduleRecyclerAdapter extends RecyclerView.Adapter<ScheduleRecyclerAdapter.MessageViewHolder> {

    private LayoutInflater inflater;
    private List<MessageItemViewModel> viewModels;

    public ScheduleRecyclerAdapter(@NonNull Context context, @NonNull ArrayList<MessageItemViewModel> viewModels) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.viewModels = viewModels;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemScheduleBinding binding = ItemScheduleBinding.inflate(inflater, parent, false);
        return new MessageViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        MessageItemViewModel viewModel = viewModels.get(position);
        holder.binding.setViewmodel(viewModel);
    }

    @Override
    public int getItemCount() {
        return viewModels.size();
    }

    public void setList(List<MessageItemViewModel> viewModels) {
        this.viewModels = viewModels;
        notifyDataSetChanged();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {

        ItemScheduleBinding binding;

        MessageViewHolder(ItemScheduleBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
