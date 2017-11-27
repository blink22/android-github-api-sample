package com.repos.src.controllers;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.repos.src.services.GitHubService;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import io.realm.RealmObject;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Author Ayman Mahgoub.
 */
public class ApiBuilder {

    private static final OkHttpClient CLIENT = new OkHttpClient();
    // To solve conflicts between gson and realm
    private static Gson gson = new GsonBuilder()
            .setExclusionStrategies(new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes f) {
                    return f.getDeclaringClass().equals(RealmObject.class);
                }

                @Override
                public boolean shouldSkipClass(Class<?> clazz) {
                    return false;
                }
            })
            .create();
    private static final Retrofit RETROFIT = new Retrofit.Builder()
            .baseUrl(Constants.SERVER_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(CLIENT)
            .build();
    private static GitHubService gitHubService;

    static {
        CLIENT.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request.Builder request = chain.request()
                        .newBuilder();
                Response response = chain.proceed(request.build());

                return response;
            }
        });
    }

    /**
     * Obtains buildings service. Must be called from UI thread for synchronization reasons.
     *
     * @return buildings service
     */
    public static GitHubService obtainGitHubServiceService() {

        if (gitHubService == null) {
            gitHubService = RETROFIT.create(GitHubService.class);
        }

        return gitHubService;
    }

    public abstract static class CancelableCallback<T> implements Callback<T> {

        private boolean canceled;

        public CancelableCallback() {
            this.canceled = false;
        }

        @Override
        public void onResponse(retrofit.Response<T> response, Retrofit retrofit) {
            if (canceled) {
                return;
            }
            success(response, retrofit);
        }

        @Override
        public void onFailure(Throwable t) {
            if (canceled) {
                return;
            }
            failure(t);
        }

        public void cancel() {
            canceled = true;
        }

        protected abstract void success(retrofit.Response<T> response, Retrofit retrofit);

        protected abstract void failure(Throwable t);
    }

}
