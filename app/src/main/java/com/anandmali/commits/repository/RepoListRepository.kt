package com.anandmali.commits.repository

import com.anandmali.commits.api.ApiService
import com.anandmali.commits.repository.remot.makeApiCall
import javax.inject.Inject

class RepoListRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getRpoList() = makeApiCall(apiService.getRepoList())

}