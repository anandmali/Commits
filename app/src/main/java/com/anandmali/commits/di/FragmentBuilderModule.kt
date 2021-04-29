package com.anandmali.commits.di

import com.anandmali.commits.ui.main.DetailsFragment
import com.anandmali.commits.ui.main.RepoListFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuilderModule {

    @ContributesAndroidInjector
    abstract fun contributeRepoListFragment(): RepoListFragment

    @ContributesAndroidInjector
    abstract fun contributeDetailsFragment(): DetailsFragment

}