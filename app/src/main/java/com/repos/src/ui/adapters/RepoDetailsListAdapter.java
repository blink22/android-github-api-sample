package com.repos.src.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.repos.src.R;
import com.repos.src.models.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by tarekAshraf on 11/26/17.
 */

public class RepoDetailsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater mInflater;
    private List<User> mItems;
    private Context context;

    public RepoDetailsListAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        mItems = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.list_item_contributor, parent, false);
        RepoContributorItemViewHolder repoContributorItemViewHolder = new RepoContributorItemViewHolder(view);
        return repoContributorItemViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RepoContributorItemViewHolder repoContributorItemViewHolder = (RepoContributorItemViewHolder) holder;
        User repository = getItem(position);
        repoContributorItemViewHolder.bind(repository);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void setData(List<User> data) {
        mItems.clear();
        mItems.addAll(data);

        notifyDataSetChanged();
    }

    public User getItem(int position) {

        if (mItems == null)
            return null;
        return mItems.get(position);
    }

    public class RepoContributorItemViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.user_name)
        TextView usernameTextView;

        @Bind(R.id.user_contribution)
        TextView userContributionTextView;

        @Bind(R.id.user_avatar)
        CircleImageView userAvatarImageView;


        public RepoContributorItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(User user) {
            Picasso.with(context).load(user.getAvatarUrl()).into(userAvatarImageView);
            usernameTextView.setText(user.getUsername());
            userContributionTextView.setText(context.getString(R.string.num_commits_string, user.getContributions()));
        }
    }
}
