package com.repos.src.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.f2prateek.dart.Dart;
import com.f2prateek.dart.InjectExtra;
import com.repos.src.R;
import com.repos.src.fragments.RepoDetailsFragmentBuilder;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class RepoDetailsActivity extends AppCompatActivity {

    @InjectExtra
    String repoName;

    public static Intent newIntent(Context context, String repoName) {
        return Henson.with(context)
                .gotoRepoDetailsActivity()
                .repoName(repoName)
                .build();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repo_details);
        setActionBar();
        Dart.inject(this);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_repo_details_container);

        if (fragment == null) {
            RepoDetailsFragmentBuilder filteredChantsFragmentBuilder = new RepoDetailsFragmentBuilder(repoName);
            fragment = filteredChantsFragmentBuilder.build();

            fm.beginTransaction()
                    .add(R.id.fragment_repo_details_container, fragment)
                    .commit();
        }
    }

    @OnClick(R.id.back_btn)
    void onClickBackButton() {
        finish();
    }

    public void setActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.details_activity_toolbar_layout);
        View actionBarCustomView = actionBar.getCustomView();
        ButterKnife.bind(this, actionBarCustomView);
        setActionbarFeatures(actionBarCustomView, actionBar);
    }

    public void setActionbarFeatures(View customView, ActionBar actionBar) {
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        Toolbar parent = (Toolbar) customView.getParent();
        parent.setContentInsetsAbsolute(0, 0);
    }
}
