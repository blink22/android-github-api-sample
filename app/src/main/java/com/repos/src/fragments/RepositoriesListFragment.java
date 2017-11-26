package com.repos.src.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.repos.src.R;
import com.repos.src.controllers.ApiBuilder;
import com.repos.src.controllers.ApplicationController;
import com.repos.src.models.User;
import com.repos.src.models.Repository;
import com.repos.src.orm.RepositoriesDao;
import com.repos.src.services.GitHubService;
import com.repos.src.ui.adapters.RepositoriesListAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Response;
import retrofit.Retrofit;

public class RepositoriesListFragment extends Fragment {

    @Bind(R.id.repos_list_view)
    RecyclerView reposRecyclerView;

    @Bind(R.id.loading_progress_bar)
    ProgressBar progressBar;

    private GitHubService mGitHubService;
    private FragmentActivity mActivity;
    private ListReposCallback mListReposCallback;
    private ListRepoContributors mListRepoContributors;
    private List<Repository> repos;

    private List<User> contributors = new ArrayList<>();

    public static RepositoriesListFragment getInstance() {
        RepositoriesListFragment repositoriesListFragment = new RepositoriesListFragment();
        return repositoriesListFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = getActivity();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_repositories_list, container, false);
        ButterKnife.bind(this, view);
        initialize();
        return view;
    }

    @Override
    public void onDestroyView() {
        // Cancel web call

        if (mListReposCallback != null)
            mListReposCallback.cancel();

        if (mListRepoContributors != null)
            mListRepoContributors.cancel();
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        mActivity = null;
        super.onDetach();
    }

    private void initialize() {
        // Fetch repos

        if (!ApplicationController.isNetworkAvailable()) {
            // bring it from realm
            RepositoriesDao repositoriesDao = RepositoriesDao.getInstance();
            repos = repositoriesDao.findAll();
            setUIValues();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            mGitHubService = ApiBuilder.obtainGitHubServiceService();
            mListReposCallback = new ListReposCallback();
            mListRepoContributors = new ListRepoContributors();
            mGitHubService.listRepositories(User.GITHUB_DEFAULT_USER).enqueue(mListReposCallback);

            for(int page=0; page<100; page++)
                mGitHubService.listContributors("octocat", "linguist", page).enqueue(mListRepoContributors);
        }
    }

    private void setUIValues() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        reposRecyclerView.setLayoutManager(linearLayoutManager);

        RepositoriesListAdapter repositoriesListAdapter = new RepositoriesListAdapter(mActivity);
        repositoriesListAdapter.setData(repos);
        reposRecyclerView.setAdapter(repositoriesListAdapter);
        progressBar.setVisibility(View.GONE);
    }

    private class ListReposCallback extends ApiBuilder.CancelableCallback<List<Repository>> {

        @Override
        protected void success(Response<List<Repository>> response, Retrofit retrofit) {

            if (response.isSuccess()) {
                repos = response.body();
                // save data into realm
                RepositoriesDao repositoriesDao = RepositoriesDao.getInstance();
                repositoriesDao.insertOrUpdate(repos);
                setUIValues();
            }
        }

        @Override
        protected void failure(Throwable exception) {
            progressBar.setVisibility(View.GONE);
            // TODO: handle error message and display to user
        }
    }

    private class ListRepoContributors extends ApiBuilder.CancelableCallback<List<User>> {
        @Override
        protected void success(Response<List<User>> response, Retrofit retrofit) {
            if(response.isSuccess()){
                contributors.addAll(response.body());
                Log.d("TESTO", ""+contributors.size());
            }
        }

        @Override
        protected void failure(Throwable t) {

        }
    }
}
