package com.patlejch.messageschedule.view;

import android.content.Intent;
import android.content.res.Resources;
import android.databinding.BaseObservable;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.patlejch.messageschedule.R;
import com.patlejch.messageschedule.app.MyApplication;
import com.patlejch.messageschedule.dagger.components.DaggerMessagesComponent;
import com.patlejch.messageschedule.dagger.components.MessagesComponent;
import com.patlejch.messageschedule.dagger.components.SingletonComponent;
import com.patlejch.messageschedule.dagger.modules.MessagesModule;
import com.patlejch.messageschedule.data.MessageDataSource;
import com.patlejch.messageschedule.utils.Utils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG_SCHEDULE_VM = "TAG_SCHEDULE_VM";
    private static final String TAG_HISTORY_VM = "TAG_HISTORY_VM";

    private MessagesViewModel scheduleViewModel, historyViewModel;
    private SingletonComponent singletonComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (singletonComponent == null) {
            singletonComponent = MyApplication.getInstance().getSingletonComponent();
        }

        MessagesNavigator navigator = new MessagesNavigator() {
            @Override
            public void onMessageEdit(String key, MessageDataSource.MessagesListType listType) {

                Intent intent = new Intent(MainActivity.this, MessageEditorActivity.class);
                intent.putExtra(MessageEditorFragment.ARGUMENT_MESSAGE_ID, key);
                intent.putExtra(MessageEditorFragment.ARGUMENT_MESSAGE_LIST, listType.name());
                startActivity(intent);

            }
        };

        scheduleViewModel = (MessagesViewModel) findOrCreateViewModel(TAG_SCHEDULE_VM);
        scheduleViewModel.setNavigator(navigator);

        historyViewModel = (MessagesViewModel) findOrCreateViewModel(TAG_HISTORY_VM);
        historyViewModel.setNavigator(navigator);

        FloatingActionButton actionButton = findViewById(R.id.floating_button_new);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MessageEditorActivity.class);
                startActivity(intent);
            }
        });

        ViewPager viewPager = findViewById(R.id.view_pager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        MessageFragmentsViewPagerAdapter pagerAdapter = new MessageFragmentsViewPagerAdapter(getSupportFragmentManager());

        Resources resources = singletonComponent.resources();
        pagerAdapter.addViewmodel(scheduleViewModel, resources.getString(R.string.title_schedule));
        pagerAdapter.addViewmodel(historyViewModel, resources.getString(R.string.title_history));

        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_watch_later_white_24dp);
        tabLayout.getTabAt(0).getIcon().setColorFilter(resources.getColor(R.color.material_gray_300), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_history_white_24dp);
        tabLayout.getTabAt(1).getIcon().setColorFilter(resources.getColor(R.color.material_gray_300), PorterDuff.Mode.SRC_IN);

        Utils.askForPermissions(MainActivity.this);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (scheduleViewModel != null) {
            scheduleViewModel.setNavigator(null);
        }

        if (historyViewModel != null) {
            historyViewModel.setNavigator(null);
        }

    }

    //For testing purpose, default value set in onCreate() if none set before
    public void setSingletonComponent(SingletonComponent singletonComponent) {
        this.singletonComponent = singletonComponent;
    }

    private BaseObservable findOrCreateViewModel(String tag) {

        ViewModelHolder<?> viewModelHolder = (ViewModelHolder<?>) getSupportFragmentManager()
                .findFragmentByTag(tag);
        if (viewModelHolder == null || viewModelHolder.getViewModel() == null) {

            MessagesModule module = new MessagesModule(tag.equals(TAG_SCHEDULE_VM)
                    ? MessageDataSource.MessagesListType.LIST_SCHEDULE
                    : MessageDataSource.MessagesListType.LIST_HISTORY);

            MessagesComponent component = DaggerMessagesComponent.builder()
                    .messagesModule(module)
                    .singletonComponent(singletonComponent)
                    .build();
            MessagesViewModel viewModel = new MessagesViewModel(component);

            viewModelHolder = ViewModelHolder.createContainer(viewModel);
            Utils.addFragmentToManager(viewModelHolder, getSupportFragmentManager(), tag);
            return viewModel;

        }

        return viewModelHolder.getViewModel();

    }

    private class MessageFragmentsViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<MessagesViewModel> viewmodels;
        private ArrayList<String> titles;

        MessageFragmentsViewPagerAdapter(FragmentManager fm) {
            super(fm);
            viewmodels = new ArrayList<>();
            titles = new ArrayList<>();
        }

        @NonNull
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            MessagesFragment fragment = (MessagesFragment) super.instantiateItem(container, position);
            MessagesViewModel viewmodel = viewmodels.get(position);
            if (viewmodel != null) {
                fragment.setViewModel(viewmodel);
            }
            fragment.setSingletonComponent(singletonComponent);
            return fragment;

        }

        @Override
        public Fragment getItem(int position) {
            return new MessagesFragment();
        }

        @Override
        public int getCount() {
            return viewmodels.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }

        void addViewmodel(@NonNull MessagesViewModel viewmodel, @NonNull String title) {
            viewmodels.add(viewmodel);
            titles.add(title);
        }

    }

}
