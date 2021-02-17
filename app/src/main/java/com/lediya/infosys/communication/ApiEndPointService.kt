package com.lediya.infosys.communication


import com.lediya.infosys.model.CountryListResponse
import retrofit2.Response
import retrofit2.http.GET
/**
 * Interface class to list out the API end points used in this application
 * Responses of each API call is mapped into a global {@link Result<T>} object.
 *
 * Refer {@see ResultCall} class for Global response handling
 */
interface ApiEndPointService {
    @GET("facts.json")
    suspend fun getList(): Response<CountryListResponse>
}