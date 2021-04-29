package com.anandmali.commits.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import com.anandmali.commits.api.model.RepoModel
import com.anandmali.commits.repository.RepoListRepository
import com.anandmali.commits.repository.remot.MutableStatus
import com.anandmali.commits.repository.remot.NetworkError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class RepoListViewModel
@Inject constructor(
    private val repository: RepoListRepository
) : ViewModel() {

    private val repoListStatus = MutableStatus<List<RepoModel>>()

    private var job: Job = Job()

    private val backGroundScope = CoroutineScope(Dispatchers.IO)

    fun getRepoListObserver() = repoListStatus

    init {
        getRepoList()
    }

    private fun getRepoList() {
        repoListStatus postInFlight true
        job = backGroundScope.launch {
            repository.getRpoList().either(::handlerError, ::handleResponse)
        }
    }

    private fun handleResponse(models: List<RepoModel>) {
        Log.e("handleResponse ", models.size.toString())
        repoListStatus postSuccess models
    }

    private fun handlerError(error: NetworkError) {
        Log.e("handlerError ", error.message ?: "")
        repoListStatus postFailure error.message
    }

    override fun onCleared() {
        super.onCleared()
    }
}