package com.repos.src.services;

import com.repos.src.models.Repository;
import com.repos.src.models.User;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Ayman Mahgoub on 2/20/16.
 */
public interface GitHubService {

    @GET("users/{user}/repos")
    Call<List<Repository>> listRepositories(@Path("user") String user);

    @GET("repos/{user}/{repo}/contributors")
    Call<List<User>> listContributors(@Path("user") String user, @Path("repo") String repo, @Query("page") int page);
}
