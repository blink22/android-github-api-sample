package com.repos.src.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hannesdorfmann.fragmentargs.FragmentArgs;
import com.hannesdorfmann.fragmentargs.annotation.Arg;
import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs;
import com.repos.src.R;
import com.repos.src.controllers.ApiBuilder;
import com.repos.src.models.User;
import com.repos.src.services.GitHubService;
import com.repos.src.ui.adapters.RepoDetailsListAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Response;
import retrofit.Retrofit;


@FragmentWithArgs
public class RepoDetailsFragment extends Fragment {

    public static final String OWNER_NAME = "octocat";

    @Arg
    String repoName;

    @Bind(R.id.repo_contributors_rv)
    RecyclerView repoContributorsRecyclerView;

    private GitHubService mGitHubService;
    private ListRepoContributors mListRepoContributors;
    private List<User> contributors;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_repo_details, container, false);
        ButterKnife.bind(this, view);
        FragmentArgs.inject(this);
        initialize();
        return view;
    }

    private void initialize() {
        mGitHubService = ApiBuilder.obtainGitHubServiceService();
        contributors = new ArrayList<>();

        mListRepoContributors = new ListRepoContributors();
        mGitHubService.listContributors(OWNER_NAME, repoName, 0).enqueue(mListRepoContributors);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        repoContributorsRecyclerView.setLayoutManager(linearLayoutManager);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mListRepoContributors != null)
            mListRepoContributors.cancel();
    }

    private class ListRepoContributors extends ApiBuilder.CancelableCallback<List<User>> {
        @Override
        protected void success(Response<List<User>> response, Retrofit retrofit) {
            if (response.isSuccess()) {
                contributors.addAll(response.body());
                RepoDetailsListAdapter repoDetailsListAdapter = new RepoDetailsListAdapter(getActivity());
                repoDetailsListAdapter.setData(contributors);
                repoContributorsRecyclerView.setAdapter(repoDetailsListAdapter);
            }
        }

        @Override
        protected void failure(Throwable t) {

        }
    }
}
