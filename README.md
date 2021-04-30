# Commits

> Commits demonstrating best practices for android development, written in Kotlin

## Tech-stack

    * [Kotlin](https://kotlinlang.org/)
    * [Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html) - perform background operations
    * [Jetpack](https://developer.android.com/jetpack)
        * [Navigation](https://developer.android.com/topic/libraries/architecture/navigation/) - in-app navigation
        * [LiveData](https://developer.android.com/topic/libraries/architecture/livedata) - notify views about database change
        * [Lifecycle](https://developer.android.com/topic/libraries/architecture/lifecycle) - perform an action when lifecycle state changes
        * [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) - store and manage UI-related data in a lifecycle conscious way
    * [Retrofit](https://square.github.io/retrofit/) - networking
    * [Dagger 2 for Android](https://dagger.dev/dev-guide/android)
    * [Custom view](https://developer.android.com/guide/topics/ui/custom-components) - creating custom bar graph, plotting commits count for each month

## Screens

    **RepoListFragment** - Renders list of repositories.
    **DetailsFragment** - Render list of commits and a custom bar graph view representing number of commits made in each respective months.

## Architecture

Modularized code providing benefits of better [separation of concerns](https://en.wikipedia.org/wiki/Separation_of_concerns). Demonstrating [recommended app architecture](https://developer.android.com/jetpack/guide#recommended-app-arch) with structuring into separate modules, that can be grouped as `activity/fragment`, `view models`, `repositories`, `data source`, and `dependecies`.

### Dependency injection using Dagger 2

`AppComponent` - application level dependencies component.
`AppModule` - application level provider factory for `Retrofit client`, `ApiServices`, and `View provider factory`.
`ActivityBuilderModule` and `FragmentBuilderModule` - subcomponent injector for activity and fragments.
`ViewModelKey` - annotation type helping in multibinding  for view provider factory.
    
### Presentation layer

**View (Activity / Fragment)** - render data to the screen and passing user interaction data and having one-way interaction with view models.
**View models** - dispatching state changes via `LiveData` and handling user interactions.
**Data binding** - bind UI component in XML to data source, reducing boiler plate.
**Navigation** - handling navigation.

### Repository layer

**Repository** - independent of other layer handling data operations.
**Model** - defining core structure of the data used within the application.

### Data layer

**ApiService** - `retrofit` defines API endpoints interacting with the external repositories