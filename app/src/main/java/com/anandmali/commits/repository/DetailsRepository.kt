package com.anandmali.commits.repository

import com.anandmali.commits.api.ApiService
import com.anandmali.commits.repository.remot.makeApiCall
import javax.inject.Inject

class DetailsRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getRepoDetails(repo: String) =
        makeApiCall(apiService.getRepoDetails(repository = repo))

}