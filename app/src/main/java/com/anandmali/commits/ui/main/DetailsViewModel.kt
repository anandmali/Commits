package com.anandmali.commits.ui.main

import androidx.lifecycle.ViewModel
import com.anandmali.commits.api.model.RepoDetailsModel
import com.anandmali.commits.repository.DetailsRepository
import com.anandmali.commits.repository.remot.MutableStatus
import com.anandmali.commits.repository.remot.NetworkError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

class DetailsViewModel
@Inject constructor(
    private val repository: DetailsRepository
) : ViewModel() {

    private val repoDetailsStatus = MutableStatus<List<RepoDetailsModel>>()

    private var job: Job = Job()

    private val backGroundScope = CoroutineScope(Dispatchers.IO)

    fun getRepoDetailsObserver() = repoDetailsStatus

    fun getRepoDetails(repo: String) {
        repoDetailsStatus postInFlight true
        job = backGroundScope.launch {
            try {
                repository.getRepoDetails(repo).either(::handlerError, ::handleResponse)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun handleResponse(models: List<RepoDetailsModel>) {
        repoDetailsStatus postSuccess models
    }

    private fun handlerError(error: NetworkError) {
        error.message?.let {
            repoDetailsStatus postFailure it
        } ?: repoDetailsStatus postFailure error.messageId

    }

}