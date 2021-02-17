package com.lediya.infosys.view.viewModel

import android.app.Application
import androidx.lifecycle.*
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
    var countryList = MediatorLiveData<Event<List<Row>>> ()
    init {
        val db = AppDatabase.getDatabase(application)
        if (db != null) {
            repository = MainRepository(db.countryDao)
        }
        fetchResult.addSource(repository.fetchResult) {
                fetchResult.value = it
        }
        title.addSource(repository.title) {
            title.value = it
        }
        countryList.addSource(repository.countryList){
            countryList.value = it
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
}

