package com.lediya.infosys.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Store the response data in the model class*/

@Entity
data class CountryListResponse(
    val rows: List<Row>,
    @PrimaryKey val title: String
)

data class Row(
    val description: String?,
    val imageHref: String?,
    val title: String?
)