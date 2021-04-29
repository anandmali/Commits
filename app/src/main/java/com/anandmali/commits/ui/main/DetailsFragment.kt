package com.anandmali.commits.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.anandmali.commits.R
import com.anandmali.commits.api.model.RepoDetailsModel
import com.anandmali.commits.databinding.DetailsFragmentBinding
import com.anandmali.commits.di.AppViewModelFactory
import com.anandmali.commits.repository.remot.Status
import com.anandmali.commits.util.showSnackBar
import dagger.android.support.DaggerFragment
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class DetailsFragment : DaggerFragment() {

    @Inject
    lateinit var factory: AppViewModelFactory

    private lateinit var viewModel: DetailsViewModel

    private val arguments: DetailsFragmentArgs by navArgs()

    private lateinit var detailsFragmentBinding: DetailsFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        detailsFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.details_fragment, container, false)
        return detailsFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this, factory).get(DetailsViewModel::class.java)
        viewModel.getRepoDetails(arguments.repoName)

        viewModel.getRepoDetailsObserver().observe(viewLifecycleOwner, {
            showLoading(false)
            it?.let {
                when (it) {
                    is Status.Success -> showDetails(it.data)
                    is Status.Error -> showError(it.message)
                    is Status.IsInFlight -> showLoading(it.loading)
                }
            }
        })
    }

    private fun showLoading(loading: Boolean) {
        detailsFragmentBinding.progressBar.isVisible = loading
    }

    private fun showError(message: String?) {
        showSnackBar(message ?: "Some error fetching details")
    }

    private fun showDetails(data: List<RepoDetailsModel>) {

        val commitAdapter = CommitListAdapter(data)
        detailsFragmentBinding.commitList.apply {
            adapter = commitAdapter
            layoutManager = LinearLayoutManager(this@DetailsFragment.activity)
            addItemDecoration(
                DividerItemDecoration(
                    this@DetailsFragment.activity,
                    DividerItemDecoration.VERTICAL
                )
            )
        }

        val format: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        format.timeZone = TimeZone.getTimeZone("UTC")
        val calendar = GregorianCalendar.getInstance()


        val map = data.groupBy { item ->
            val stringDate = item.commit.committer.date
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                val instant: Instant = Instant.parse(stringDate)
                instant.atZone(ZoneId.of("UTC")).month.toString()
            } else {
                val date = format.parse(stringDate)
                calendar.time = date!!
                calendar.get(Calendar.MONTH).toShort()
            }
        }

        val dataList = ArrayList<Int>()
        map.forEach {
            dataList.add(it.value.size)
        }

        @Suppress("UNCHECKED_CAST")
        val test: ArrayList<String> = map.keys.toMutableList() as ArrayList<String>
        detailsFragmentBinding.barChart.setBottomTextList(test)
        detailsFragmentBinding.barChart.setTopTextList(dataList)

        detailsFragmentBinding.barChart.setDataList(dataList, dataList.maxOrNull() ?: 0)

    }
}