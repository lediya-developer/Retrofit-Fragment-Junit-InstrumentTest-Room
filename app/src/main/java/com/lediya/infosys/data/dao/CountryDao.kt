package com.lediya.infosys.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lediya.infosys.model.CountryListResponse


@Dao
interface CountryDao {
    /**
     * Get the country list in the database*/
    @Query("SELECT * FROM CountryListResponse")
    suspend fun getCountryData(): CountryListResponse
    /**
     * Store the country list in the database*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCountryData(countryData: CountryListResponse)

}