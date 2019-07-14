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
        searchPagedDataSourceFactory = SearchPagedDataSourceFactory(string)
        networkState = Transformations.switchMap(searchPagedDataSourceFactory.mutableLiveData) { it.networkState }
        message = Transformations.switchMap(searchPagedDataSourceFactory.mutableLiveData) { it.message }
        val pagedListConfig = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setInitialLoadSizeHint(15)
            .setPageSize(15)
            .build()
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

    fun setQuery(string: String) {
        this.string = string
        searchPagedDataSourceFactory.setQuery(string)
    }


}