package com.anandmali.commits.api.model

data class RepoDetailsModel(val commit: CommitModel)

data class CommitModel(
    val message: String,
    val committer: Committer
)

data class Committer(
    val name: String,
    val email: String,
    val date: String
)
