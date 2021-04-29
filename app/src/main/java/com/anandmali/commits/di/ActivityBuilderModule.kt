package com.anandmali.commits.di

import com.anandmali.commits.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilderModule {

    @ContributesAndroidInjector(
        modules = [FragmentBuilderModule::class]
    )
    abstract fun contributeMainActivity(): MainActivity

}