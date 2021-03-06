package com.repos.src.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs;
import com.repos.src.R;
import com.repos.src.activities.RepoDetailsActivity;
import com.repos.src.controllers.ApiBuilder;
import com.repos.src.controllers.ApplicationController;
import com.repos.src.models.Repository;
import com.repos.src.models.User;
import com.repos.src.orm.RepositoriesDao;
import com.repos.src.services.GitHubService;
import com.repos.src.ui.adapters.RepositoriesListAdapter;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Response;
import retrofit.Retrofit;

@FragmentWithArgs
public class RepositoriesListFragment extends Fragment implements RepositoriesListAdapter.RepoSelectedCallBack {

    @Bind(R.id.repos_list_view)
    RecyclerView reposRecyclerView;

    @Bind(R.id.loading_progress_bar)
    ProgressBar progressBar;

    private GitHubService mGitHubService;
    private FragmentActivity mActivity;
    private ListReposCallback mListReposCallback;
    private List<Repository> repos;

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
            mGitHubService.listRepositories(User.GITHUB_DEFAULT_USER).enqueue(mListReposCallback);
        }
    }

    private void setUIValues() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        reposRecyclerView.setLayoutManager(linearLayoutManager);

        RepositoriesListAdapter repositoriesListAdapter = new RepositoriesListAdapter(this, mActivity);
        repositoriesListAdapter.setData(repos);
        reposRecyclerView.setAdapter(repositoriesListAdapter);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onRepoItemSelected(String repoId) {
        Intent intent = RepoDetailsActivity.newIntent(getActivity(), repoId);
        getActivity().startActivity(intent);
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
}
