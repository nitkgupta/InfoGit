package com.nitkarsh.infogit.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.nitkarsh.infogit.RestServices.models.UsersResponse
import com.nitkarsh.infogit.paging.SearchPagedDataSourceFactory

class SearchUsersViewModel() : ViewModel() {
    var string: String = ""
    var networkState: LiveData<Int>
    var userResponseLiveData: LiveData<PagedList<UsersResponse>>
    var searchPagedDataSourceFactory: SearchPagedDataSourceFactory
    var message: LiveData<String>
    val listLimit by lazy { MutableLiveData<Int>() }

    init {
//        DataSource factory
        searchPagedDataSourceFactory = SearchPagedDataSourceFactory(string)

        /*
         * live data for network state(Between calling an api and receiving the response)
         */
        networkState = Transformations.switchMap(searchPagedDataSourceFactory.mutableLiveData) { it.networkState }

//        livedata for observing api response message
        message = Transformations.switchMap(searchPagedDataSourceFactory.mutableLiveData) { it.message }
        val pagedListConfig = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setInitialLoadSizeHint(15)
            .setPageSize(15)
            .build()

        /*
         * returns pagedlist which will be used with recyclerview adapter with paging
         * Boundary Callback in case if no data is laoded or data inserted at front or end
         */
        userResponseLiveData = (LivePagedListBuilder(searchPagedDataSourceFactory, pagedListConfig))
            .setBoundaryCallback(object : PagedList.BoundaryCallback<UsersResponse>() {
                override fun onZeroItemsLoaded() {
                    super.onZeroItemsLoaded()
                    listLimit.postValue(0)
                }

                override fun onItemAtEndLoaded(itemAtEnd: UsersResponse) {
                    super.onItemAtEndLoaded(itemAtEnd)
                    listLimit.postValue(1)
                }

                override fun onItemAtFrontLoaded(itemAtFront: UsersResponse) {
                    super.onItemAtFrontLoaded(itemAtFront)
                    listLimit.postValue(1)
                }
            }).build()
    }

    /*
    * method for setting the text for which network call is to be made and chunks loaded into data source
     */
    fun setQuery(string: String) {
        this.string = string
        searchPagedDataSourceFactory.setQuery(string)
    }


}