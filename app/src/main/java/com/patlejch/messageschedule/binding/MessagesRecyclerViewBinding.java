package com.patlejch.messageschedule.binding;

import android.databinding.BindingAdapter;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

public class MessagesRecyclerViewBinding {

    @BindingAdapter("app:touchHelperRight")
    public static void setMessagesRecyclerViewTouchHelperRight(@NonNull RecyclerView recyclerView,
                                                               ItemTouchHelper.SimpleCallback callback) {
        if (callback != null) {
            ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
            touchHelper.attachToRecyclerView(recyclerView);
        }
    }

    @BindingAdapter("app:adapter")
    public static void setMessagesRecyclerViewAdapter(@NonNull RecyclerView recyclerView,
                                                      RecyclerView.Adapter adapter) {
        if (adapter != null) {
            recyclerView.setAdapter(adapter);
        }
    }

}
