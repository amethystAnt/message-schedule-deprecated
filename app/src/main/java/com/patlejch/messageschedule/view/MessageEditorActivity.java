package com.patlejch.messageschedule.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.patlejch.messageschedule.R;
import com.patlejch.messageschedule.app.MyApplication;
import com.patlejch.messageschedule.dagger.components.DaggerMessageEditorComponent;
import com.patlejch.messageschedule.dagger.components.MessageEditorComponent;
import com.patlejch.messageschedule.dagger.components.SingletonComponent;
import com.patlejch.messageschedule.utils.Utils;

public class MessageEditorActivity extends AppCompatActivity {

    private static final String TAG_EDITOR_FRAGMENT = "TAG_EDITOR_FRAGMENT";
    private static final String TAG_EDITOR_VM = "TAG_EDITOR_VM";

    private MessageEditorViewModel viewModel;
    private SingletonComponent singletonComponent;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (singletonComponent == null) {
            singletonComponent = MyApplication.getInstance().getSingletonComponent();
        }

        setContentView(R.layout.activity_message_editor);

        MessageEditorNavigator navigator = new MessageEditorNavigator() {
            @Override
            public void onFinish() {
                onBackPressed();
            }
        };

        MessageEditorFragment fragment = findOrCreateFragment();
        viewModel = findOrCreateViewModel();
        viewModel.setNavigator(navigator);
        fragment.setViewModel(viewModel);

        Toolbar toolbar = findViewById(R.id.toolbar_message_editor);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(singletonComponent.resources().getString(R.string.title_toolbar_message_editor));
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container_editor, fragment);
        transaction.commit();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (viewModel != null) {
            viewModel.setNavigator(null);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_message_editor, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_finnish:
                viewModel.saveAndFinish();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //for testing purpose, default value set in onCreate()
    public void setSingletonComponent(SingletonComponent component) {
        singletonComponent = component;
    }

    private MessageEditorFragment findOrCreateFragment() {

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_EDITOR_FRAGMENT);
        if (fragment == null) {
            fragment = MessageEditorFragment.newInstance(singletonComponent);
            Bundle bundle = new Bundle();
            bundle.putString(MessageEditorFragment.ARGUMENT_MESSAGE_ID,
                    getIntent().getStringExtra(MessageEditorFragment.ARGUMENT_MESSAGE_ID));
            bundle.putString(MessageEditorFragment.ARGUMENT_MESSAGE_LIST,
                    getIntent().getStringExtra(MessageEditorFragment.ARGUMENT_MESSAGE_LIST));
            fragment.setArguments(bundle);
            Utils.addFragmentToManager(fragment, getSupportFragmentManager(), TAG_EDITOR_FRAGMENT);
        }

        return (MessageEditorFragment) fragment;

    }

    private MessageEditorViewModel findOrCreateViewModel() {

        ViewModelHolder<?> viewModelHolder = (ViewModelHolder<?>) getSupportFragmentManager()
                .findFragmentByTag(TAG_EDITOR_VM);
        if (viewModelHolder == null || viewModelHolder.getViewModel() == null) {

            MessageEditorComponent component = DaggerMessageEditorComponent
                    .builder()
                    .singletonComponent(singletonComponent)
                    .build();
            MessageEditorViewModel viewModel = new MessageEditorViewModel(component);

            viewModelHolder = ViewModelHolder.createContainer(viewModel);
            Utils.addFragmentToManager(viewModelHolder, getSupportFragmentManager(), TAG_EDITOR_VM);
            return viewModel;

        }

        return (MessageEditorViewModel) viewModelHolder.getViewModel();

    }

}
