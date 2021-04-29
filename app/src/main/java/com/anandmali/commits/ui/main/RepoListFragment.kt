package com.anandmali.commits.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.anandmali.commits.R
import com.anandmali.commits.api.model.RepoModel
import com.anandmali.commits.databinding.RepolistFragmentBinding
import com.anandmali.commits.di.AppViewModelFactory
import com.anandmali.commits.repository.remot.Status
import com.anandmali.commits.util.showSnackBar
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class RepoListFragment : DaggerFragment() {

    @Inject
    lateinit var factory: AppViewModelFactory

    private lateinit var viewModel: RepoListViewModel

    private lateinit var repoListFragmentBinding: RepolistFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        repoListFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.repolist_fragment, container, false)

        return repoListFragmentBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, factory).get(RepoListViewModel::class.java)
        viewModel.getRepoListObserver().observe(viewLifecycleOwner, {
            showLoading(false)
            it?.let {
                when (it) {
                    is Status.Success -> handleResponse(it.data)
                    is Status.Error -> showError(it.message)
                    is Status.IsInFlight -> showLoading(it.loading)
                }
            }
        })
    }

    private fun showError(message: String?) {
        showSnackBar(message ?: "Some error fetching details")
    }

    private fun showLoading(loading: Boolean) {
        repoListFragmentBinding.progressBar.isVisible = loading
    }

    private fun handleResponse(data: List<RepoModel>) {
        val repoAdapter = RepoListAdapter(data, ::onRepoClick)
        repoListFragmentBinding.repoList.apply {
            adapter = repoAdapter
            layoutManager = LinearLayoutManager(this@RepoListFragment.activity)
            addItemDecoration(DividerItemDecoration(this@RepoListFragment.activity, DividerItemDecoration.VERTICAL))
        }
    }

    private fun onRepoClick(repoModel: RepoModel) {
        val action =
            RepoListFragmentDirections.actionRepoListFragmentToDetailsFragment(repoModel.name)
        NavHostFragment.findNavController(this).navigate(action)
    }
}