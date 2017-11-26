package com.repos.src.services;

import com.repos.src.models.Repository;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by Ayman Mahgoub on 2/20/16.
 */
public interface GitHubService {

    @GET("users/{user}/repos")
    Call<List<Repository>> listRepositories(@Path("user") String user);
}
