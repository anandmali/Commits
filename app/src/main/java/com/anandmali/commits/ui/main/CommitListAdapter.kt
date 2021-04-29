package com.anandmali.commits.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anandmali.commits.api.model.CommitModel
import com.anandmali.commits.api.model.RepoDetailsModel
import com.anandmali.commits.databinding.CommitListItemBinding

class CommitListAdapter (
    private val commitList: List<RepoDetailsModel>
) :
    RecyclerView.Adapter<CommitListAdapter.ViewHolder>() {

    class ViewHolder constructor(private val itemBinding: CommitListItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(commitModel: CommitModel) {
            itemBinding.commit = commitModel
            itemBinding.committer = commitModel.committer
            itemBinding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            CommitListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(commitList[position].commit)
    }

    override fun getItemCount(): Int {
        return commitList.size
    }

}