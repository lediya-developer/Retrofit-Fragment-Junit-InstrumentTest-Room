package com.lediya.infosys.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lediya.infosys.model.Row

/**
 * Data converter class
 */
class DataConverter {
    private var gson = Gson()

    @TypeConverter
    fun stringToSomeObjectList(data: String?): List<Row> {
        if (data == null) {
            return ArrayList()
        }

        val listType = object : TypeToken<List<Row>>() {}.type

        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun someObjectListToString(someObjects: List<Row>?): String? {

        if (someObjects == null) {
            return null
        }
        return gson.toJson(someObjects)
    }

}
