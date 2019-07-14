# InfoGit
This is a pseudo Android project that which would let users help search and analyse GitHub users conveniently. 
This application is build on MVVM architecture using **Kotlin**, **Coroutines**, **ViewModel**, **LiveData**, **Paging** and **Android Architecture Components**.

The application retrieves the list of github repositories of  the username entered.Data is loaded using the paging library.
It uses retrofit for all network/api calls. Data is observed in LiveData with ViewModel so Livedata updates app component observers that are in an active lifecycle state.
Also it let them navigate to the profile details and their followers. 


### Libraries Used and purpose :
| Library | Description |
| ------------- | ------------- |
| [Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html) | For asynchronous programming |
| [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) | Maintaining UI related data in Lifecycle conscious way |
| [LiveData](https://developer.android.com/topic/libraries/architecture/livedata) | Lifcycle aware observable data holder
| [Retrofit2](https://github.com/square/retrofit) | For making network/api calls |
| [Paging](https://developer.android.com/topic/libraries/architecture/paging) | For paging the data |
| [Glide](https://github.com/bumptech/glide) | For processing and caching images |
