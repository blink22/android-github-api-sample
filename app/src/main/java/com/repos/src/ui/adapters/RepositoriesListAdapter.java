package com.repos.src.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.repos.src.R;
import com.repos.src.models.Repository;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Ayman Mahgoub on 2/20/16.
 */
public class RepositoriesListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final LayoutInflater mInflater;
    private List<Repository> mItems;
    private RepoSelectedCallBack repoSelectedCallBack;

    public RepositoriesListAdapter(RepoSelectedCallBack repoSelectedCallBack, Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.repoSelectedCallBack = repoSelectedCallBack;
        mItems = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.list_item_repository, parent, false);
        RepoListItemViewHolder repoListItemViewHolder = new RepoListItemViewHolder(view);
        return repoListItemViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RepoListItemViewHolder repoListItemViewHolder = (RepoListItemViewHolder) holder;
        Repository repository = getItem(position);
        repoListItemViewHolder.bind(repository);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void setData(List<Repository> data) {
        mItems.clear();
        mItems.addAll(data);

        notifyDataSetChanged();
    }

    public Repository getItem(int position) {

        if (mItems == null)
            return null;
        return mItems.get(position);
    }

    public class RepoListItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.repo_name)
        TextView repoNameTextView;

        @Bind(R.id.repo_description)
        TextView repoDescription;

        private String repoName;

        public RepoListItemViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            ButterKnife.bind(this, itemView);
        }

        void bind(Repository repository) {
            String name = repository.getName();
            String description = repository.getDescription();
            this.repoName = repository.getName();
            repoNameTextView.setText(name);
            repoDescription.setText(description);
        }

        @Override
        public void onClick(View v) {
            if (repoSelectedCallBack == null)
                return;
            repoSelectedCallBack.onRepoItemSelected(repoName);
        }
    }

    public interface RepoSelectedCallBack {
        void onRepoItemSelected(String repoId);
    }
}
