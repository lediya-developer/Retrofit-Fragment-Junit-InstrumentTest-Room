package com.lediya.infosys.view.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lediya.infosys.data.AppDatabase
import com.lediya.infosys.model.Row
import com.lediya.infosys.respository.MainRepository
import com.lediya.infosys.utility.Event
import com.lediya.infosys.utility.ResultImp
import kotlinx.coroutines.launch

class ListScreenViewModel(application: Application) : AndroidViewModel(application) {
    private lateinit var repository: MainRepository
    var title = MediatorLiveData<String>()
    var fetchResult = MediatorLiveData<Event<ResultImp>>()
    var getResult = MediatorLiveData<Event<ResultImp>>()
    var countryList = MediatorLiveData<Event<List<Row>>> ()
    var pullRefresh = MutableLiveData<Boolean>()
    init {
        val db = AppDatabase.getDatabase(application)
        if (db != null) {
            repository = MainRepository(db.countryDao)
        }
        pullRefresh.value = false
        fetchResult.addSource(repository.fetchResult) {
                fetchResult.value = it
        }
        getResult.addSource(repository.getResult) {
            getResult.value = it
        }
        title.addSource(repository.title) {
            title.value = it
        }
        countryList.addSource(repository.countryList){
            event ->
            event.getContentIfNotHandled()?.let { result ->
                countryList.postValue(Event(result))
            }
        }
    }
    /**
     * The method used to get country data from repository, request perform using the corountine scope and set the result  */
    fun downloadCountryData() = viewModelScope.launch {
        repository.getCountryDataFromServer()
    }
    /** fetch country data from local database*/
    fun getCountryDataFromDatabase()= viewModelScope.launch {
        repository.fetchCountryDataFromDatabase()
    }
    /**
     * if its data available in local database , it will load the data otherwise it will send data request
     */
    fun getCountryData(){
        if(pullRefresh.value!=true){
            getCountryDataFromDatabase()
        }else{
            downloadCountryData()
        }
    }
}

