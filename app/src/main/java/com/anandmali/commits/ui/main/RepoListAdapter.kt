package com.anandmali.commits.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anandmali.commits.api.model.RepoModel
import com.anandmali.commits.databinding.RepoListItemBinding

class RepoListAdapter(
    private val repoList: List<RepoModel>,
    private val onClick: (RepoModel) -> Unit
) :
    RecyclerView.Adapter<RepoListAdapter.ViewHolder>() {

    class ViewHolder constructor(private val itemBinding: RepoListItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(repoModel: RepoModel) {
            itemBinding.repo = repoModel
            itemBinding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RepoListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(repoList[position])
        holder.itemView.setOnClickListener {
            onClick(repoList[position])
        }
    }

    override fun getItemCount(): Int {
        return repoList.size
    }

}