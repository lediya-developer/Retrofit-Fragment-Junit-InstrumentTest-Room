package com.lediya.infosys.respository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lediya.infosys.communication.RestClient
import com.lediya.infosys.data.dao.CountryDao
import com.lediya.infosys.model.CountryListResponse
import com.lediya.infosys.model.Row
import com.lediya.infosys.utility.Event
import com.lediya.infosys.utility.ResultImp
import com.lediya.infosys.utility.ResultType

class MainRepository(private var countryDao: CountryDao) {
    val title = MutableLiveData<String>()
    private var _title = MutableLiveData<String>()
    var countryList = MutableLiveData<Event<List<Row>>>()
    private val _fetchResult = MutableLiveData<Event<ResultImp>>()
    val fetchResult: LiveData<Event<ResultImp>>
        get() = _fetchResult
    private val _getResult = MutableLiveData<Event<ResultImp>>()
    val getResult: LiveData<Event<ResultImp>>
        get() = _getResult

    /**
     * The method used to get country data from backend, request perform and set the result  */
    suspend fun getCountryDataFromServer() {
        _getResult.postValue(Event(ResultImp(ResultType.PENDING)))
        try {
            val response = RestClient.getInstance().getList()
            if (response.body() != null) {
                response.body()?.let {
                    insertUpdatedCountryData(it)
                }
            } else {
                _getResult.postValue(Event(ResultImp(ResultType.FAILURE)))
            }
        } catch (e: Exception) {
            _getResult.postValue(Event(ResultImp(ResultType.FAILURE)))
        }
    }

    /** insert country data to local database*/
    private suspend fun insertUpdatedCountryData(countryData: CountryListResponse) {
         countryDao.insertCountryData(countryData)
        _getResult.postValue(Event(ResultImp(ResultType.SUCCESS)))
    }

    /** fetch country data from local database*/
    suspend fun fetchCountryDataFromDatabase() {
        val countryData = countryDao.getCountryData()
        if (countryData!=null&&!countryData.equals(" ") && !countryData.rows.isNullOrEmpty()) {
            countryList.postValue((Event(countryData.rows)))
            title.value = countryData.title
        } else {
            _fetchResult.postValue(Event(ResultImp(ResultType.FAILURE)))
        }
    }
}