package com.nitkarsh.infogit.paging

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.nitkarsh.infogit.RestServices.models.UsersResponse

/*
 * class for paging, Provides DataSorce.Factory
 */
class SearchPagedDataSourceFactory(var text: String) : DataSource.Factory<Int,UsersResponse>() {

    lateinit var searchPagedDataSource: SearchPagedDataSource
    val mutableLiveData = MutableLiveData<SearchPagedDataSource>()

//    returns a datasource
    override fun create(): DataSource<Int, UsersResponse> {
        searchPagedDataSource = dataSource()
        this.mutableLiveData.postValue(searchPagedDataSource)
        return searchPagedDataSource
    }

    private fun dataSource(): SearchPagedDataSource {
        return SearchPagedDataSource(text)
    }

//    for dynamically test change for query
    public fun setQuery(text: String) {
        this.text = text
    }
}