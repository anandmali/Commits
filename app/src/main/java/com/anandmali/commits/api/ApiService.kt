package com.anandmali.commits.api

import com.anandmali.commits.api.model.RepoDetailsModel
import com.anandmali.commits.api.model.RepoModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("users/mralexgray/repos")
    suspend fun getRepoList(): Response<List<RepoModel>>

    @GET("repos/{user}/{repository}/commits")
    suspend fun getRepoDetails(
        @Path("user") user: String = "mralexgray",
        @Path("repository") repository: String
    ): Response<List<RepoDetailsModel>>

}